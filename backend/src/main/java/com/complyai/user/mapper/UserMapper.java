package com.complyai.user.mapper;

import com.complyai.user.dto.UserDto;
import com.complyai.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getTenant() == null ? null : user.getTenant().getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getStatus(),
                user.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toSet())
        );
    }
}
