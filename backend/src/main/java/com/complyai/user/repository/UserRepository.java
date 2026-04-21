package com.complyai.user.repository;

import com.complyai.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAllByTenantId(Long tenantId);

    Optional<User> findByIdAndTenantId(Long id, Long tenantId);
}
