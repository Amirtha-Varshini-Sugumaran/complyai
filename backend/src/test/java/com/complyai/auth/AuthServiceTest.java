package com.complyai.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.complyai.audit.service.AuditLogService;
import com.complyai.auth.dto.AuthResponse;
import com.complyai.auth.dto.LoginRequest;
import com.complyai.auth.service.AuthService;
import com.complyai.config.ApplicationProperties;
import com.complyai.security.JwtService;
import com.complyai.tenantmanagement.entity.Tenant;
import com.complyai.user.entity.Role;
import com.complyai.user.entity.User;
import com.complyai.user.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuditLogService auditLogService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        auditLogService = Mockito.mock(AuditLogService.class);
        ApplicationProperties properties = new ApplicationProperties(
                new ApplicationProperties.SecurityProperties(new ApplicationProperties.JwtProperties("change-me-change-me-change-me-change-me", 120)),
                new ApplicationProperties.CorsProperties(java.util.List.of("http://localhost:5173")),
                new ApplicationProperties.AiProperties("mock"),
                new ApplicationProperties.ComplianceProperties(new ApplicationProperties.CheckerProperties(true)),
                new ApplicationProperties.LoggingProperties(true)
        );
        authService = new AuthService(userRepository, passwordEncoder, new JwtService(properties), properties, auditLogService);
    }

    @Test
    void loginShouldReturnTokenForValidCredentials() {
        User user = buildUser();
        when(userRepository.findByEmailIgnoreCase("tenantadmin@northwind.dev")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hash")).thenReturn(true);

        AuthResponse response = authService.login(new LoginRequest("tenantadmin@northwind.dev", "password"));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.roles()).contains("ROLE_TENANT_ADMIN");
    }

    @Test
    void loginShouldRejectInvalidPassword() {
        User user = buildUser();
        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("tenantadmin@northwind.dev", "wrong")))
                .hasMessageContaining("Invalid email or password");
    }

    private User buildUser() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Northwind");
        tenant.setSlug("northwind");
        tenant.setStatus("ACTIVE");
        Role role = new Role();
        role.setId(2L);
        role.setName("ROLE_TENANT_ADMIN");
        User user = new User();
        user.setId(2L);
        user.setTenant(tenant);
        user.setFirstName("Nora");
        user.setLastName("Admin");
        user.setEmail("tenantadmin@northwind.dev");
        user.setPasswordHash("hash");
        user.setStatus("ACTIVE");
        user.setRoles(Set.of(role));
        return user;
    }
}
