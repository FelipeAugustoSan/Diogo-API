package com.example.diogoapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.diogoapi.dto.FuncionarioPatchRequest;
import com.example.diogoapi.dto.FuncionarioRequest;
import com.example.diogoapi.dto.FuncionarioResponse;
import com.example.diogoapi.dto.IndicadoresResponse;
import com.example.diogoapi.entity.StatusCandidatura;
import com.example.diogoapi.service.FuncionarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponse>> findAll(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) StatusCandidatura status) {
        return ResponseEntity.ok(funcionarioService.findAll(nome, cargo, status));
    }

    @GetMapping("/indicadores")
    public ResponseEntity<IndicadoresResponse> getIndicadores() {
        return ResponseEntity.ok(funcionarioService.getIndicadores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(funcionarioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponse> create(@RequestBody @Valid FuncionarioRequest request) {
        FuncionarioResponse response = funcionarioService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> update(@PathVariable Long id,
                                                     @RequestBody @Valid FuncionarioRequest request) {
        return ResponseEntity.ok(funcionarioService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> patch(@PathVariable Long id,
                                                     @RequestBody @Valid FuncionarioPatchRequest request) {
        return ResponseEntity.ok(funcionarioService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        funcionarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
