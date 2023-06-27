package dev.guipalazzo.spring.api.controller;

import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.exception.NenhumUsuarioEncontradoPorCpfException;
import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.request.AtualizarUsuarioRequest;
import dev.guipalazzo.spring.api.service.ServiceLayer;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {


    private final ServiceLayer serviceLayer;

    public UsuarioController(ServiceLayer serviceLayer) {
        this.serviceLayer = serviceLayer;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario salvar(@Valid @RequestBody Usuario usuario) {
        return serviceLayer.salvarUsuario(usuario);
    }

    @GetMapping
    public ResponseEntity<Page<Usuario>> listarTodos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "nome,asc") String[] sort
    ) {
        List<Order> ordenacao = getSort(sort);
        Page<Usuario> lista = serviceLayer.listarTodosUsuarios(page, size, ordenacao);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "/{idUsuario}")
    public Usuario listarUmPorId(@PathVariable Long idUsuario) {
        return serviceLayer.listarUmUsuario(idUsuario).orElseThrow(() -> new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), idUsuario));
    }

    @PutMapping(value = "/{idUsuario}")
    public Usuario atualizarUsuario(@PathVariable Long idUsuario,
                                    @Valid @RequestBody AtualizarUsuarioRequest body) {
        return serviceLayer.atualizarUsuario(idUsuario, body);
    }

    @GetMapping(value = "/cpf/{cpf}")
    public Usuario listarUmPorCpf(@PathVariable String cpf) {
        return serviceLayer.listarUsuarioPorCpf(cpf).orElseThrow(() -> new NenhumUsuarioEncontradoPorCpfException(cpf));
    }


    // From https://www.baeldung.com/spring-boot-bean-validation
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    // https://www.bezkoder.com/spring-boot-pagination-sorting-example/
    private List<Order> getSort(String[] sort) {
        List<Order> orders = new ArrayList<>();
        try {
            if (sort[0].contains(",")) {
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }
        } catch (Exception e) {
            System.out.println("Erro no sorting");
        }
        return orders;
    }

    @SneakyThrows
    private Sort.Direction getSortDirection(String s) {
        return switch (s) {
            case "asc" -> Sort.Direction.ASC;
            case "desc" -> Sort.Direction.DESC;
            default -> throw new Exception();
        };
    }
}
