package com.example.BlogOnline.Service.interfaces;


import com.example.BlogOnline.DTO.PermissionDTO;

import java.util.List;

public interface IPermissionService {

    PermissionDTO create(PermissionDTO permissionDTO);
    PermissionDTO getById(Long id);
    List<PermissionDTO> getAll();
    PermissionDTO update(Long id, PermissionDTO permissionDTO);
    void delete(Long id);


}
