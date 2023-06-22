package dev.guipalazzo.spring.api.controller;

import dev.guipalazzo.spring.api.domain.Anuncio;
import dev.guipalazzo.spring.api.service.AnuncioService;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/anuncios")
public class AnuncioController {

//    private final AnuncioSalvarService anuncioSalvarService;
    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {//, AnuncioSalvarService anuncioSalvarService) {
        this.anuncioService = anuncioService;
//        this.anuncioSalvarService = anuncioSalvarService;
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Anuncio salvar(@RequestBody @Valid CadastrarAnuncioRequest body) {
//        return anuncioSalvarService.salvar(body);
//    }

    @GetMapping
    public ResponseEntity<Page<Anuncio>> listarTodos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "valorDiaria,asc") String[] sort
    ) {
        List<Sort.Order> ordenacao = getSort(sort);
        Page<Anuncio> lista = anuncioService.listarTodos(page, size, ordenacao);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "/anunciantes/{idAnunciante}")
    public ResponseEntity<Page<Anuncio>> listarPorAnunciante(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "valorDiaria,asc") String[] sort,
            @PathVariable Long idAnunciante
    ) {
        List<Sort.Order> ordenacao = getSort(sort);
        Page<Anuncio> lista = anuncioService.listarPorAnunciante(page, size, ordenacao, idAnunciante);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{idAnuncio}")
    @ResponseStatus(HttpStatus.OK)
    public void deletar(@PathVariable Long idAnuncio) {
        anuncioService.deletarAnuncio(idAnuncio);
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
