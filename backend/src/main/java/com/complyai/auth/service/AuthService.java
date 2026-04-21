package com.complyai.auth.service;

import com.complyai.audit.service.AuditLogService;
import com.complyai.auth.dto.AuthResponse;
import com.complyai.auth.dto.AuthUserDto;
import com.complyai.auth.dto.LoginRequest;
import com.complyai.auth.dto.TenantSummaryDto;
import com.complyai.common.exception.BusinessRuleViolationException;
import com.complyai.common.exception.ResourceNotFoundException;
import com.complyai.config.ApplicationProperties;
import com.complyai.security.AppUserPrincipal;
import com.complyai.security.JwtService;
import com.complyai.user.entity.User;
import com.complyai.user.repository.UserRepository;
import java.time.Instant;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationProperties properties;
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       ApplicationProperties properties,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.properties = properties;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessRuleViolationException("Invalid email or password");
        }
        user.setLastLoginAt(Instant.now());
        Set<String> roles = user.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toSet());
        AppUserPrincipal principal = new AppUserPrincipal(
                user.getId(),
                user.getTenant() == null ? null : user.getTenant().getId(),
                user.getEmail(),
                user.getPasswordHash(),
                roles
        );
        String token = jwtService.generateToken(principal);
        auditLogService.log(user, "LOGIN", "USER", user.getId(), "User login");
        return new AuthResponse(
                token,
                "Bearer",
                properties.security().jwt().accessTokenExpirationMinutes() * 60,
                new AuthUserDto(user.getId(), user.getTenant() == null ? null : user.getTenant().getId(),
                        user.getFirstName(), user.getLastName(), user.getEmail(), user.getStatus()),
                roles,
                user.getTenant() == null ? null : new TenantSummaryDto(
                        user.getTenant().getId(),
                        user.getTenant().getName(),
                        user.getTenant().getSlug(),
                        user.getTenant().getStatus()
                )
        );
    }
}
