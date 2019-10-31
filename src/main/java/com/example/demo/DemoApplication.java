package com.example.demo;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderDiscount;
import com.example.demo.domain.OrderFullList;
import com.example.demo.domain.OrderItem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpMethod;
import org.springframework.integration.aggregator.MessageGroupProcessor;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.http.config.EnableIntegrationGraphController;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.jdbc.JdbcMessageHandler;
import org.springframework.integration.store.MessageGroup;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan("com.example.demo")
@EnableIntegrationGraphController(allowedOrigins = "http://localhost:8082")
@ImportResource("classpath:hsql_cfg.xml")
public class DemoApplication {

    private static final double BOOK_DISCOUNT = 0.95;
    private static final double CD_DISCOUNT = 0.9;
    private static final double POSTCARD_DISCOUNT = 0.85;

    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);
    }


    @Bean
    public MessageHandler jdbcMessageHandler(DataSource dataSource) {
        JdbcMessageHandler handler = new JdbcMessageHandler(dataSource,
                "insert into data (ORDER_ID, DISCOUNT_TOTAL, ITEM_LIST) values (:payload.orderId, :payload.discountTotal, :payload.itemList)");
        return handler;
    }

    @Bean
    public IntegrationFlow fullFlow(MessageHandler jdbcMessageHandler) {
        ThreadPoolTaskExecutor execu = new ThreadPoolTaskExecutor();
        execu.setMaxPoolSize(100);
        execu.setQueueCapacity(50);
        execu.initialize();
        return IntegrationFlows.from(Http.inboundChannelAdapter("/api/passJson")
                .requestMapping(m -> {
                    m.methods(HttpMethod.PUT);
                    m.consumes("application/json");
                })
                .requestPayloadType(OrderFullList.class))
                .split(OrderFullList.class, OrderFullList::getOrderList)
                .channel(MessageChannels.executor(execu))
                .enrichHeaders(h -> h.headerExpression("orderId", "payload.orderId"))
                .split(Order.class, Order::getOrderItems, s -> s.applySequence(true))
                .channel(MessageChannels.executor(execu))
                .<OrderItem, String>route(OrderItem::getItemType, m -> m
                        .subFlowMapping("BOOK", sf -> sf.<OrderItem>handle((p, h) -> {
                            p.setPrice(p.getPrice() * BOOK_DISCOUNT);
                            return p;
                        }))
                        .subFlowMapping("CD", sf -> sf.<OrderItem>handle((p, h) -> {
                            p.setPrice(p.getPrice() * CD_DISCOUNT);
                            return p;
                        }))
                        .subFlowMapping("POSTCARD", sf -> sf.<OrderItem>handle((p, h) -> {
                            p.setPrice(p.getPrice() * POSTCARD_DISCOUNT);
                            return p;
                        })))
                .aggregate(aggregator -> aggregator.outputProcessor(
                        new MessageGroupProcessor() {
                            @Override
                            public Object processMessageGroup(MessageGroup mg) {

                                List<Message<?>> messageList = new ArrayList<>(mg.getMessages());
                                List<String> itemList = new ArrayList<>();
                                OrderDiscount toReturn = new OrderDiscount();
                                double discountTotal = 0;
                                if (mg.size() != 0) {
                                    for (Message<?> partialResult : messageList) {
                                        OrderItem thisOrderItem = (OrderItem) partialResult.getPayload();
                                        discountTotal += thisOrderItem.getPrice();
                                        itemList.add(thisOrderItem.getItemName());
                                    }
                                }
                                toReturn.setOrderId((Integer) messageList.get(0).getHeaders().get("orderId"));
                                toReturn.setItemList(itemList.toString());
                                toReturn.setDiscountTotal(discountTotal);
                                return toReturn;
                            }
                        }))
                .handle(jdbcMessageHandler)
                .get();
    }


}



