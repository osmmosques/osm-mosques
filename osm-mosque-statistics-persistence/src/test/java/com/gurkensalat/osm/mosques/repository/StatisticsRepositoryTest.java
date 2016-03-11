package com.gurkensalat.osm.mosques.repository;

import com.gurkensalat.osm.mosques.SimpleConfiguration;
import com.gurkensalat.osm.mosques.entity.StatisticsEntry;
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
public class StatisticsRepositoryTest
{
    @Autowired
    StatisticsRepository statisticsRepository;

    @Before
    public void setUp()
    {

    }

    @Test
    public void savePlace()
    {
        StatisticsEntry entry = new StatisticsEntry();

        assertTrue(entry.isNew());

        StatisticsEntry qaData = statisticsRepository.save(entry);
        assertNotNull(qaData);
        assertFalse(entry.isNew());
        assertEquals(qaData.getId(), new Long(1));
    }
}
