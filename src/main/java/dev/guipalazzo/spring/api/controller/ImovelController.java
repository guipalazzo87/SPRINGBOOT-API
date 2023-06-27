package dev.guipalazzo.spring.api.controller;

import dev.guipalazzo.spring.api.domain.Imovel;
import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.request.CadastrarImovelRequest;
import dev.guipalazzo.spring.api.service.ServiceLayer;
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
@RequestMapping("/imoveis")
public class ImovelController {

    private final ServiceLayer serviceLayer;

    public ImovelController(ServiceLayer serviceLayer) {
        this.serviceLayer = serviceLayer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Imovel salvar(@RequestBody @Valid CadastrarImovelRequest cadastroImovelRequest) {
        return serviceLayer.salvarImovel(cadastroImovelRequest);
    }

    @GetMapping
    public ResponseEntity<Page<Imovel>> listarTodos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "identificacao,asc") String[] sort
    ) {
        List<Sort.Order> ordenacao = getSort(sort);
        Page<Imovel> lista = serviceLayer.listarTodosImovel(page, size, ordenacao);
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
        Page<Imovel> lista = serviceLayer.listarPorProprietario(page, size, ordenacao, idProprietario);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "/{idImovel}")
    @ResponseStatus(HttpStatus.OK)
    public Imovel listarUmPorId(@PathVariable Long idImovel) {
        return serviceLayer.listarImovelPorId(idImovel).orElseThrow(() -> new ObjetoNaoEncontradoPorIdException(Imovel.class.getSimpleName(), idImovel));
    }

    @DeleteMapping(value = "/{idImovel}")
    @ResponseStatus(HttpStatus.OK)
    public void deletar(@PathVariable Long idImovel) {
        serviceLayer.deletarImovel(idImovel);
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
