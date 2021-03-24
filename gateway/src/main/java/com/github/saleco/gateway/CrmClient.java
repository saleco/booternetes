package com.github.saleco.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class CrmClient {

    private final RSocketRequester rSocket;
    private final WebClient http;


    Flux<Customer> getCustomers() {
        return this.http.get().uri("http://localhost:8080/customers").retrieve().bodyToFlux(Customer.class)
          .retryWhen(Retry.backoff(10, Duration.ofSeconds(10)))
          .onErrorResume(ex -> Flux.empty())
          .timeout(Duration.ofSeconds(10));
    }

    Flux<Order> getOrdersFor(Integer customerId) {
        return this.rSocket.route("orders.{cid}", customerId).retrieveFlux(Order.class)
          .retryWhen(Retry.backoff(10, Duration.ofSeconds(10)))
          .onErrorResume(ex -> Flux.empty())
          .timeout(Duration.ofSeconds(10));
    }

    Flux<CustomerOrders> getCustomerOrders() {
        return this.getCustomers().flatMap(customer -> Mono.zip(
                Mono.just(customer),
                getOrdersFor(customer.getId()).collectList()
            )).map( tuple -> new CustomerOrders(tuple.getT1(), tuple.getT2()));
    }
}
