package dev.guipalazzo.spring.api.controller;

import dev.guipalazzo.spring.api.domain.FormaPagamento;
import dev.guipalazzo.spring.api.domain.Reserva;
import dev.guipalazzo.spring.api.controller.request.CadastrarReservaRequest;
import dev.guipalazzo.spring.api.controller.response.InformacaoReservaResponse;
import dev.guipalazzo.spring.api.service.*;
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
@RequestMapping("/api/v1/reservas")
public class ReservaController {


    private final DomainService service;

    public ReservaController(DomainService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InformacaoReservaResponse salvar(@RequestBody @Valid CadastrarReservaRequest body) {
        return service.salvarReserva(body);
    }

    @GetMapping(value = "/solicitantes/{idSolicitante}")
    public ResponseEntity<Page<Reserva>> reservasPorSolicitante(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "Periodo.dataHoraFinal,desc") String[] sort,
            @PathVariable Long idSolicitante,
            @RequestParam(required = false) String dataHoraFinal,
            @RequestParam(required = false) String dataHoraInicial
    ) {
        List<Sort.Order> ordenacao = getSort(sort);

        Page<Reserva> lista = service.listarReservaPorSolicitante(page,
                size,
                ordenacao,
                idSolicitante,
                dataHoraFinal,
                dataHoraInicial
        );
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "/anuncios/anunciantes/{idAnunciante}")
    public ResponseEntity<Page<Reserva>> reservasPorAnunciante(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "Periodo.dataHoraFinal,desc") String[] sort,
            @PathVariable Long idAnunciante
    ) {
        List<Sort.Order> ordenacao = getSort(sort);

        Page<Reserva> lista = service.listarReservaPorAnunciante(page,
                size,
                ordenacao,
                idAnunciante);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping(value = "/{idReserva}/pagamentos")
    @ResponseStatus(HttpStatus.OK)
    public void pagarReserva(@PathVariable Long idReserva,
                             @RequestBody @Valid FormaPagamento formaPagamento) {
        service.pagarReserva(idReserva, formaPagamento);
    }

    @PutMapping(value = "/{idReserva}/pagamentos/cancelar")
    @ResponseStatus(HttpStatus.OK)
    public void cancelarReserva(@PathVariable Long idReserva) {
        service.cancelarReserva(idReserva);
    }

    @PutMapping(value = "/{idReserva}/pagamentos/estornar")
    @ResponseStatus(HttpStatus.OK)
    public void estornarReserva(@PathVariable Long idReserva) {
        service.estornarReserva(idReserva);
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
