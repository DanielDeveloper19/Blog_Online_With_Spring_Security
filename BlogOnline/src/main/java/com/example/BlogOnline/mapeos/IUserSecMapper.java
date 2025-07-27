package com.example.BlogOnline.mapeos;


import com.example.BlogOnline.DTO.PermissionDTO;
import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.DTO.UserSecDTO;
import com.example.BlogOnline.Model.UserSec;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IUserSecMapper {

    UserSec toEntity(UserSecDTO dto);

    default UserSecDTO toDTO(UserSec user) {
        return UserSecDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(user.isEnabled())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .roles(user.getRoles().stream()
                        .map(r -> new RoleDTO(r.getId(), r.getName(),
                                r.getPermissions().stream()
                                        .map(p -> new PermissionDTO(p.getId(), p.getName()))
                                        .collect(Collectors.toSet())))
                        .collect(Collectors.toSet()))
                .build();

    }


}
