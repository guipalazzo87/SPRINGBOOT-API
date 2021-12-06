package dev.guipalazzo.spring.api.controller;

import dev.guipalazzo.spring.api.domain.FormaPagamento;
import dev.guipalazzo.spring.api.domain.Reserva;
import dev.guipalazzo.spring.api.request.CadastrarReservaRequest;
import dev.guipalazzo.spring.api.response.InformacaoReservaResponse;
import dev.guipalazzo.spring.api.service.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaSalvarReservaService reservaSalvarReservaService;
    private ReservaListarPorAnuncianteService reservaListarPorAnuncianteService;
    private ReservaListarPorSolicitanteService reservaListarPorSolicitanteService;
    private ReservaPagarReservaService reservaPagarReservaService;
    private ReservaCancelarReservaService reservaCancelarReservaService;
    private ReservaEstornarReservaService reservaEstornarReservaService;

    public ReservaController(ReservaSalvarReservaService reservaSalvarReservaService,
                             ReservaListarPorAnuncianteService reservaListarPorAnuncianteService,
                             ReservaListarPorSolicitanteService reservaListarPorSolicitanteService,
                             ReservaPagarReservaService reservaPagarReservaService,
                             ReservaCancelarReservaService reservaCancelarReservaService,
                             ReservaEstornarReservaService reservaEstornarReservaService
    ) {
        this.reservaSalvarReservaService = reservaSalvarReservaService;
        this.reservaListarPorAnuncianteService = reservaListarPorAnuncianteService;
        this.reservaListarPorSolicitanteService = reservaListarPorSolicitanteService;
        this.reservaPagarReservaService = reservaPagarReservaService;
        this.reservaCancelarReservaService = reservaCancelarReservaService;
        this.reservaEstornarReservaService = reservaEstornarReservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InformacaoReservaResponse salvar(@RequestBody @Valid CadastrarReservaRequest body) {
        return reservaSalvarReservaService.execute(body);
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

        Page<Reserva> lista = reservaListarPorSolicitanteService.execute(page,
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

        Page<Reserva> lista = reservaListarPorAnuncianteService.execute(page,
                size,
                ordenacao,
                idAnunciante);
        return new ResponseEntity<>(lista, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping(value = "/{idReserva}/pagamentos")
    @ResponseStatus(HttpStatus.OK)
    public void pagarReserva(@PathVariable Long idReserva,
                             @RequestBody @Valid FormaPagamento formaPagamento) {
        reservaPagarReservaService.execute(idReserva, formaPagamento);
    }

    @PutMapping(value = "/{idReserva}/pagamentos/cancelar")
    @ResponseStatus(HttpStatus.OK)
    public void cancelarReserva(@PathVariable Long idReserva) {
        reservaCancelarReservaService.execute(idReserva);
    }

    @PutMapping(value = "/{idReserva}/pagamentos/estornar")
    @ResponseStatus(HttpStatus.OK)
    public void estornarReserva(@PathVariable Long idReserva) {
        reservaEstornarReservaService.execute(idReserva);
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
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
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
        switch(s) {
            case "asc":
                return Sort.Direction.ASC;
            case "desc":
                return Sort.Direction.DESC;
            default:
                throw new Exception();
        }
    }
}
