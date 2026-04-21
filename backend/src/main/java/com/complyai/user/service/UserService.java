package com.complyai.user.service;

import com.complyai.common.exception.BusinessRuleViolationException;
import com.complyai.common.exception.ResourceNotFoundException;
import com.complyai.security.AppUserPrincipal;
import com.complyai.tenant.service.TenantAccessEvaluator;
import com.complyai.tenantmanagement.entity.Tenant;
import com.complyai.tenantmanagement.service.TenantService;
import com.complyai.user.dto.CreateUserRequest;
import com.complyai.user.dto.UserDto;
import com.complyai.user.entity.Role;
import com.complyai.user.entity.User;
import com.complyai.user.mapper.UserMapper;
import com.complyai.user.repository.RoleRepository;
import com.complyai.user.repository.UserRepository;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantService tenantService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TenantAccessEvaluator tenantAccessEvaluator;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       TenantService tenantService,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       TenantAccessEvaluator tenantAccessEvaluator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tenantService = tenantService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.tenantAccessEvaluator = tenantAccessEvaluator;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public List<UserDto> listTenantUsers(AppUserPrincipal principal) {
        return userRepository.findAllByTenantId(principal.getTenantId()).stream().map(userMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @Transactional
    public UserDto createTenantUser(CreateUserRequest request, AppUserPrincipal principal) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new BusinessRuleViolationException("Email already exists");
        }
        Tenant tenant = tenantService.getEntity(principal.getTenantId());
        User user = new User();
        user.setTenant(tenant);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setStatus(request.status());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(resolveRoles(request.roles()));
        return userMapper.toDto(userRepository.save(user));
    }

    public User getTenantUser(Long userId, Long tenantId) {
        return userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDto getCurrentUser(AppUserPrincipal principal) {
        if (principal.getRoles().contains("ROLE_SUPER_ADMIN")) {
            return userMapper.toDto(userRepository.findById(principal.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        }
        tenantAccessEvaluator.assertTenantAccess(principal, principal.getTenantId());
        return userMapper.toDto(getTenantUser(principal.getUserId(), principal.getTenantId()));
    }

    private Set<Role> resolveRoles(Set<String> names) {
        return names.stream().map(name -> roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name)))
                .collect(java.util.stream.Collectors.toSet());
    }
}
