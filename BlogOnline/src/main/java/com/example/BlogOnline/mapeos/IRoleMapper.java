package com.example.BlogOnline.mapeos;


import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.Model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IRoleMapper {

    Role toRole(RoleDTO roleDTO);
    RoleDTO toRoleDTO(Role role);


}
