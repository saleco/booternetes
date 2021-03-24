package com.github.saleco.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    RouteLocator gateway(RouteLocatorBuilder rlb){
        return rlb
          .routes()
          .route(rs -> rs.path("/proxy").and().host("*.spring.io")
            .filters(fs -> fs.setPath("/customers"))
            .uri("http://localhost:8080/")
          )
          .build();
    }

    @Bean
    RSocketRequester rSocket(RSocketRequester.Builder builder) {
        return builder.tcp("localhost", 8181);
    }

    @Bean
    WebClient http(WebClient.Builder builder) {
        return builder.build();
    }
}
