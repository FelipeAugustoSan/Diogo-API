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
import java.util.Locale;
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

    public List<FuncionarioResponse> findAll(String nome, String cargo, StatusCandidatura status) {
        List<Funcionario> encontrados = funcionarioRepository.findAll().stream()
                .filter(funcionario -> contains(funcionario.getNome(), nome))
                .filter(funcionario -> contains(funcionario.getCargo(), cargo))
                .filter(funcionario -> status == null || status == funcionario.getStatus())
                .toList();

        return funcionarioMapper.toResponseList(encontrados);
    }

    public FuncionarioResponse findById(Long id) {
        return funcionarioMapper.toResponse(getOrFail(id));
    }

    public FuncionarioResponse create(FuncionarioRequest request) {
        ensureEmailAvailable(request.email(), null);

        Funcionario funcionario = funcionarioMapper.toEntity(request);
        applyDefaultStatus(funcionario);

        Funcionario saved = funcionarioRepository.save(funcionario);
        log.info("Funcionario {} cadastrado com o id {}", saved.getNome(), saved.getId());

        return funcionarioMapper.toResponse(saved);
    }

    public FuncionarioResponse update(Long id, FuncionarioRequest request) {
        getOrFail(id);
        ensureEmailAvailable(request.email(), id);

        Funcionario atualizado = funcionarioMapper.toEntity(request);
        atualizado.setId(id);
        applyDefaultStatus(atualizado);

        funcionarioRepository.replace(atualizado);
        log.info("Funcionario {} atualizado por completo", id);

        return funcionarioMapper.toResponse(atualizado);
    }

    public FuncionarioResponse patch(Long id, FuncionarioPatchRequest request) {
        Funcionario existente = getOrFail(id);

        Funcionario atualizado = existente.toBuilder().build();
        funcionarioMapper.patchEntity(request, atualizado);

        funcionarioRepository.replace(atualizado);
        log.info("Funcionario {} atualizado parcialmente", id);

        return funcionarioMapper.toResponse(atualizado);
    }

    public void delete(Long id) {
        if (!funcionarioRepository.deleteById(id)) {
            throw notFound(id);
        }
        log.info("Funcionario {} excluido", id);
    }

    public IndicadoresResponse getIndicadores() {
        List<Funcionario> todos = funcionarioRepository.findAll();

        return new IndicadoresResponse(
                todos.size(),
                countByStatus(todos, StatusCandidatura.EM_ANALISE),
                countByStatus(todos, StatusCandidatura.APROVADO),
                countByStatus(todos, StatusCandidatura.REPROVADO),
                countByStatus(todos, StatusCandidatura.CONTRATADO));
    }

    private Funcionario getOrFail(Long id) {
        return funcionarioRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private void ensureEmailAvailable(String email, Long ignoredId) {
        if (funcionarioRepository.existsByEmail(email, ignoredId)) {
            log.warn("Tentativa de usar o e-mail ja cadastrado {}", email);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ja existe um funcionario cadastrado com o e-mail " + email + ". Informe outro e-mail.");
        }
    }

    private ResponseStatusException notFound(Long id) {
        log.warn("Funcionario {} nao encontrado", id);
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Funcionario nao encontrado para o id " + id + ". Verifique a lista de funcionarios cadastrados.");
    }

    private void applyDefaultStatus(Funcionario funcionario) {
        if (funcionario.getStatus() == null) {
            funcionario.setStatus(StatusCandidatura.padrao());
        }
    }

    private long countByStatus(List<Funcionario> funcionarios, StatusCandidatura status) {
        return funcionarios.stream()
                .filter(funcionario -> funcionario.getStatus() == status)
                .count();
    }

    private boolean contains(String valor, String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return true;
        }
        return valor != null && normalize(valor).contains(normalize(criterio));
    }

    private String normalize(String texto) {
        return texto.trim().toLowerCase(Locale.ROOT);
    }
}
