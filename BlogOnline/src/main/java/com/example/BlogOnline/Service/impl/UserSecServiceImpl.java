package com.example.BlogOnline.Service.impl;

import com.example.BlogOnline.DTO.PermissionDTO;
import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.DTO.UserSecDTO;
import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Repository.RoleRepository;
import com.example.BlogOnline.Repository.UserSecRepo;
import com.example.BlogOnline.Service.interfaces.IUserSecService;
import com.example.BlogOnline.mapeos.IUserSecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserSecServiceImpl implements IUserSecService {

    //Crear el mapper de DTO a Entity y vicebersa


    @Autowired
    private UserSecRepo userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private IUserSecMapper userSecMapper;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public UserSecDTO create(UserSecDTO dto) {

        UserSec user = userSecMapper.toEntity(dto);

        String passwordBycrpt = passwordEncoder.encode(user.getPassword());

        user.setPassword(passwordBycrpt);

        Set<Role> roles = dto.getRoles().stream()
                .map(r -> roleRepository.findById(r.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        user.setRoles(roles);

        UserSec saved = userRepository.save(user);
        return userSecMapper.toDTO(saved);
    }

    @Override
    public UserSecDTO getById(Long id) {
        UserSec user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));
        return userSecMapper.toDTO(user);
    }

    @Override
    public List<UserSecDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userSecMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public UserSecDTO update(Long id, UserSecDTO dto) {
        UserSec user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        if (dto.getUsername() != null) {user.setUsername(dto.getUsername());}

        if (dto.getPassword() != null) {

            String passwordBycrpt = passwordEncoder.encode(dto.getPassword());
            user.setPassword(passwordBycrpt);}

        if (dto.getEnabled() == false) {
            user.setEnabled(dto.getEnabled());
        }
        if (dto.getAccountNonExpired() != null) {
            user.setAccountNonExpired(dto.getAccountNonExpired());
        }
        if (dto.getAccountNonLocked() != null) {
            user.setAccountNonLocked(dto.getAccountNonLocked());
        }
        if (dto.getCredentialsNonExpired() != null) {
            user.setCredentialsNonExpired(dto.getCredentialsNonExpired());
        }

        if (dto.getRoles().isEmpty()) {throw new RuntimeException("Roles cannot be empty");}

                 user.setRoles(dto.getRoles().stream()
                         .map(r -> roleRepository.findById(r.getId()))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .collect(Collectors.toSet()));

        userRepository.save(user);
        return getById(id);
    }

    @Override
    public void delete(Long id) {

        UserSec userSec = userRepository.getById(id);
        userSec.setEnabled(false);

        userRepository.save(userSec);
    }


}
