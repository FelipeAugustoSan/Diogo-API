package com.example.diogoapi.repository;

import com.example.diogoapi.entity.Funcionario;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class FuncionarioRepository {

    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong();

    public synchronized Funcionario save(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            funcionario.setId(idGenerator.incrementAndGet());
            funcionarios.add(funcionario);
        }
        return funcionario;
    }

    public synchronized List<Funcionario> findAll() {
        return List.copyOf(funcionarios);
    }

    public synchronized Optional<Funcionario> findById(Long id) {
        return funcionarios.stream()
                .filter(funcionario -> Objects.equals(funcionario.getId(), id))
                .findFirst();
    }

    public synchronized boolean replace(Funcionario atualizado) {
        for (int i = 0; i < funcionarios.size(); i++) {
            if (Objects.equals(funcionarios.get(i).getId(), atualizado.getId())) {
                funcionarios.set(i, atualizado);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean existsByEmail(String email, Long ignoredId) {
        if (email == null) {
            return false;
        }
        return funcionarios.stream()
                .filter(funcionario -> !Objects.equals(funcionario.getId(), ignoredId))
                .anyMatch(funcionario -> email.equalsIgnoreCase(funcionario.getEmail()));
    }

    public synchronized boolean deleteById(Long id) {
        return funcionarios.removeIf(funcionario -> Objects.equals(funcionario.getId(), id));
    }
}
