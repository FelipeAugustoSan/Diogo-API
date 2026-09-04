package com.example.diogoapi.service;

import com.example.diogoapi.dto.FuncionarioPatchRequest;
import com.example.diogoapi.dto.FuncionarioRequest;
import com.example.diogoapi.dto.FuncionarioResponse;
import com.example.diogoapi.dto.IndicadoresResponse;
import com.example.diogoapi.entity.Funcionario;
import com.example.diogoapi.entity.StatusCandidatura;
import com.example.diogoapi.mapper.FuncionarioMapper;
import com.example.diogoapi.repository.FuncionarioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FuncionarioService {

    private static final Logger log = LoggerFactory.getLogger(FuncionarioService.class);

    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioMapper funcionarioMapper;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
    }

    public List<FuncionarioResponse> findAll() {
        return funcionarioMapper.toResponseList(funcionarioRepository.findAll());
    }

    public FuncionarioResponse findById(Long id) {
        return funcionarioMapper.toResponse(buscarOuFalhar(id));
    }

    public FuncionarioResponse create(FuncionarioRequest request) {
        garantirEmailDisponivel(request.email(), null);

        Funcionario funcionario = funcionarioMapper.toEntity(request);
        aplicarStatusPadrao(funcionario);

        Funcionario saved = funcionarioRepository.save(funcionario);
        log.info("Funcionario {} cadastrado com o id {}", saved.getNome(), saved.getId());

        return funcionarioMapper.toResponse(saved);
    }

    public FuncionarioResponse update(Long id, FuncionarioRequest request) {
        buscarOuFalhar(id);
        garantirEmailDisponivel(request.email(), id);

        Funcionario atualizado = funcionarioMapper.toEntity(request);
        atualizado.setId(id);
        aplicarStatusPadrao(atualizado);

        funcionarioRepository.substituir(atualizado);
        log.info("Funcionario {} atualizado por completo", id);

        return funcionarioMapper.toResponse(atualizado);
    }

    public FuncionarioResponse patch(Long id, FuncionarioPatchRequest request) {
        Funcionario existente = buscarOuFalhar(id);

        Funcionario atualizado = existente.toBuilder().build();
        funcionarioMapper.patchEntity(request, atualizado);

        funcionarioRepository.substituir(atualizado);
        log.info("Funcionario {} atualizado parcialmente", id);

        return funcionarioMapper.toResponse(atualizado);
    }

    public void delete(Long id) {
        if (!funcionarioRepository.deleteById(id)) {
            throw naoEncontrado(id);
        }
        log.info("Funcionario {} excluido", id);
    }

    public IndicadoresResponse getIndicadores() {
        List<Funcionario> todos = funcionarioRepository.findAll();

        return new IndicadoresResponse(
                todos.size(),
                contarPorStatus(todos, StatusCandidatura.EM_ANALISE),
                contarPorStatus(todos, StatusCandidatura.APROVADO),
                contarPorStatus(todos, StatusCandidatura.REPROVADO),
                contarPorStatus(todos, StatusCandidatura.CONTRATADO));
    }

    private Funcionario buscarOuFalhar(Long id) {
        return funcionarioRepository.findById(id).orElseThrow(() -> naoEncontrado(id));
    }

    private void garantirEmailDisponivel(String email, Long ignoredId) {
        if (funcionarioRepository.existsByEmail(email, ignoredId)) {
            log.warn("Tentativa de usar o e-mail ja cadastrado {}", email);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ja existe um funcionario cadastrado com o e-mail " + email + ". Informe outro e-mail.");
        }
    }

    private ResponseStatusException naoEncontrado(Long id) {
        log.warn("Funcionario {} nao encontrado", id);
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Funcionario nao encontrado para o id " + id + ". Verifique a lista de funcionarios cadastrados.");
    }

    private void aplicarStatusPadrao(Funcionario funcionario) {
        if (funcionario.getStatus() == null) {
            funcionario.setStatus(StatusCandidatura.padrao());
        }
    }

    private long contarPorStatus(List<Funcionario> funcionarios, StatusCandidatura status) {
        return funcionarios.stream()
                .filter(funcionario -> funcionario.getStatus() == status)
                .count();
    }
}
