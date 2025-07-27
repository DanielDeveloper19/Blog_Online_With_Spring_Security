package com.example.BlogOnline.Service.interfaces;


import com.example.BlogOnline.DTO.RoleDTO;

import java.util.List;

public interface IRoleService {


    RoleDTO create(RoleDTO roleDTO);
    RoleDTO getById(Long id);
    List<RoleDTO> getAll();
    RoleDTO update(Long id, RoleDTO roleDTO);
    void delete(Long id);
}
