package com.example.BlogOnline.Controllers;


import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.Service.impl.RoleServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class RolesController {

    @Autowired
    private RoleServiceImpl roleService;

    @PostMapping("/role/save")
    public ResponseEntity<RoleDTO> create(@Valid @RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.create(dto));
    }

    @GetMapping("/role/GetById/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @GetMapping("/role/GetAll")
    public ResponseEntity<List<RoleDTO>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @PutMapping("role/update/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.update(id, dto));
    }

    @DeleteMapping("role/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
