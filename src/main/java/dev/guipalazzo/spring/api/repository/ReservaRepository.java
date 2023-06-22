package dev.guipalazzo.spring.api.repository;

import dev.guipalazzo.spring.api.domain.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva,Long> {

    Page<Reserva> findByAnuncioAnuncianteIdAndAnuncioAtivoTrue(Long idAnunciante, Pageable paging);

    Page<Reserva> findBySolicitanteId(Long idSolicitante, Pageable paging);

    // Caso seja necessário retornar reservas no período exatamente igual ao período passado
    @Query("select r from Reserva r where r.solicitante.id = ?1 and r.periodo.dataHoraInicial = ?2 and r.periodo.dataHoraFinal = ?3")
    Page<Reserva> findBySolicitanteIdAndPeriodoDataHoraInicialAndPeriodoDataHoraFinal(Long idSolicitante,
                                                                                      LocalDateTime dataHoraInicial,
                                                                                      LocalDateTime dataHoraFinal,
                                                                                      Pageable paging);

    // Caso seja necessário retornar reservas cujo início e/ou final estejam dentro do período passado
    @Query("select r from Reserva r where r.solicitante.id = ?1 and r.periodo.dataHoraInicial between ?2 and ?3 or r.periodo.dataHoraFinal between ?4 and ?5")
    Page<Reserva> findBySolicitanteIdAndPeriodoDataHoraInicialBetweenOrPeriodoDataHoraFinalBetween(Long idSolicitante,
                                                                                                   LocalDateTime dataHoraInicial,
                                                                                                   LocalDateTime dataHoraFinal,
                                                                                                   LocalDateTime dataHoraInicial2,
                                                                                                   LocalDateTime dataHoraFinal2,
                                                                                                   Pageable paging);

    // Caso seja necessário retornar reservas cujo início e final estejam dentro do período passado
    Page<Reserva> findBySolicitanteIdAndPeriodoDataHoraInicialGreaterThanEqualAndPeriodoDataHoraFinalLessThanEqualAndAnuncioAtivoTrue(Long idSolicitante,
                                                                                                                                      LocalDateTime dataHoraInicial,
                                                                                                                                      LocalDateTime dataHoraFinal,
                                                                                                                                      Pageable paging);

    //
    // countByAnuncioIdAndPeriodo_DataHoraInicialLessThanEqualAndPeriodo_DataHoraFinalGreaterThanEqualAndAtivoTrue
//    @Query("select (count(r) > 0) from Reserva r where r.anuncio.id = :anuncioId and r.periodo.dataHoraInicial <= :dataHoraFinal and r.periodo.dataHoraFinal >= :dataHoraInicial and r.ativo = true")
    boolean existsByAnuncioIdAndPeriodo_DataHoraInicialLessThanEqualAndPeriodo_DataHoraFinalGreaterThanEqualAndAtivoTrue(Long anuncioId,
                                                                                                                         LocalDateTime dataHoraFinal,
                                                                                                                         LocalDateTime dataHoraInicial);

    Optional<Reserva> findByIdAndAnuncioAtivoTrue(Long idReserva);

    Page<Reserva> findBySolicitanteIdAndAnuncioAtivoTrue(Long idSolicitante, Pageable paging);

    @NotNull @org.jetbrains.annotations.NotNull Reserva save(@NotNull @org.jetbrains.annotations.NotNull Reserva reserva);
}
