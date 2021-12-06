package dev.guipalazzo.spring.api.domain;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "ativo='True'")
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificacao;

    @Enumerated(EnumType.STRING)
    private TipoImovel tipoImovel;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name = "id_proprietario")
    private Usuario proprietario;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_imovel")
    private List<CaracteristicaImovel> caracteristicas;

    @Column
    private boolean ativo;

    public Imovel(String identificacao, TipoImovel tipoImovel, Endereco endereco, Usuario proprietario, List<CaracteristicaImovel> caracteristicas, boolean ativo) {
        this.identificacao = identificacao;
        this.tipoImovel = tipoImovel;
        this.endereco = endereco;
        this.proprietario = proprietario;
        this.caracteristicas = caracteristicas;
        this.ativo = ativo;
    }
}
