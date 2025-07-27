package com.example.BlogOnline.Controllers;

import com.example.BlogOnline.DTO.PermissionDTO;
import com.example.BlogOnline.Service.impl.PermissionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    @Autowired
    PermissionServiceImpl permissionService;

    @PostMapping("/permission/save")
    public ResponseEntity<PermissionDTO> create(@Valid @RequestBody PermissionDTO dto) {
        return ResponseEntity.ok(permissionService.create(dto));
    }

    @GetMapping("/permissionGetById/{id}")
    public ResponseEntity<PermissionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getById(id));
    }

    @GetMapping("/Permission/GetAll")
    public ResponseEntity<List<PermissionDTO>> getAll() {
        return ResponseEntity.ok(permissionService.getAll());
    }

    @PutMapping("permission/Update/{id}")
    public ResponseEntity<PermissionDTO> update(@PathVariable Long id, @Valid @RequestBody PermissionDTO dto) {
        return ResponseEntity.ok(permissionService.update(id, dto));
    }

    @DeleteMapping("permission/Delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
