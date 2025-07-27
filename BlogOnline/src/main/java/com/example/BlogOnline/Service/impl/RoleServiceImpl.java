package com.example.BlogOnline.Service.impl;


import com.example.BlogOnline.DTO.PermissionDTO;
import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Repository.PermissionRepo;
import com.example.BlogOnline.Repository.RoleRepository;
import com.example.BlogOnline.Service.interfaces.IRoleService;
import com.example.BlogOnline.mapeos.IRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
   private IRoleMapper roleMapper;

    @Override
    public RoleDTO create(RoleDTO dto) {
        Role role = roleMapper.toRole(dto);

        Set<Permission> permissions = dto.getPermissions().stream()
                .map(p -> permissionRepo.findById(p.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        role.setPermissions(permissions);

        if(role.getPermissions().isEmpty()){
            throw  new RuntimeException("El Role debe tener al menos un permiso");
        }


        Role saved = roleRepository.save(role);
        return roleMapper.toRoleDTO(saved);
    }

    @Override
    public RoleDTO getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return roleMapper.toRoleDTO(role);
    }

    @Override
    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO update(Long id, RoleDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (dto.getName() != null) {
            role.setName(dto.getName());
        }

        role.setPermissions(
                dto.getPermissions().stream()
                        .map(p -> permissionRepo.findById(p.getId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet())
        );

        if(role.getPermissions().isEmpty()){
            throw  new RuntimeException("El Role debe tener al menos un permiso");
        }

        if (dto.isEnabled() != role.isEnabled()) {
            role.setEnabled(dto.isEnabled());
        }

        Role updated = roleRepository.save(role);
        return roleMapper.toRoleDTO(updated);
    }


    @Override
    public void delete(Long id) {

        Role role = roleRepository.getById(id);
        role.setEnabled(false);

        roleRepository.save(role);
    }

}
