package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.domain.*;
import dev.guipalazzo.spring.api.exception.*;
import dev.guipalazzo.spring.api.repository.ReservaRepository;
import dev.guipalazzo.spring.api.response.DadosAnuncioResponse;
import dev.guipalazzo.spring.api.response.DadosSolicitanteResponse;
import dev.guipalazzo.spring.api.response.InformacaoReservaResponse;
import dev.guipalazzo.spring.api.request.CadastrarReservaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class ReservaSalvarReservaService {
    private final UsuarioService usuarioService;
    private final AnuncioService anuncioService;
    private final ReservaRepository reservaRepository;

    public ReservaSalvarReservaService(UsuarioService usuarioService,
                                       AnuncioService anuncioService,
                                       ReservaRepository reservaRepository) {
        this.usuarioService = usuarioService;
        this.anuncioService = anuncioService;
        this.reservaRepository = reservaRepository;
    }

    public InformacaoReservaResponse execute(CadastrarReservaRequest body) {
        Optional<Usuario> optionalSolicitante = usuarioService.listarUm(body.getIdSolicitante());
        if (optionalSolicitante.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), body.getIdSolicitante());
        Usuario solicitante = optionalSolicitante.orElse(null);

        Optional<Anuncio> optionalAnuncio = anuncioService.listarAnuncioPorId(body.getIdAnuncio());
        if (optionalAnuncio.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Anuncio.class.getSimpleName(), body.getIdAnuncio());
        Anuncio anuncio = optionalAnuncio.orElse(null);

        if (solicitante.getId().equals(anuncio.getAnunciante().getId()))
            throw new SolicitanteNaoPodeSerAnuncianteException();

        if (anuncio.getImovel().getTipoImovel().equals(TipoImovel.HOTEL) && body.getQuantidadePessoas() < 2)
            throw new PessoasMinimasException(2,anuncio.getImovel().getTipoImovel().toString());

        LocalDateTime dataInicio = body.getPeriodo().getDataHoraInicial();
        LocalDateTime dataFim = body.getPeriodo().getDataHoraFinal();

        if (dataInicio.getHour() != 14 || dataFim.getHour() != 12) {
            dataInicio = LocalDateTime.parse(dataInicio.toLocalDate().toString() + "T14:00");
            dataFim = LocalDateTime.parse(dataFim.toLocalDate().toString() + "T12:00");
            body.setPeriodo(new Periodo(dataInicio,dataFim));
        }

        boolean imovelOcupado = reservaRepository.existsByAnuncioIdAndPeriodo_DataHoraInicialLessThanEqualAndPeriodo_DataHoraFinalGreaterThanEqualAndAtivoTrue(anuncio.getId(), dataFim, dataInicio);

        if (imovelOcupado)
            throw new ImovelNaoDisponivelException();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(dataInicio.toLocalDate().toString());
            d2 = sdf.parse(dataFim.toLocalDate().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d1 == null || d2 == null) throw new DatasInvalidasException();

        long differenceInTime = d2.getTime()-d1.getTime();
        long diffInDays = (differenceInTime / (1000 * 60 * 60 * 24)) % 365;

        if (diffInDays < 0) throw new DatasInvalidasException();
        if (diffInDays == 0) throw new MinimoUmaDiariaException();

        if (diffInDays < 5 && anuncio.getImovel().getTipoImovel().equals(TipoImovel.POUSADA))
            throw new DiariasMinimasException(5, anuncio.getImovel().getTipoImovel().toString());

        BigDecimal valorTotal = anuncio.getValorDiaria().multiply(BigDecimal.valueOf(diffInDays));

        Pagamento pagamento = new Pagamento(
                valorTotal, null, StatusPagamento.PENDENTE
        );

        Reserva reservaSalva = reservaRepository.save(new Reserva(
                solicitante,
                anuncio,
                body.getPeriodo(),
                body.getQuantidadePessoas(),
                LocalDateTime.now(),
                pagamento,
                true
        ));

        DadosSolicitanteResponse dadosSolicitante = new DadosSolicitanteResponse(
                reservaSalva.getSolicitante().getId(),
                reservaSalva.getSolicitante().getNome()
        );

        DadosAnuncioResponse dadosAnuncio = new DadosAnuncioResponse(
                reservaSalva.getAnuncio().getId(),
                reservaSalva.getAnuncio().getImovel(),
                reservaSalva.getAnuncio().getAnunciante(),
                reservaSalva.getAnuncio().getFormasAceitas(),
                reservaSalva.getAnuncio().getDescricao()
        );

        return new InformacaoReservaResponse(
                reservaSalva.getId(),
                dadosSolicitante,
                reservaSalva.getQuantidadePessoas(),
                dadosAnuncio,
                reservaSalva.getPeriodo(),
                reservaSalva.getPagamento()
        );
    }
}
