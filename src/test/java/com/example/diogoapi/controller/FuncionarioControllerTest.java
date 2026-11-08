package com.example.diogoapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FuncionarioControllerTest {

    private static final String URL = "/funcionarios";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCadastrarFuncionarioComStatusEmAnalise() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson("Ana Souza", "ana@empresa.com")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.status").value("EM_ANALISE"));
    }

    @Test
    void deveRecusarFuncionarioSemNomeEComEmailInvalido() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson("", "email-invalido")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRecusarEmailJaCadastrado() throws Exception {
        cadastrar("Bruno Lima", "bruno@empresa.com");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson("Bruno Lima Outro", "bruno@empresa.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornarNaoEncontradoParaIdInexistente() throws Exception {
        mockMvc.perform(get(URL + "/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarSoOCargoNoPatch() throws Exception {
        String location = cadastrar("Carla Dias", "carla@empresa.com");

        mockMvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cargo": "Tech Lead"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargo").value("Tech Lead"))
                .andExpect(jsonPath("$.nome").value("Carla Dias"))
                .andExpect(jsonPath("$.email").value("carla@empresa.com"));
    }

    @Test
    void deveExcluirFuncionario() throws Exception {
        String location = cadastrar("Diego Rocha", "diego@empresa.com");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound());
    }

    private String cadastrar(String nome, String email) throws Exception {
        return mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson(nome, email)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String funcionarioJson(String nome, String email) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "cargo": "Desenvolvedor",
                  "salario": 5000
                }
                """.formatted(nome, email);
    }
}
