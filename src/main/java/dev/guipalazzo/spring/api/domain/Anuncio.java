package dev.guipalazzo.spring.api.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoAnuncio tipoAnuncio;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_imovel")
    private Imovel imovel;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_anunciante")
    private Usuario anunciante;

    private BigDecimal valorDiaria;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<FormaPagamento> formasAceitas;

    private String descricao;

    private boolean ativo;

    public Anuncio(TipoAnuncio tipoAnuncio, Imovel imovel, Usuario anunciante, BigDecimal valorDiaria, List<FormaPagamento> formasAceitas, String descricao, boolean ativo) {
        this.tipoAnuncio = tipoAnuncio;
        this.imovel = imovel;
        this.anunciante = anunciante;
        this.valorDiaria = valorDiaria;
        this.formasAceitas = formasAceitas;
        this.descricao = descricao;
        this.ativo = ativo;
    }
}
