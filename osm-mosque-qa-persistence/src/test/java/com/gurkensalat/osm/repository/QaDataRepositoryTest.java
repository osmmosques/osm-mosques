package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.SimpleConfiguration;
import com.gurkensalat.osm.entity.QaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = SimpleConfiguration.class)
public class QaDataRepositoryTest
{
    @Autowired
    QaDataRepository qaDataRepository;

    @Before
    public void setUp()
    {

    }

    @Test
    public void savePlace()
    {
        QaData place = new QaData();

        assertTrue(place.isNew());

        QaData qaData = qaDataRepository.save(place);
        assertNotNull(qaData);
        assertFalse(place.isNew());
        assertEquals(qaData.getId(), new Long(1));
    }
}
