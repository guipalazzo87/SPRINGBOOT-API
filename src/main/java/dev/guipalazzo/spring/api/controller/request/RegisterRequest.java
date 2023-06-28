package dev.guipalazzo.spring.api.controller.request;

import dev.guipalazzo.spring.api.domain.Endereco;
import dev.guipalazzo.spring.api.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private LocalDate dataNascimento;
    private Endereco endereco;
    private Role role;
}
