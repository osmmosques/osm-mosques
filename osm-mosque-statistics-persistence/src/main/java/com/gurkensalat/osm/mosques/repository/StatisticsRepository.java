package com.gurkensalat.osm.mosques.repository;

import com.gurkensalat.osm.mosques.entity.StatisticsEntry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;

@RepositoryRestResource(collectionResourceRel = "statistics", path = "statistics")
public interface StatisticsRepository extends PagingAndSortingRepository<StatisticsEntry, Long>
{
    @Modifying
    @Transactional
    @Query("update StatisticsEntry set valid = false")
    void invalidateAll();

    @Modifying
    @Transactional
    @Query("delete from StatisticsEntry where valid = false")
    void deleteAllInvalid();
}
