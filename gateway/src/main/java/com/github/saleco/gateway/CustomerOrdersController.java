package com.github.saleco.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class CustomerOrdersController {

    private final CrmClient crmClient;

    @GetMapping("/cos")
    Flux<CustomerOrders> get() {
        return this.crmClient.getCustomerOrders();
    }


}
