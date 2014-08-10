package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.CombinedPlace;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "combinedPlace", path = "combinedPlace")
public interface CombinedPlaceRepository extends PagingAndSortingRepository<CombinedPlace, Long>
{
    CombinedPlace findByOsmId(@Param("osm_id") String osmId);

    CombinedPlace findByDitibKey(@Param("ditib_key") String ditibKey);
}
