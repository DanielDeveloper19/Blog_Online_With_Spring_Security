package com.example.BlogOnline.Service.impl;

import com.example.BlogOnline.DTO.PermissionDTO;
import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Repository.PermissionRepo;
import com.example.BlogOnline.Service.interfaces.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionRepo permissionRepository;

    @Override
    public PermissionDTO create(PermissionDTO dto) {
        Permission perm = new Permission();
        perm.setName(dto.getName());
        perm.setEnabled(dto.isEnabled());
        Permission saved = permissionRepository.save(perm);
        return new PermissionDTO(saved.getId(), saved.getName(), saved.isEnabled());
    }

    @Override
    public PermissionDTO getById(Long id) {
        Permission perm = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        return new PermissionDTO(perm.getId(), perm.getName(), perm.isEnabled());
    }

    @Override
    public List<PermissionDTO> getAll() {
        return permissionRepository.findAll().stream()
                .map(p -> new PermissionDTO(p.getId(), p.getName(), p.isEnabled()))
                .collect(Collectors.toList());
    }

    @Override
    public PermissionDTO update(Long id, PermissionDTO dto) {
        Permission perm = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        perm.setName(dto.getName());
        perm.setEnabled(dto.isEnabled());
        Permission updated = permissionRepository.save(perm);
        return new PermissionDTO(updated.getId(), updated.getName(),  updated.isEnabled());
    }

    @Override
    public void delete(Long id) {

        Permission permission = permissionRepository.getById(id);
permission.setEnabled(false);

permissionRepository.save(permission);
    }
}
