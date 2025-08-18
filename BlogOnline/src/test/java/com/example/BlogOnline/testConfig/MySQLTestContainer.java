package com.example.BlogOnline.testConfig;

import org.testcontainers.containers.MySQLContainer;

public class MySQLTestContainer {

    private static final MySQLContainer<?> container;

    static {
        container = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true); // ¡Importante!

        container.start();
    }

    public static MySQLContainer<?> getInstance() {
        return container;
    }


}
