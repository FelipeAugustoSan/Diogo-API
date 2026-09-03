package com.example.diogoapi.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.diogoapi.dto.FuncionarioPatchRequest;
import com.example.diogoapi.dto.FuncionarioRequest;
import com.example.diogoapi.dto.FuncionarioResponse;
import com.example.diogoapi.entity.Funcionario;

@Mapper(componentModel = "spring")
public interface FuncionarioMapper {

    @Mapping(target = "id", ignore = true)
    Funcionario toEntity(FuncionarioRequest request);

    FuncionarioResponse toResponse(Funcionario funcionario);

    List<FuncionarioResponse> toResponseList(List<Funcionario> funcionarios);

    @Mapping(target = "id", ignore = true)
    void updateEntity(FuncionarioRequest request, @MappingTarget Funcionario funcionario);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void patchEntity(FuncionarioPatchRequest request, @MappingTarget Funcionario funcionario);
}
