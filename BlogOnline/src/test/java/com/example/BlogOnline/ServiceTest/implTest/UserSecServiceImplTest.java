package com.example.BlogOnline.ServiceTest.implTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Collectors;

import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.DTO.UserSecDTO;
import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Repository.RoleRepository;
import com.example.BlogOnline.Repository.UserSecRepo;
import com.example.BlogOnline.Service.impl.UserSecServiceImpl;
import com.example.BlogOnline.mapeos.IUserSecMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserSecServiceImplTest {

    @Mock
    private UserSecRepo userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private IUserSecMapper userSecMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserSecServiceImpl userSecService;

    private UserSecDTO userSecDTO;
    private UserSec userSec;
    private Role role1, role2;
    private RoleDTO roleDto1,  roleDto2;
    private Set<Role> roles;
    private Set<RoleDTO> rolesdto;

    @BeforeEach
    void setUp() {
        // Setup roles
        role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        roles = Set.of(role1, role2);


        roleDto1 = new RoleDTO();
        roleDto1.setId(1L);
        roleDto1.setName("ROLE_USER");

        roleDto2 = new RoleDTO();
        roleDto2.setId(2L);
        roleDto2.setName("ROLE_ADMIN");

        rolesdto = Set.of(roleDto1, roleDto2);

        // Setup UserSecDTO
        userSecDTO = new UserSecDTO();
        userSecDTO.setId(1L);
        userSecDTO.setUsername("testuser");
        userSecDTO.setPassword("password123");
        userSecDTO.setEnabled(true);
        userSecDTO.setAccountNonExpired(true);
        userSecDTO.setAccountNonLocked(true);
        userSecDTO.setCredentialsNonExpired(true);
        userSecDTO.setRoles(rolesdto);

        // Setup UserSec
        userSec = new UserSec();
        userSec.setId(1L);
        userSec.setUsername("testuser");
        userSec.setPassword("password123");
        userSec.setEnabled(true);
        userSec.setAccountNonExpired(true);
        userSec.setAccountNonLocked(true);
        userSec.setCredentialsNonExpired(true);
        userSec.setRoles(roles);
    }

    @Test
    void create_ShouldCreateUserSuccessfully() {
        // Given
        String encodedPassword = "encodedPassword123";
        UserSec savedUser = new UserSec();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword(encodedPassword);
        savedUser.setEnabled(true);
        savedUser.setRoles(roles);

        when(userSecMapper.toEntity(userSecDTO)).thenReturn(userSec);
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role2));
        when(userRepository.save(any(UserSec.class))).thenReturn(savedUser);
        when(userSecMapper.toDTO(savedUser)).thenReturn(userSecDTO);

        // When
        UserSecDTO result = userSecService.create(userSecDTO);

        // Then
        assertNotNull(result);
        assertEquals(userSecDTO.getUsername(), result.getUsername());
        verify(userSecMapper).toEntity(userSecDTO);
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findById(1L);
        verify(roleRepository).findById(2L);
        verify(userRepository).save(any(UserSec.class));
        verify(userSecMapper).toDTO(savedUser);
    }

    @Test
    void create_ShouldHandleNonExistentRoles() {
        // Given
        when(userSecMapper.toEntity(userSecDTO)).thenReturn(userSec);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.findById(2L)).thenReturn(Optional.empty()); // Role not found
        when(userRepository.save(any(UserSec.class))).thenReturn(userSec);
        when(userSecMapper.toDTO(any(UserSec.class))).thenReturn(userSecDTO);

        // When
        UserSecDTO result = userSecService.create(userSecDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getRoles().size() == 1));
    }

    @Test
    void getById_ShouldReturnUserWhenExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(userSec));
        when(userSecMapper.toDTO(userSec)).thenReturn(userSecDTO);

        // When
        UserSecDTO result = userSecService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(userSecDTO.getId(), result.getId());
        assertEquals(userSecDTO.getUsername(), result.getUsername());
        verify(userRepository).findById(1L);
        verify(userSecMapper).toDTO(userSec);
    }

    @Test
    void getById_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userSecService.getById(1L));
        assertEquals("Usuario not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userSecMapper, never()).toDTO(any());
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        // Given
        List<UserSec> users = Arrays.asList(userSec);
        when(userRepository.findAll()).thenReturn(users);
        when(userSecMapper.toDTO(userSec)).thenReturn(userSecDTO);

        // When
        List<UserSecDTO> result = userSecService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userSecDTO.getUsername(), result.get(0).getUsername());
        verify(userRepository).findAll();
        verify(userSecMapper).toDTO(userSec);
    }

    @Test
    void getAll_ShouldReturnEmptyListWhenNoUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<UserSecDTO> result = userSecService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void update_ShouldUpdateAllFields() {
        // Given
        UserSecDTO updateDTO = new UserSecDTO();
        updateDTO.setUsername("updateduser");
        updateDTO.setPassword("newpassword");
        updateDTO.setEnabled(false);
        updateDTO.setAccountNonExpired(false);
        updateDTO.setAccountNonLocked(false);
        updateDTO.setCredentialsNonExpired(false);
        updateDTO.setRoles(Set.of(roleDto1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(userSec));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(userRepository.save(userSec)).thenReturn(userSec);
        when(userSecMapper.toDTO(userSec)).thenReturn(updateDTO);

        // When
        UserSecDTO result = userSecService.update(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository).save(userSec);
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    void update_ShouldOnlyUpdateNonNullFields() {
        // Given
        UserSecDTO partialUpdateDTO = new UserSecDTO();
        partialUpdateDTO.setUsername("updateduser");
        partialUpdateDTO.setRoles(Set.of(roleDto1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(userSec));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(userRepository.save(userSec)).thenReturn(userSec);
        when(userSecMapper.toDTO(userSec)).thenReturn(partialUpdateDTO);

        // When
        UserSecDTO result = userSecService.update(1L, partialUpdateDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository).save(userSec);
        verify(passwordEncoder, never()).encode(anyString()); // Password shouldn't be encoded if null
    }

    @Test
    void update_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userSecService.update(1L, userSecDTO));
        assertEquals("Usuario not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowExceptionWhenRolesEmpty() {
        // Given
        UserSecDTO updateDTO = new UserSecDTO();
        updateDTO.setUsername("updateduser");
        updateDTO.setRoles(Collections.emptySet());

        when(userRepository.findById(1L)).thenReturn(Optional.of(userSec));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userSecService.update(1L, updateDTO));
        assertEquals("Roles cannot be empty", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_ShouldHandleEnabledFalse() {
        // Given
        UserSecDTO updateDTO = new UserSecDTO();
        updateDTO.setEnabled(false);
        updateDTO.setRoles(Set.of(roleDto1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(userSec));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(userRepository.save(userSec)).thenReturn(userSec);
        when(userSecMapper.toDTO(userSec)).thenReturn(updateDTO);

        // When
        UserSecDTO result = userSecService.update(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> !user.isEnabled()));
    }

    @Test
    void delete_ShouldDisableUser() {
        // Given
        when(userRepository.getById(1L)).thenReturn(userSec);
        when(userRepository.save(userSec)).thenReturn(userSec);

        // When
        userSecService.delete(1L);

        // Then
        verify(userRepository).getById(1L);
        verify(userRepository).save(argThat(user -> !user.isEnabled()));
    }

    @Test
    void delete_ShouldHandleUserNotFound() {
        // Given
        when(userRepository.getById(1L)).thenThrow(new RuntimeException("User not found"));

        // When & Then
        assertThrows(RuntimeException.class, () -> userSecService.delete(1L));
        verify(userRepository).getById(1L);
        verify(userRepository, never()).save(any());
    }
}
