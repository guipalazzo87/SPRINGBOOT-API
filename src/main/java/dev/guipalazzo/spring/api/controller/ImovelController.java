package dev.guipalazzo.spring.api.controller;

import dev.guipalazzo.spring.api.domain.Imovel;
import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.controller.request.CadastrarImovelRequest;
import dev.guipalazzo.spring.api.service.DomainService;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/v1/imoveis")
public class ImovelController {

    private final DomainService service;

    public ImovelController(DomainService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Imovel salvar(@RequestBody @Valid CadastrarImovelRequest cadastroImovelRequest) {
        return service.salvarImovel(cadastroImovelRequest);
    }

    @GetMapping
    public ResponseEntity<Page<Imovel>> listarTodos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "identificacao,asc") String[] sort
    ) {
        List<Sort.Order> ordenacao = getSort(sort);
        Page<Imovel> lista = service.listarTodosImovel(page, size, ordenacao);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "/proprietarios/{idProprietario}")
    public ResponseEntity<Page<Imovel>> listarPorProprietario(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "identificacao,asc") String[] sort,
            @PathVariable Long idProprietario
    ) {
        List<Sort.Order> ordenacao = getSort(sort);
        Page<Imovel> lista = service.listarPorProprietario(page, size, ordenacao, idProprietario);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "/{idImovel}")
    @ResponseStatus(HttpStatus.OK)
    public Imovel listarUmPorId(@PathVariable Long idImovel) {
        return service.listarImovelPorId(idImovel).orElseThrow(() -> new ObjetoNaoEncontradoPorIdException(Imovel.class.getSimpleName(), idImovel));
    }

    @DeleteMapping(value = "/{idImovel}")
    @ResponseStatus(HttpStatus.OK)
    public void deletar(@PathVariable Long idImovel) {
        service.deletarImovel(idImovel);
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
    private List<Sort.Order> getSort(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        try {
            if (sort[0].contains(",")) {
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
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
