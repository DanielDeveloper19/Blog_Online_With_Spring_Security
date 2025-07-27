package com.example.BlogOnline.mapeos;


import com.example.BlogOnline.DTO.PosteoDTO;
import com.example.BlogOnline.Model.Posteo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IPostMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "enabled", target = "enabled")
    PosteoDTO convertToDTO(Posteo posteo);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "enabled", target = "enabled")
    Posteo convertToEntity(PosteoDTO posteoDTO);


}
