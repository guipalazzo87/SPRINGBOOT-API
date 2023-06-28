package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.config.JwtService;
import dev.guipalazzo.spring.api.controller.request.AuthenticationRequest;
import dev.guipalazzo.spring.api.controller.request.RegisterRequest;
import dev.guipalazzo.spring.api.controller.response.AuthenticationResponse;
import dev.guipalazzo.spring.api.domain.Role;
import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final ServiceLayer serviceLayer;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = Usuario.builder()
                .nome(request.getNome())
                .cpf(request.getCpf())
                .dataNascimento(request.getDataNascimento())
                .endereco(request.getEndereco())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(Role.USER)
                .build();
        serviceLayer.salvarUsuario(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
