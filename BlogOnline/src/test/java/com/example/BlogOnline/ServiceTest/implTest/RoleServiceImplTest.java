package com.example.BlogOnline.ServiceTest.implTest;


import com.example.BlogOnline.DTO.PermissionDTO;
import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Repository.PermissionRepo;
import com.example.BlogOnline.Repository.RoleRepository;
import com.example.BlogOnline.Service.impl.RoleServiceImpl;
import com.example.BlogOnline.mapeos.IRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepo permissionRepo;

    @Mock
    private IRoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RoleDTO roleDTO;
    private Role role;
    private Permission permission1;
    private Permission permission2;
    private PermissionDTO permissionDTO1;
    private PermissionDTO permissionDTO2;

    @BeforeEach
    void setUp() {
        // Setup permissions
        permission1 = new Permission();
        permission1.setId(1L);
        permission1.setName("READ");

        permission2 = new Permission();
        permission2.setId(2L);
        permission2.setName("WRITE");

        permissionDTO1 = new PermissionDTO();
        permissionDTO1.setId(1L);
        permissionDTO1.setName("READ");

        permissionDTO2 = new PermissionDTO();
        permissionDTO2.setId(2L);
        permissionDTO2.setName("WRITE");

        // Setup role
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setEnabled(true);
        role.setPermissions(Set.of(permission1, permission2));

        // Setup roleDTO
        roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setName("ADMIN");
        roleDTO.setEnabled(true);
        roleDTO.setPermissions(Set.of(permissionDTO1, permissionDTO2));
    }

    @Test
    void create_ShouldCreateRoleSuccessfully_WhenValidData() {
        // Given
        Role roleToSave = new Role();
        roleToSave.setName("ADMIN");
        roleToSave.setEnabled(true);

        when(roleMapper.toRole(roleDTO)).thenReturn(roleToSave);
        when(permissionRepo.findById(1L)).thenReturn(Optional.of(permission1));
        when(permissionRepo.findById(2L)).thenReturn(Optional.of(permission2));
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toRoleDTO(role)).thenReturn(roleDTO);

        // When
        RoleDTO result = roleService.create(roleDTO);

        // Then
        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        assertTrue(result.isEnabled());
        assertEquals(2, result.getPermissions().size());

        verify(roleMapper).toRole(roleDTO);
        verify(permissionRepo).findById(1L);
        verify(permissionRepo).findById(2L);
        verify(roleRepository).save(any(Role.class));
        verify(roleMapper).toRoleDTO(role);
    }

    @Test
    void create_ShouldThrowException_WhenNoPermissionsFound() {
        // Given
        when(roleMapper.toRole(roleDTO)).thenReturn(new Role());
        when(permissionRepo.findById(1L)).thenReturn(Optional.empty());
        when(permissionRepo.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.create(roleDTO));

        assertEquals("El Role debe tener al menos un permiso", exception.getMessage());

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void create_ShouldCreateRole_WhenSomePermissionsNotFound() {
        // Given
        Role roleToSave = new Role();
        roleToSave.setName("ADMIN");

        Role savedRole = new Role();
        savedRole.setId(1L);
        savedRole.setName("ADMIN");
        savedRole.setPermissions(Set.of(permission1));

        RoleDTO expectedDTO = new RoleDTO();
        expectedDTO.setId(1L);
        expectedDTO.setName("ADMIN");
        expectedDTO.setPermissions(Set.of(permissionDTO1));

        when(roleMapper.toRole(roleDTO)).thenReturn(roleToSave);
        when(permissionRepo.findById(1L)).thenReturn(Optional.of(permission1));
        when(permissionRepo.findById(2L)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        when(roleMapper.toRoleDTO(savedRole)).thenReturn(expectedDTO);

        // When
        RoleDTO result = roleService.create(roleDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPermissions().size());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void getById_ShouldReturnRole_WhenRoleExists() {
        // Given
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleMapper.toRoleDTO(role)).thenReturn(roleDTO);

        // When
        RoleDTO result = roleService.getById(roleId);

        // Then
        assertNotNull(result);
        assertEquals(roleId, result.getId());
        assertEquals("ADMIN", result.getName());

        verify(roleRepository).findById(roleId);
        verify(roleMapper).toRoleDTO(role);
    }

    @Test
    void getById_ShouldThrowException_WhenRoleNotFound() {
        // Given
        Long roleId = 999L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.getById(roleId));

        assertEquals("Role not found", exception.getMessage());
        verify(roleMapper, never()).toRoleDTO(any());
    }

    @Test
    void getAll_ShouldReturnAllRoles() {
        // Given
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("USER");

        RoleDTO roleDTO2 = new RoleDTO();
        roleDTO2.setId(2L);
        roleDTO2.setName("USER");

        List<Role> roles = Arrays.asList(role, role2);

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toRoleDTO(role)).thenReturn(roleDTO);
        when(roleMapper.toRoleDTO(role2)).thenReturn(roleDTO2);

        // When
        List<RoleDTO> result = roleService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        assertEquals("USER", result.get(1).getName());

        verify(roleRepository).findAll();
        verify(roleMapper, times(2)).toRoleDTO(any(Role.class));
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoRoles() {
        // Given
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<RoleDTO> result = roleService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(roleRepository).findAll();
        verify(roleMapper, never()).toRoleDTO(any());
    }

    @Test
    void update_ShouldUpdateRoleSuccessfully_WhenValidData() {
        // Given
        Long roleId = 1L;
        RoleDTO updateDTO = new RoleDTO();
        updateDTO.setName("UPDATED_ADMIN");
        updateDTO.setEnabled(false);
        updateDTO.setPermissions(Set.of(permissionDTO1));

        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("ADMIN");
        existingRole.setEnabled(true);

        Role updatedRole = new Role();
        updatedRole.setId(roleId);
        updatedRole.setName("UPDATED_ADMIN");
        updatedRole.setEnabled(false);
        updatedRole.setPermissions(Set.of(permission1));

        RoleDTO updatedDTO = new RoleDTO();
        updatedDTO.setId(roleId);
        updatedDTO.setName("UPDATED_ADMIN");
        updatedDTO.setEnabled(false);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(permissionRepo.findById(1L)).thenReturn(Optional.of(permission1));
        when(roleRepository.save(existingRole)).thenReturn(updatedRole);
        when(roleMapper.toRoleDTO(updatedRole)).thenReturn(updatedDTO);

        // When
        RoleDTO result = roleService.update(roleId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("UPDATED_ADMIN", result.getName());
        assertFalse(result.isEnabled());

        verify(roleRepository).findById(roleId);
        verify(roleRepository).save(existingRole);
        verify(roleMapper).toRoleDTO(updatedRole);
    }

    @Test
    void update_ShouldThrowException_WhenRoleNotFound() {
        // Given
        Long roleId = 999L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.update(roleId, roleDTO));

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowException_WhenNoPermissionsAfterUpdate() {
        // Given
        Long roleId = 1L;
        RoleDTO updateDTO = new RoleDTO();
        updateDTO.setName("ADMIN");
        updateDTO.setPermissions(Set.of(permissionDTO1));

        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("ADMIN");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(permissionRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.update(roleId, updateDTO));

        assertEquals("El Role debe tener al menos un permiso", exception.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void update_ShouldOnlyUpdateName_WhenOnlyNameProvided() {
        // Given
        Long roleId = 1L;
        RoleDTO updateDTO = new RoleDTO();
        updateDTO.setName("NEW_NAME");
        updateDTO.setPermissions(Set.of(permissionDTO1));
        updateDTO.setEnabled(true); // Same as existing

        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("OLD_NAME");
        existingRole.setEnabled(true);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(permissionRepo.findById(1L)).thenReturn(Optional.of(permission1));
        when(roleRepository.save(existingRole)).thenReturn(existingRole);
        when(roleMapper.toRoleDTO(existingRole)).thenReturn(updateDTO);

        // When
        roleService.update(roleId, updateDTO);

        // Then
        assertEquals("NEW_NAME", existingRole.getName());
        assertTrue(existingRole.isEnabled());
        verify(roleRepository).save(existingRole);
    }

    @Test
    void delete_ShouldDisableRole_WhenRoleExists() {
        // Given
        Long roleId = 1L;
        Role roleToDelete = new Role();
        roleToDelete.setId(roleId);
        roleToDelete.setEnabled(true);

        when(roleRepository.getById(roleId)).thenReturn(roleToDelete);

        // When
        roleService.delete(roleId);

        // Then
        assertFalse(roleToDelete.isEnabled());
        verify(roleRepository).getById(roleId);
        verify(roleRepository).save(roleToDelete);
    }

    @Test
    void delete_ShouldCallRepositoryMethods_EvenIfRoleAlreadyDisabled() {
        // Given
        Long roleId = 1L;
        Role roleToDelete = new Role();
        roleToDelete.setId(roleId);
        roleToDelete.setEnabled(false);

        when(roleRepository.getById(roleId)).thenReturn(roleToDelete);

        // When
        roleService.delete(roleId);

        // Then
        assertFalse(roleToDelete.isEnabled());
        verify(roleRepository).getById(roleId);
        verify(roleRepository).save(roleToDelete);
    }
}