package com.gurkensalat.osm.mosques.repository;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OsmMosquePlaceRepositoryTest
{
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OsmMosquePlaceRepository productRespository;

    @Before
    public void setUp()
    {
        // given
        OsmMosquePlace product = OsmMosquePlace.builder()
            .name("P1")
            // .description("P1 desc")
            // .price(new BigDecimal("1"))
            .build();

        testEntityManager.persist(product);
    }

    @Test
    public void whenFindByName_thenReturnProduct()
    {
        // when
        OsmMosquePlace product = productRespository.findByName("P1").get();

        // then
        // // assertThat(product.getDescription()).isEqualTo("P1 desc");
        // assertThat(4711).isEqualTo(42);
    }

    @Test
    public void whenFindAll_thenReturnProductList()
    {
        // when
        List<OsmMosquePlace> products = productRespository.findAll();

        // then
        assertThat(products).hasSize(1);
    }
}
