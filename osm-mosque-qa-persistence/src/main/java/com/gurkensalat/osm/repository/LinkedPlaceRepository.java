package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.LinkedPlace;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;

@RepositoryRestResource(collectionResourceRel = "linkedPlace", path = "linkedPlace")
public interface LinkedPlaceRepository extends PagingAndSortingRepository<LinkedPlace, Long>
{
    Iterable<LinkedPlace> findAllByOsmId(@Param("OSM_ID") String osmId);

    Iterable<LinkedPlace> findAllByDitibCode(@Param("DITIB_CODE") String ditibCode);

    @Modifying
    @Transactional
    @Query("update LinkedPlace set valid = 'f'")
    void invalidateAll();

    @Modifying
    @Transactional
    @Query("delete from LinkedPlace where valid = 'f'")
    void deleteAllInvalid();
}
