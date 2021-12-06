package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.repository.ReservaRepository;
import dev.guipalazzo.spring.api.domain.Periodo;
import dev.guipalazzo.spring.api.domain.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReservaListarPorSolicitanteService {
    private ReservaRepository reservaRepository;

    @Autowired
    public ReservaListarPorSolicitanteService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Page<Reserva> execute(Integer page,
                                 Integer size,
                                 List<Sort.Order> ordenacao,
                                 Long idSolicitante,
                                 String dataHoraFinalStr,
                                 String dataHoraInicialStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime dataHoraInicial = null;
        LocalDateTime dataHoraFinal = null;

        Periodo periodo = null;

        if (dataHoraInicialStr != null && dataHoraFinalStr != null) {
            try {
                dataHoraInicial = LocalDateTime.parse(dataHoraInicialStr, formatter);
                dataHoraFinal = LocalDateTime.parse(dataHoraFinalStr, formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dataHoraFinal != null && dataHoraInicial != null) {
                periodo = new Periodo(LocalDateTime.parse(dataHoraInicialStr, formatter),
                                        LocalDateTime.parse(dataHoraFinalStr, formatter));
            }
        }

        if (periodo != null) {
            LocalDateTime horaInicio = periodo.getDataHoraInicial();
            LocalDateTime horaFim = periodo.getDataHoraFinal();

            Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao).descending());
            // Consulta por datahoras exatas
//            Page<Reserva> resultadoPaginado = reservaRepository.findBySolicitanteIdAndPeriodoDataHoraInicialAndPeriodoDataHoraFinal(idSolicitante, horaInicio, horaFim, paging);

            // Consulta por reservas cuja data inicial e/ou data final estão entre o período indicado na request
//            Page<Reserva> resultadoPaginado = reservaRepository.findBySolicitanteIdAndPeriodoDataHoraInicialBetweenOrPeriodoDataHoraFinalBetween(idSolicitante, horaInicio, horaFim, horaInicio, horaFim, paging);

            // Consulta por reservas que estejam integralmente incluídas dentro do período indicado na request
            Page<Reserva> resultadoPaginado = reservaRepository.findBySolicitanteIdAndPeriodoDataHoraInicialGreaterThanEqualAndPeriodoDataHoraFinalLessThanEqualAndAnuncioAtivoTrue(idSolicitante, horaInicio, horaFim, paging);

            return resultadoPaginado;
        } else {
            Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
            Page<Reserva> resultadoPaginado = reservaRepository.findBySolicitanteIdAndAnuncioAtivoTrue(idSolicitante, paging);
            return resultadoPaginado;
        }
    }

}
