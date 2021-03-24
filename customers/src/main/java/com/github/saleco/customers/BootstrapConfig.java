package com.github.saleco.customers;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

@Configuration
public class BootstrapConfig {

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(DatabaseClient dbc, CustomerRepository repository) {
        return applicationReadyEvent -> {
            var ddl = dbc.sql("create table if not exists customer (id serial primary key, name varchar(255) not null)")
              .fetch()
              .rowsUpdated();

            var saved = Flux.just("A", "B", "C")
              .map(name -> new Customer(null, name))
              .flatMap(repository::save);

            ddl.thenMany(saved).subscribe(System.out::println);
        };
    }
}
