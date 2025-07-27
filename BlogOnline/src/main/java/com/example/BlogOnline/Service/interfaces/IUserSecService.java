package com.example.BlogOnline.Service.interfaces;

import com.example.BlogOnline.DTO.UserSecDTO;

import java.util.List;

public interface IUserSecService {

    UserSecDTO create(UserSecDTO userDTO);
    UserSecDTO getById(Long id);
    List<UserSecDTO> getAll();
    UserSecDTO update(Long id, UserSecDTO userDTO);
    void delete(Long id);

}
