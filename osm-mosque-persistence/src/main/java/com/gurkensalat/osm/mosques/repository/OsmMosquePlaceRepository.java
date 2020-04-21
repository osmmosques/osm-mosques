package com.gurkensalat.osm.mosques.repository;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface OsmMosquePlaceRepository extends PagingAndSortingRepository<OsmMosquePlace, Long>
{
    Optional<OsmMosquePlace> findByName(String name);

//    List<OsmMosquePlace> findByKey(@Param("key") String key);
//
//    List<OsmMosquePlace> findByName(@Param("name") String name);
//
//    @Query("SELECT p FROM OsmMosquePlace p WHERE :min_lon <= p.lon and p.lon < :max_lon and :min_lat <= p.lat and p.lat <= :max_lat")
//    List<OsmMosquePlace> findByBbox(@Param("min_lon") double minLongitude,
//                                    @Param("min_lat") double minLatitude,
//                                    @Param("max_lon") double maxLongitude,
//                                    @Param("max_lat") double maxLatitude);
//
//    @Modifying
//    @Transactional
//    @Query("update OsmMosquePlace set valid = false")
//    void invalidateAll();
//
//    @Modifying
//    @Transactional
//    @Query("update OsmMosquePlace set valid = false where ADDR_COUNTRY = :addr_country")
//    void invalidateByCountryCode(@Param("addr_country") String countryCode);
//
//    @Modifying
//    @Transactional
//    @Query("update OsmMosquePlace set valid = false where ADDR_COUNTRY_DATAFILE = :addr_country")
//    void invalidateByCountryCodeFromDatafile(@Param("addr_country") String countryCode);
//
//    @Modifying
//    @Transactional
//    @Query("update OsmMosquePlace set valid = false where ADDR_COUNTRY_OSM = :addr_country")
//    void invalidateByCountryCodeFromOSM(@Param("addr_country") String countryCode);
//
//    @Modifying
//    @Transactional
//    @Query("update OsmMosquePlace set valid = false where ADDR_COUNTRY_GEOCODING = :addr_country")
//    void invalidateByCountryCodeFromGeocoding(@Param("addr_country") String countryCode);
//
//    @Modifying
//    @Transactional
//    @Query("delete from OsmMosquePlace where valid = false")
//    void deleteAllInvalid();
//
//    @Modifying
//    @Transactional
//    @Query("update OsmMosquePlace set ADDR_COUNTRY_GEOCODING = '' where ADDR_COUNTRY_GEOCODING is null")
//    void emptyIfNullCountryCodeFromGeocoding();
//
//    @Query("SELECT p FROM OsmMosquePlace p WHERE ADDR_COUNTRY is null and p.countryFromGeocoding = '' order by p.lastGeocodeAttempt, p.key")
//    List<OsmMosquePlace> reverseCountryGeocodingCandidatesByAge(Pageable pageable);
//
//    @Query("SELECT p FROM OsmMosquePlace p WHERE ADDR_COUNTRY is null and p.countryFromGeocoding = '' order by p.lastGeocodeAttempt, p.lon")
//    List<OsmMosquePlace> reverseCountryGeocodingCandidatesByLongitude(Pageable pageable);
//
//    @Query("SELECT p FROM OsmMosquePlace p WHERE :min_lon <= p.lon and p.lon < :max_lon and :min_lat <= p.lat and p.lat <= :max_lat and ADDR_COUNTRY is null and p.countryFromGeocoding = '' order by p.lastGeocodeAttempt, p.lon")
//    List<OsmMosquePlace> reverseCountryGeocodingCandidates(Pageable pageable,
//                                                           @Param("min_lon") double minLongitude,
//                                                           @Param("min_lat") double minLatitude,
//                                                           @Param("max_lon") double maxLongitude,
//                                                           @Param("max_lat") double maxLatitude);
}
