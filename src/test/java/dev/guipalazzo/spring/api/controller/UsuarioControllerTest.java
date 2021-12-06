package dev.guipalazzo.spring.api.controller;

import dev.guipalazzo.spring.api.domain.Endereco;
import dev.guipalazzo.spring.api.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void salvar() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.get("/usuarios");
        MvcResult result = mvc.perform(request).andReturn();
        assertEquals("expected", result.getResponse().getContentAsString());


//        UsuarioController controller = new UsuarioController();

        Usuario modelo = new Usuario(1L,
                "Nome Completo Solicitante",
                "email@servidor.com",
                "senha123",
                "123345678900",
                LocalDate.of(1980,1,1),
                new Endereco(
                        1L,
                        "99999-999",
                        "Logradouro",
                        "Numero",
                        "Complemento",
                        "Bairro",
                        "Cidade",
                        "Estado"
                ),
                "http://urlDoAvatar"
        );

//        Usuario response = controller.salvar(modelo);
//        assertEquals(modelo.getNome(), response.getNome());
    }

}