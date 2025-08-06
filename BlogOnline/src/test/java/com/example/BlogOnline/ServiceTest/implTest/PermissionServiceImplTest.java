package com.example.BlogOnline.ServiceTest.implTest;

import com.example.BlogOnline.DTO.PermissionDTO;
import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Repository.PermissionRepo;
import com.example.BlogOnline.Service.impl.PermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionServiceImpl Tests")
class PermissionServiceImplTest {

    @Mock
    private PermissionRepo permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;
    private PermissionDTO permissionDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        permission = new Permission();
        permission.setId(1L);
        permission.setName("READ_POSTS");
        permission.setEnabled(true);

        permissionDTO = new PermissionDTO(1L, "READ_POSTS", true);
    }

    @Test
    @DisplayName("Create - Should create permission successfully")
    void create_ShouldCreatePermissionSuccessfully() {
        // Given
        PermissionDTO inputDTO = new PermissionDTO(null, "READ_POSTS", true);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        // When
        PermissionDTO result = permissionService.create(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("READ_POSTS", result.getName());
        assertTrue(result.isEnabled());

        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    @DisplayName("Create - Should handle null input")
    void create_ShouldHandleNullInput() {
        // Given
        PermissionDTO inputDTO = new PermissionDTO(null, null, false);
        Permission savedPermission = new Permission();
        savedPermission.setId(2L);
        savedPermission.setName(null);
        savedPermission.setEnabled(false);

        when(permissionRepository.save(any(Permission.class))).thenReturn(savedPermission);

        // When
        PermissionDTO result = permissionService.create(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertNull(result.getName());
        assertFalse(result.isEnabled());
    }

    @Test
    @DisplayName("GetById - Should return permission when found")
    void getById_ShouldReturnPermissionWhenFound() {
        // Given
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        // When
        PermissionDTO result = permissionService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("READ_POSTS", result.getName());
        assertTrue(result.isEnabled());

        verify(permissionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GetById - Should throw exception when permission not found")
    void getById_ShouldThrowExceptionWhenPermissionNotFound() {
        // Given
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> permissionService.getById(999L));

        assertEquals("Permission not found", exception.getMessage());
        verify(permissionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("GetAll - Should return all permissions")
    void getAll_ShouldReturnAllPermissions() {
        // Given
        Permission permission2 = new Permission();
        permission2.setId(2L);
        permission2.setName("WRITE_POSTS");
        permission2.setEnabled(false);

        List<Permission> permissions = Arrays.asList(permission, permission2);
        when(permissionRepository.findAll()).thenReturn(permissions);

        // When
        List<PermissionDTO> result = permissionService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        PermissionDTO firstDTO = result.get(0);
        assertEquals(1L, firstDTO.getId());
        assertEquals("READ_POSTS", firstDTO.getName());
        assertTrue(firstDTO.isEnabled());

        PermissionDTO secondDTO = result.get(1);
        assertEquals(2L, secondDTO.getId());
        assertEquals("WRITE_POSTS", secondDTO.getName());
        assertFalse(secondDTO.isEnabled());

        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("GetAll - Should return empty list when no permissions exist")
    void getAll_ShouldReturnEmptyListWhenNoPermissionsExist() {
        // Given
        when(permissionRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<PermissionDTO> result = permissionService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update - Should update permission successfully")
    void update_ShouldUpdatePermissionSuccessfully() {
        // Given
        PermissionDTO updateDTO = new PermissionDTO(1L, "UPDATED_PERMISSION", false);
        Permission updatedPermission = new Permission();
        updatedPermission.setId(1L);
        updatedPermission.setName("UPDATED_PERMISSION");
        updatedPermission.setEnabled(false);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(updatedPermission);

        // When
        PermissionDTO result = permissionService.update(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("UPDATED_PERMISSION", result.getName());
        assertFalse(result.isEnabled());

        verify(permissionRepository, times(1)).findById(1L);
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    @DisplayName("Update - Should throw exception when permission not found")
    void update_ShouldThrowExceptionWhenPermissionNotFound() {
        // Given
        PermissionDTO updateDTO = new PermissionDTO(999L, "UPDATED_PERMISSION", false);
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> permissionService.update(999L, updateDTO));

        assertEquals("Permission not found", exception.getMessage());
        verify(permissionRepository, times(1)).findById(999L);
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    @DisplayName("Delete - Should disable permission successfully")
    void delete_ShouldDisablePermissionSuccessfully() {
        // Given
        when(permissionRepository.getById(1L)).thenReturn(permission);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        // When
        permissionService.delete(1L);

        // Then
        verify(permissionRepository, times(1)).getById(1L);
        verify(permissionRepository, times(1)).save(permission);
        assertFalse(permission.isEnabled()); // Verificar que se deshabilitó
    }

    @Test
    @DisplayName("Delete - Should handle repository exceptions")
    void delete_ShouldHandleRepositoryExceptions() {
        // Given
        when(permissionRepository.getById(1L)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> permissionService.delete(1L));
        verify(permissionRepository, times(1)).getById(1L);
        verify(permissionRepository, never()).save(any(Permission.class));
    }


}