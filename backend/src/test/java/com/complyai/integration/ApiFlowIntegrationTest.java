package com.complyai.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.complyai.audit.repository.AuditLogRepository;
import com.complyai.request.repository.DataSubjectRequestAuditRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ApiFlowIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private DataSubjectRequestAuditRepository requestAuditRepository;

    @Test
    void loginShouldReturnJwtForSeededUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"tenantadmin@northwind.dev","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.roles[0]").exists());
    }

    @Test
    void loginValidationShouldReturnConsistentErrorEnvelope() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"not-an-email","password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void tenantAdminCannotReadOtherTenantInventory() throws Exception {
        String token = login("tenantadmin@northwind.dev", "password");

        mockMvc.perform(get("/api/v1/inventory/3")
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidRequestStatusJumpIsRejected() throws Exception {
        String token = login("tenantadmin@northwind.dev", "password");
        long requestId = createRequest(token);

        mockMvc.perform(post("/api/v1/requests/{id}/transition", requestId)
                        .header(AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"COMPLETED","note":"Skipping review"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid request status transition")));
    }

    @Test
    void requestTransitionShouldPersistHistoryAndAuditLog() throws Exception {
        String token = login("tenantadmin@northwind.dev", "password");
        long requestId = createRequest(token);

        mockMvc.perform(post("/api/v1/requests/{id}/transition", requestId)
                        .header(AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_REVIEW","note":"Started compliance review"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_REVIEW"));

        assertThat(requestAuditRepository.findAllByRequestIdOrderByChangedAtDesc(requestId))
                .hasSize(1)
                .first()
                .extracting(audit -> audit.getToStatus().name())
                .isEqualTo("IN_REVIEW");

        assertThat(auditLogRepository.findAll())
                .anyMatch(log -> "REQUEST_STATUS_CHANGED".equals(log.getAction()) && requestId == log.getEntityId());
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("accessToken").asText();
    }

    private long createRequest(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/requests")
                        .header(AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "requesterEmail":"integration.subject@example.eu",
                                  "requestType":"ACCESS",
                                  "submissionDate":"%s",
                                  "dueDate":"%s",
                                  "status":"SUBMITTED",
                                  "completionNotes":null
                                }
                                """.formatted(LocalDate.now(), LocalDate.now().plusDays(30))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asLong();
    }
}
