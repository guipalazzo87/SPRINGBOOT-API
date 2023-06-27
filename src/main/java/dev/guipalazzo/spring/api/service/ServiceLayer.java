package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.config.FeignServiceUtil;
import dev.guipalazzo.spring.api.domain.*;
import dev.guipalazzo.spring.api.exception.*;
import dev.guipalazzo.spring.api.repository.AnuncioRepository;
import dev.guipalazzo.spring.api.repository.ImovelRepository;
import dev.guipalazzo.spring.api.repository.ReservaRepository;
import dev.guipalazzo.spring.api.repository.UsuarioRepository;
import dev.guipalazzo.spring.api.request.AtualizarUsuarioRequest;
import dev.guipalazzo.spring.api.request.CadastrarAnuncioRequest;
import dev.guipalazzo.spring.api.request.CadastrarImovelRequest;
import dev.guipalazzo.spring.api.request.CadastrarReservaRequest;
import dev.guipalazzo.spring.api.response.DadosAnuncioResponse;
import dev.guipalazzo.spring.api.response.DadosSolicitanteResponse;
import dev.guipalazzo.spring.api.response.InformacaoReservaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ServiceLayer {
    private final AnuncioRepository anuncioRepository;
    private final ReservaRepository reservaRepository;
    private final ImovelRepository imovelRepository;
    private final UsuarioRepository usuarioRepository;
    private final FeignServiceUtil feignServiceUtil;

    @Autowired
    public ServiceLayer(AnuncioRepository anuncioRepository,
                        ReservaRepository reservaRepository, ImovelRepository imovelRepository, UsuarioRepository usuarioRepository, FeignServiceUtil feignServiceUtil) {
        this.anuncioRepository = anuncioRepository;
        this.reservaRepository = reservaRepository;
        this.imovelRepository = imovelRepository;
        this.usuarioRepository = usuarioRepository;
        this.feignServiceUtil = feignServiceUtil;
    }


    public Page<Anuncio> listarTodosAnuncios(Integer page,
                                             Integer size,
                                             List<Sort.Order> ordenacao) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        return anuncioRepository.findByAtivoTrue(paging);
    }

    public Page<Anuncio> listarAnunciosPorAnunciante(Integer page,
                                                     Integer size,
                                                     List<Sort.Order> ordenacao,
                                                     Long idAnunciante) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        return anuncioRepository.findByAnuncianteIdAndAtivoTrue(idAnunciante, paging);
    }

    public Optional<Anuncio> listarAnuncioPorId(Long idAnuncio) {
        return anuncioRepository.findByIdAndAtivo(idAnuncio);
    }

    public void deletarAnuncio(Long idAnuncio) {
        Optional<Anuncio> optionalAnuncio = listarAnuncioPorId(idAnuncio);
        optionalAnuncio.get().getImovel();
        Anuncio anuncio = optionalAnuncio.orElse(null);

        if (anuncio != null) {
            Anuncio novoAnuncio = new Anuncio(
                    anuncio.getId(),
                    anuncio.getTipoAnuncio(),
                    anuncio.getImovel(),
                    anuncio.getAnunciante(),
                    anuncio.getValorDiaria(),
                    anuncio.getFormasAceitas(),
                    anuncio.getDescricao(),
                    false
            );
            anuncioRepository.save(novoAnuncio);
        } else {
            throw new ObjetoNaoEncontradoPorIdException(Anuncio.class.getSimpleName(), idAnuncio);
        }
    }
    public List<Anuncio> listarAnunciosPorImovel(Long idImovel) {
        return anuncioRepository.findAllByImovelIdAndAtivo(idImovel);
    }
    public Anuncio salvarAnuncio(CadastrarAnuncioRequest body) {

        Optional<Usuario> optionalUsuario = listarUmUsuario(body.getIdAnunciante());
        if (optionalUsuario.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), body.getIdAnunciante());
        Usuario anunciante = optionalUsuario.orElse(null);

        Optional<Imovel> optionalImovel = listarImovelPorId(body.getIdImovel());
        if (optionalImovel.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Imovel.class.getSimpleName(), body.getIdImovel());
        Imovel imovel = optionalImovel.orElse(null);

        List<Anuncio> anuncioList = listarAnunciosPorImovel(imovel.getId());
        if (!anuncioList.isEmpty()) throw new JaExisteAnuncioException(imovel.getId());

        Anuncio anuncio = new Anuncio(
                body.getTipoAnuncio(),
                imovel,
                anunciante,
                body.getValorDiaria(),
                body.getFormasAceitas(),
                body.getDescricao(),
                true
        );
        return anuncioRepository.save(anuncio);
    }

    public Imovel salvarImovel(CadastrarImovelRequest body) {
        Optional<Usuario> optionalUsuario = listarUmUsuario(body.getIdProprietario());

        if (optionalUsuario.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), body.getIdProprietario());
        Usuario proprietario = optionalUsuario.orElse(null);

        Imovel imovel = Imovel.builder()
                .identificacao(body.getIdentificacao())
                .tipoImovel(body.getTipoImovel())
                .endereco(body.getEndereco())
                .proprietario(proprietario)
                .caracteristicas(body.getCaracteristicas())
                .ativo(true)
                .build();
        return imovelRepository.save(imovel);
    }

    public Page<Imovel> listarTodosImovel(Integer page,
                                          Integer size,
                                          List<Sort.Order> ordenacao) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));

        return imovelRepository.findAll(paging);
    }

    public Page<Imovel> listarPorProprietario(Integer page,
                                              Integer size,
                                              List<Sort.Order> ordenacao,
                                              Long idProprietario) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        return imovelRepository.findByProprietarioId(idProprietario, paging);
    }

    public Optional<Imovel> listarImovelPorId(Long idImovel) {
        return imovelRepository.findById(idImovel);
    }

    public void deletarImovel(Long idImovel) {
        Optional<Imovel> optionalImovel = listarImovelPorId(idImovel);
        Imovel imovel = optionalImovel.orElse(null);

        List<Anuncio> anuncioList = listarAnunciosPorImovel(idImovel);
        if (!anuncioList.isEmpty()) throw new ImovelComAnuncioException();

        if (imovel != null) {
            Imovel novoImovel = new Imovel(
                    imovel.getId(),
                    imovel.getIdentificacao(),
                    imovel.getTipoImovel(),
                    imovel.getEndereco(),
                    imovel.getProprietario(),
                    imovel.getCaracteristicas(),
                    false
            );
            imovelRepository.save(novoImovel);
        } else {
            throw new ObjetoNaoEncontradoPorIdException(Imovel.class.getSimpleName(), idImovel);
        }
    }

    public void cancelarReserva(Long idReserva) {
        Optional<Reserva> optionalReserva = reservaRepository.findByIdAndAnuncioAtivoTrue(idReserva);
        if (optionalReserva.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Reserva.class.getSimpleName(), idReserva);
        Reserva reserva = optionalReserva.orElse(null);

        if (!reserva.getPagamento().getStatus().equals(StatusPagamento.PENDENTE))
            throw new ReservaNaoEstaNoStatusEsperadoException("cancelamento", StatusPagamento.PENDENTE);

        Pagamento pagamento = reserva.getPagamento();

        pagamento.setStatus(StatusPagamento.CANCELADO);

        reserva.setPagamento(pagamento);
        reserva.setAtivo(false);

        reservaRepository.save(reserva);
    }

    public void estornarReserva(Long idReserva) {
        Optional<Reserva> optionalReserva = reservaRepository   .findByIdAndAnuncioAtivoTrue(idReserva);
        if (optionalReserva.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Reserva.class.getSimpleName(), idReserva);
        Reserva reserva = optionalReserva.orElse(null);

        if (!reserva.getPagamento().getStatus().equals(StatusPagamento.PAGO))
            throw new ReservaNaoEstaNoStatusEsperadoException("estorno", StatusPagamento.PAGO);

        reserva.getPagamento().setStatus(StatusPagamento.ESTORNADO);
        reserva.getPagamento().setFormaEscolhida(null);
        reserva.setAtivo(false);
        reservaRepository.save(reserva);
    }

    public Page<Reserva> listarReservaPorAnunciante(Integer page, Integer size, List<Sort.Order> ordenacao, Long idAnunciante) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        return reservaRepository.findByAnuncioAnuncianteIdAndAnuncioAtivoTrue(idAnunciante, paging);
    }

    public Page<Reserva> listarReservaPorSolicitante(Integer page,
                                                     Integer size,
                                                     List<Sort.Order> ordenacao,
                                                     Long idSolicitante,
                                                     String dataHoraFinalStr,
                                                     String dataHoraInicialStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime dataHoraFinal = null;

        Periodo periodo = null;

        if (dataHoraInicialStr != null && dataHoraFinalStr != null) {
            try {
                dataHoraFinal = LocalDateTime.parse(dataHoraFinalStr, formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dataHoraFinal != null) {
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

            return reservaRepository.findBySolicitanteIdAndPeriodoDataHoraInicialGreaterThanEqualAndPeriodoDataHoraFinalLessThanEqualAndAnuncioAtivoTrue(idSolicitante, horaInicio, horaFim, paging);
        } else {
            Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
            return reservaRepository.findBySolicitanteIdAndAnuncioAtivoTrue(idSolicitante, paging);
        }
    }

    public void pagarReserva(Long idReserva, FormaPagamento formaPagamento) {

        Optional<Reserva> optionalReserva = reservaRepository.findByIdAndAnuncioAtivoTrue(idReserva);
        if (optionalReserva.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Reserva.class.getSimpleName(), idReserva);
        Reserva reserva = optionalReserva.orElse(null);

        if (!reserva.getAnuncio().getFormasAceitas().contains(formaPagamento))
            throw new ReservaNaoAceitaFormaException(reserva.getAnuncio().getFormasAceitas(), formaPagamento);

        if (!reserva.getPagamento().getStatus().equals(StatusPagamento.PENDENTE))
            throw new ReservaNaoEstaNoStatusEsperadoException("pagamento", StatusPagamento.PENDENTE);

        Pagamento pagamento = new Pagamento(
                reserva.getPagamento().getValorTotal(),
                formaPagamento,
                StatusPagamento.PAGO
        );
        reservaRepository.save(new Reserva(
                reserva.getId(),
                reserva.getSolicitante(),
                reserva.getAnuncio(),
                reserva.getPeriodo(),
                reserva.getQuantidadePessoas(),
                reserva.getDataHoraReserva(),
                pagamento,
                true
        ));
    }

    public InformacaoReservaResponse salvarReserva(CadastrarReservaRequest body) {
        Optional<Usuario> optionalSolicitante = listarUmUsuario(body.getIdSolicitante());
        if (optionalSolicitante.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), body.getIdSolicitante());
        Usuario solicitante = optionalSolicitante.orElse(null);

        Optional<Anuncio> optionalAnuncio = listarAnuncioPorId(body.getIdAnuncio());
        if (optionalAnuncio.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Anuncio.class.getSimpleName(), body.getIdAnuncio());
        Anuncio anuncio = optionalAnuncio.orElse(null);

        if (solicitante.getId().equals(anuncio.getAnunciante().getId()))
            throw new SolicitanteNaoPodeSerAnuncianteException();

        if (anuncio.getImovel().getTipoImovel().equals(TipoImovel.HOTEL) && body.getQuantidadePessoas() < 2)
            throw new PessoasMinimasException(2, anuncio.getImovel().getTipoImovel().toString());

        LocalDateTime dataInicio = body.getPeriodo().getDataHoraInicial();
        LocalDateTime dataFim = body.getPeriodo().getDataHoraFinal();

        if (dataInicio.getHour() != 14 || dataFim.getHour() != 12) {
            dataInicio = LocalDateTime.parse(dataInicio.toLocalDate().toString() + "T14:00");
            dataFim = LocalDateTime.parse(dataFim.toLocalDate().toString() + "T12:00");
            body.setPeriodo(new Periodo(dataInicio, dataFim));
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

        long differenceInTime = d2.getTime() - d1.getTime();
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
    public Optional<Usuario> listarUsuarioPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    public Page<Usuario> listarTodosUsuarios(
            Integer page,
            Integer size,
            List<Sort.Order> ordenacao) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));

        return usuarioRepository.findAll(paging);
    }

    public Optional<Usuario> listarUmUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario atualizarUsuario(Long idUsuario, AtualizarUsuarioRequest body) {
        boolean usuarioExiste = usuarioRepository.existsById(idUsuario);
        if (!usuarioExiste) throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), idUsuario);


        Optional<Usuario> optionalUsuario = listarUmUsuario(idUsuario);
        Usuario usuarioTemp = optionalUsuario.orElse(null);

        assert usuarioTemp != null;
        if (!usuarioTemp.getEmail().equals(body.getEmail())) {
            boolean emailJaCadastrado = usuarioRepository.existsByEmail(body.getEmail());
            if (emailJaCadastrado) throw new EmailJaCadastradoException(body.getEmail());
        }

        usuarioTemp.setNome(body.getNome());
        usuarioTemp.setEmail(body.getEmail());
        usuarioTemp.setSenha(body.getSenha());
        usuarioTemp.setDataNascimento(body.getDataNascimento());
        usuarioTemp.getEndereco().setCep(body.getEndereco().getCep());
        usuarioTemp.getEndereco().setLogradouro(body.getEndereco().getLogradouro());
        usuarioTemp.getEndereco().setNumero(body.getEndereco().getNumero());
        usuarioTemp.getEndereco().setComplemento(body.getEndereco().getComplemento());
        usuarioTemp.getEndereco().setBairro(body.getEndereco().getBairro());
        usuarioTemp.getEndereco().setCidade(body.getEndereco().getCidade());
        usuarioTemp.getEndereco().setEstado(body.getEndereco().getEstado());

        return usuarioRepository.save(usuarioTemp);
    }

    public Usuario salvarUsuario(Usuario usuario) {
        boolean emailJaUtilizado = usuarioRepository.existsByEmail(usuario.getEmail());
        if (emailJaUtilizado)
            throw new EmailJaCadastradoException(usuario.getEmail());

        boolean cpfJaUtilizado = usuarioRepository.existsByCpf(usuario.getCpf());
        if (cpfJaUtilizado) throw new CpfJaCadastradoException(usuario.getCpf());

        ParameterizedTypeReference<String> responseType =
                new ParameterizedTypeReference<>() {
                };

        try {
            RequestEntity<Void> request = RequestEntity.get("https://picsum.photos/200")
                    .accept(MediaType.APPLICATION_JSON).build();
            String urlString = Objects.requireNonNull(new RestTemplate().exchange(request, responseType).getHeaders().get("Picsum-Id")).get(0);
            if (urlString != null) {
                usuario.setAvatarUrl("https://picsum.photos/id/" + urlString + "/200");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarioRepository.save(usuario);
    }

}
