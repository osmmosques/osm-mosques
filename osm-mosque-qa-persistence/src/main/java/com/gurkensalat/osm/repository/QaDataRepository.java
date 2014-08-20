package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.QaData;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "combinedPlace", path = "combinedPlace")
public interface QaDataRepository extends PagingAndSortingRepository<QaData, Long>
{
    QaData findByOsmId(@Param("osm_id") String osmId);

    QaData findByDitibKey(@Param("ditib_key") String ditibKey);
}
