package dev.guipalazzo.spring.api.fixture;

import dev.guipalazzo.spring.api.domain.CaracteristicaImovel;

public class CaracteristicaImovelFixture {

    private CaracteristicaImovel.CaracteristicaImovelBuilder builder = CaracteristicaImovel.builder();

    public static CaracteristicaImovelFixture get() {
        return new CaracteristicaImovelFixture();
    }

    public CaracteristicaImovel build() {
        return builder.build();
    }

    public CaracteristicaImovelFixture valido() {
        comDescricao();

        return this;
    }

    public CaracteristicaImovelFixture comDescricao() {
        builder.descricao("Característica Fixture");
        return this;
    }

}
