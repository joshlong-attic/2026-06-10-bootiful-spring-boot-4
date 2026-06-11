package com.example.adoptions;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.util.Assert;

import javax.sql.DataSource;

@EnableResilientMethods
@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

    @Bean
    JdbcPostgresDialect jdbcPostgresDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }
}





class MyBeanRegistrar implements BeanRegistrar {

    @Override
    public void register(@NonNull BeanRegistry registry, @NonNull Environment env) {
        for (var i = 0; i < 10; i++) {
            var name = "runner" + i;
            registry.registerBean(MyRunner.class, s -> s.supplier(beans -> new MyRunner(
                    beans.bean(DataSource.class),
                    name)));
        }
    }
}

class MyRunner implements ApplicationRunner {

    private final String name;

    MyRunner(DataSource dataSource, String name) {
        this.name = name;
        Assert.notNull(dataSource, "DataSource must not be null");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IO.println("hai" + this.name + "!");
    }
}

/*
@Component
class CatFactsClient {

    private final RestClient http;

    CatFactsClient(RestClient.Builder http) {
        this.http = http.build();
    }

    CatFacts facts() {
        return this.http
                .get()
                .uri("https://www.catfacts.net/api")
                .retrieve()
                .body(CatFacts.class);
    }
}*/

