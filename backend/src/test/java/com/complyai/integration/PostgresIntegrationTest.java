package com.complyai.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.complyai.inventory.repository.DataInventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class PostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DataInventoryRepository inventoryRepository;

    @Test
    void flywayShouldSeedDataAndKeepTenantsSeparate() {
        assertThat(inventoryRepository.countByTenantId(1L)).isGreaterThan(0);
        assertThat(inventoryRepository.findAllByTenantId(2L, org.springframework.data.domain.PageRequest.of(0, 20)).getContent())
                .allMatch(record -> record.getTenant().getId().equals(2L));
    }
}
