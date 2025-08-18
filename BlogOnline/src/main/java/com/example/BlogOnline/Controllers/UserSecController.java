package com.example.BlogOnline.Controllers;


import com.example.BlogOnline.DTO.UserSecDTO;
import com.example.BlogOnline.Service.impl.UserSecServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class UserSecController {

    @Autowired
    private  UserSecServiceImpl userSecService;

    @PostMapping("/userSec/save")
    public ResponseEntity<UserSecDTO> create(@Valid @RequestBody UserSecDTO dto) {
        return ResponseEntity.ok(userSecService.create(dto));
    }

    @GetMapping("userSec/getById/{id}")
    public ResponseEntity<UserSecDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userSecService.getById(id));
    }

    @GetMapping("/userSec/getAll")
    public ResponseEntity<List<UserSecDTO>> getAll() {
        return ResponseEntity.ok(userSecService.getAll());
    }

    @PutMapping("/userSec/update/{id}")
    public ResponseEntity<UserSecDTO> update(@PathVariable Long id, @Valid @RequestBody UserSecDTO dto) {
        return ResponseEntity.ok(userSecService.update(id, dto));
    }

    @DeleteMapping("/userSec/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userSecService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
