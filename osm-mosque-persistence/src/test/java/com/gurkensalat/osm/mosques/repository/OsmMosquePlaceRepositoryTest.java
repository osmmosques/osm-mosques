package com.gurkensalat.osm.mosques.repository;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OsmMosquePlaceRepositoryTest
{
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OsmMosquePlaceRepository productRepository;

    @Before
    public void setUp()
    {
        // given
        OsmMosquePlace mosquePlaceGermering = OsmMosquePlace.builder()
            .name("Türk Islam Kültür Cemiyeti")
            .build();

        mosquePlaceGermering.setCreationTime(LocalDateTime.now());
        mosquePlaceGermering.setModificationTime(LocalDateTime.now());

        mosquePlaceGermering.setLat(48.1364);
        mosquePlaceGermering.setLon(11.3872928);

        mosquePlaceGermering = testEntityManager.persist(mosquePlaceGermering);

        // given
        OsmMosquePlace mosquePlaceIstanbul = OsmMosquePlace.builder()
            .name("Konyalı Hacı Tevfik Ağazade Camii")
            .build();

        mosquePlaceIstanbul.setCreationTime(LocalDateTime.now());
        mosquePlaceIstanbul.setModificationTime(LocalDateTime.now());

        mosquePlaceIstanbul.setLat(41.0680501);
        mosquePlaceIstanbul.setLon(28.9998428);

        mosquePlaceIstanbul = testEntityManager.persist(mosquePlaceIstanbul);
    }

    @Test
    public void whenFindByName_thenReturnProduct()
    {
        // when
        OsmMosquePlace product = productRepository.findByName("Türk Islam Kültür Cemiyeti").get();

        // then
        assertThat(product).isNotNull();
    }

    @Test
    public void whenFindAll_thenReturnProductList()
    {
        // when
        List<OsmMosquePlace> products = (List) productRepository.findAll();

        // then
        assertThat(products).hasSize(2);
    }

    @Test
    public void whenFindByBBoxInGermering_thenReturnProductList()
    {
        // when
        List<OsmMosquePlace> products = productRepository.findByBbox(11, 48, 12, 49);

        // then
        assertThat(products).hasSize(1);
    }
}
