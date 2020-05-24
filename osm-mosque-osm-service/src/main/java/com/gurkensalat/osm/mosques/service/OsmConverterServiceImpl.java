package com.gurkensalat.osm.mosques.service;

import com.gurkensalat.osm.entity.OsmEntityType;
import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.entity.OsmWay;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.repository.OsmParserRepository;
import com.gurkensalat.osm.repository.OsmParserRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Component
@Slf4j
public class OsmConverterServiceImpl implements OsmConverterService
{
//    @Autowired
//    private OsmTagRepository osmTagRepository;

    // @Autowired
    // TODO fix autowiring...
    private OsmParserRepository osmParserRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @Value("${osm.data.location}")
    private String dataLocation;

    public OsmConverterResult importNodes(String path)
    {
        log.info("Request to import nodes from {} arrived.", path);

        // Hackity Hack
        osmParserRepository = new OsmParserRepositoryImpl();

        OsmConverterResult result = new OsmConverterResult();
        result.setWhat("nodes from file");
        result.setPath(path);
        result.setStart(LocalDateTime.now());

        try
        {
            File dataFile = sanitizePath(path);

            OsmRoot root = osmParserRepository.parse(dataFile);
            log.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());
            result.setNodes(root.getNodes().size());
            result.setWays(root.getWays().size());

            for (OsmNode node : root.getNodes())
            {
                try
                {
                    if (persistOsmNode(node, null, null) != null)
                    {
                        result.setPlaces(result.getPlaces() + 1);
                    }
                }
                catch (Exception e)
                {
                    log.error("While persisting OSM node", e);
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("While sanitizing path", e);
        }

        result.setEnd(LocalDateTime.now());

        return result;
    }

    public OsmConverterResult importWays(String path)
    {
        log.info("Request to import ways from {} arrived.", path);

        // Hackity Hack
        osmParserRepository = new OsmParserRepositoryImpl();

        OsmConverterResult result = new OsmConverterResult();
        result.setWhat("ways from file");
        result.setPath(path);
        result.setStart(LocalDateTime.now());

        try
        {
            File dataFile = sanitizePath(path);

            OsmRoot root = osmParserRepository.parse(dataFile);
            log.info("Read {} ways from {}", root.getNodes().size(), dataFile.getName());
            result.setNodes(root.getNodes().size());
            result.setWays(root.getWays().size());

            for (OsmWay way : root.getWays())
            {
                if (persistOsmWay(way, null, null) != null)
                {
                    result.setPlaces(result.getPlaces() + 1);
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("While sanitizing path", e);
        }

        result.setEnd(LocalDateTime.now());

        return result;
    }

    /* package protected */
    File sanitizePath(String path) throws UnsupportedEncodingException
    {
        File dataFile = new File(dataLocation);

        // Actually, sanitize this input parameter first...
        if (!(isEmpty(path)))
        {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.toString());
            // TODO remove ".." and such from path...
            path = path.replaceAll("/", "");
            dataFile = new File(dataFile, path);
        }

        log.info("Data Directory is {}", dataFile.getAbsolutePath());

        return dataFile;
    }

    @Override
    public OsmConverterResult fetchAndImportNode(String id)
    {
        log.info("Request to re-import node {} arrived.", id);

        OsmConverterResult result = new OsmConverterResult();
        result.setWhat("nodes from server");
        result.setPath(id);
        result.setStart(LocalDateTime.now());

//        long parsedId = Long.parseLong(id);
//        OsmRoot root = osmParserRepository.loadNodeFromServer(parsedId);
//
//        log.info("Read {} nodes and {} ways from server.", root.getNodes().size(), root.getWays().size());
//        result.setNodes(root.getNodes().size());
//        result.setWays(root.getWays().size());
//
//        if (root.isGone())
//        {
//            deleteOsmNode(parsedId);
//        }
//        else
//        {
//            for (OsmNode node : root.getNodes())
//            {
//                if (persistOsmNode(node, null, null) != null)
//                {
//                    result.setPlaces(result.getPlaces() + 1);
//                }
//            }
//        }

        result.setEnd(LocalDateTime.now());

        return result;
    }

    @Override
    public OsmConverterResult fetchAndImportWay(String id)
    {
        log.info("Request to re-import way {} arrived.", id);

        OsmConverterResult result = new OsmConverterResult();
        result.setWhat("ways from server");
        result.setPath(id);
        result.setStart(LocalDateTime.now());

//        long parsedId = Long.parseLong(id);
//        OsmRoot root = osmParserRepository.loadWayFromServer(parsedId);
//
//        log.info("Read {} nodes and {} ways from server.", root.getNodes().size(), root.getWays().size());
//        result.setNodes(root.getNodes().size());
//        result.setWays(root.getWays().size());
//
//        if (root.isGone())
//        {
//            deleteOsmWay(parsedId);
//        }
//        else
//        {
//            for (OsmWay way : root.getWays())
//            {
//                if (persistOsmWay(way, null, null) != null)
//                {
//                    result.setPlaces(result.getPlaces() + 1);
//                }
//            }
//        }

        result.setEnd(LocalDateTime.now());

        return result;
    }

//    /*
//        log.info("About to import OSM data from {} / {}", dataLocation, path);
//
//        log.info("OSM Repository is: {}", osmParserRepository);
//
//        log.debug("Place Repository is: {}", osmMosquePlaceRepository);
//
//        // TODO find a way to invalidate all tags belonging to nodes in a country...
//        // osmTagRepository.invalidateAll();
//
//        // First, import everything so we won't miss anything.
//        for (String continent : Continents.getContinents().keySet())
//        {
//            importData(dataDirectory, continent, "all");
//        }
//
//        for (String country : Countries.getCountries().keySet())
//        {
//            String countryCode = Countries.getCountries().get(country);
//            osmMosquePlaceRepository.invalidateByCountryCode(countryCode);
//
//            if ("germany".equals(country))
//            {
//                for (String state : Countries.getGermanyStates())
//                {
//                    importData(dataDirectory, country, state);
//                }
//            }
//            else
//            {
//                importData(dataDirectory, country, "all");
//            }
//        }
//
//        statisticsService.calculate();
//
//        // Lastly, remove all invalid places
//        osmMosquePlaceRepository.deleteAllInvalid();
//
//        // Now, return the amount of items in the database
//        long loaded = osmMosquePlaceRepository.count();
//        log.info("Loaded {} places into database", loaded);
//
//        return new ImportDataResponse("O.K., Massa!", loaded);
//    }
//    */
//
//        /*
//    private void importData(File dataDirectory, String country, String state)
//    {
//        String countryCode = Countries.getCountries().get(country);
//        if (isEmpty(countryCode))
//        {
//            // Hack to identify continental data
//            countryCode = Continents.getContinents().get(country);
//            if (isEmpty(countryCode))
//            {
//                // Still empty? Then, special hack for "world" :)
//                countryCode = "ZZ";
//            }
//        }
//
//        File dataFile = new File(dataDirectory, country + "-" + state + "-religion-muslim" + "-node" + ".osm");
//
//        OsmRoot root = osmParserRepository.parse(dataFile);
//        for (OsmNode node : root.getNodes())
//        {
//            persistOsmNode(node, countryCode, state);
//        }
//
//        log.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());
//*/
//
//    private void deleteOsmNode(long id)
//    {
//        log.info("Deleting node {}", id);
//        String key = Long.toString(id);
//        List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(key);
//        if ((places != null) && (places.size() > 0))
//        {
//            OsmMosquePlace place = places.get(0);
//            osmMosquePlaceRepository.delete(place.getId());
//        }
//    }

    private OsmMosquePlace persistOsmNode(OsmNode node)
    {
        return persistOsmNode(node, null, null);
    }

    private OsmMosquePlace persistOsmNode(OsmNode node, String countryCode, String state)
    {
        log.debug("Read node: {}, {}, {}", node, node.getLat(), node.getLon());

        String key = Long.toString(node.getId());

        // re-create a place from OSM data
        OsmMosquePlace tempPlace = new OsmMosquePlace(node);
        tempPlace.setKey(key);
        tempPlace.setType(OsmEntityType.NODE);
        tempPlace.setPlaceType(PlaceType.OSM_PLACE_OF_WORSHIP);
        tempPlace.setCountryFromOSM(tempPlace.getAddress().getCountry());

        OsmMosquePlace place = persistOsmMosquePlace(tempPlace, key, countryCode, state);
        if (place != null)
        {
            if (place.isValid())
            {
//                persistTags(place.getId(), node.getTags());
            }
        }

        return place;
    }

//    private void deleteOsmWay(long id)
//    {
//        log.info("Deleting way {}", id);
//        String key = Long.toString(id + OsmMosquePlace.getWayOffset());
//        List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(key);
//        if ((places != null) && (places.size() > 0))
//        {
//            OsmMosquePlace place = places.get(0);
//            osmMosquePlaceRepository.delete(place.getId());
//        }
//    }

    private OsmMosquePlace persistOsmWay(OsmWay way)
    {
        return persistOsmWay(way, null, null);
    }

    private OsmMosquePlace persistOsmWay(OsmWay way, String countryCode, String state)
    {
        log.debug("Read way: {}, {}, {}", way, way.getLat(), way.getLon());

        String key = Long.toString(way.getId() + OsmMosquePlace.getWayOffset());

        // re-create a place from OSM data
        OsmMosquePlace tempPlace = new OsmMosquePlace(way);
        tempPlace.setKey(key);
        tempPlace.setType(OsmEntityType.WAY);
        tempPlace.setPlaceType(PlaceType.OSM_PLACE_OF_WORSHIP);
        tempPlace.setCountryFromOSM(tempPlace.getAddress().getCountry());

        OsmMosquePlace place = persistOsmMosquePlace(tempPlace, key, countryCode, state);
        if (place != null)
        {
            if (place.isValid())
            {
//                persistTags(place.getId(), way.getTags());
            }
        }

        return place;
    }

    private OsmMosquePlace persistOsmMosquePlace(OsmMosquePlace tempPlace, String key, String state, String countryCode)
    {
        OsmMosquePlace place = null;

        if (isEmpty(tempPlace.getAddress().getState()))
        {
            tempPlace.getAddress().setState(state);
        }

        if (isEmpty(tempPlace.getAddress().getCountry()))
        {
            tempPlace.getAddress().setCountry(countryCode);
        }

        String placeName = trimToEmpty(tempPlace.getName());

        if (placeName.length() > 79)
        {
            log.error("Place name too long ({} chars", placeName.length());
            log.error("  was '{}'", placeName);
            tempPlace.setName(substring(placeName, 0, 79));
        }

        tempPlace.getContact().setWebsite(substring(tempPlace.getContact().getWebsite(), 0, 79));

        try
        {
            place = osmMosquePlaceRepository.findByKey(key).orElse(null);
            if (place == null)
            {
                // Place could not be found, insert it...
                tempPlace.setCreationTime(LocalDateTime.now());
                tempPlace.setModificationTime(LocalDateTime.now());
                place = osmMosquePlaceRepository.save(tempPlace);
            }
            else
            {
                log.debug("Found pre-existing entity {} / {}", place.getId(), place.getVersion());
                log.debug("  reloaded: {} / {}", place.getId(), place.getVersion());
            }
                /*
            Optional<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(key);
            if ((places == null) || (places.isEmpty()))
            {
            }
            else
            {
                // take the one from the database and update it
                place = places.get();
                places = osmMosquePlaceRepository.findById(place.getId());
                place = places.get();
            }
                 */

            // TODO maybe better to do a deep-copy of the old place?
            double oldLat = place.getLat();
            double oldLon = place.getLon();

//            String oldCountryFromDatafile = place.getCountryFromDatafile();
//            String oldCountryFromGeocoding = place.getCountryFromGeocoding();

            tempPlace.copyTo(place);

            if (place.getLat() == 0)
            {
                place.setLat(oldLat);
            }

            if (place.getLon() == 0)
            {
                place.setLon(oldLon);
            }

//            if (isEmpty(place.getCountryFromDatafile()))
//            {
//                place.setCountryFromDatafile(oldCountryFromDatafile);
//            }
//
//            if (isEmpty(place.getCountryFromGeocoding()))
//            {
//                place.setCountryFromGeocoding(oldCountryFromGeocoding);
//            }

            place.setValid(true);
            place.setModificationTime(LocalDateTime.now());
            place = osmMosquePlaceRepository.save(place);

            log.info("Saved Place {}", place);
        }
        catch (Exception e)
        {
            log.error("While persisting place", e);
            log.info("Place: {}", tempPlace);
            log.info("OSM node: {}", tempPlace);
        }

        return place;
    }

//    private void persistTags(long parentId, List<OsmWayTag> tags)
//    {
//        TODO analog to persistTags(OsmNodeTag)
//
//                List<OsmNodeTag> tags = new ArrayList<>();
//                for (OsmWayTag wayTag : way.getTags())
//                {
//                    OsmNodeTag tag = new OsmNodeTag();
//                    tag.setKey(wayTag.getKey());
//                    tag.setValue(wayTag.getValue());
//                    tags.add(tag);
//                }
//
//                persistTags(place.getId(), tags);
//    }

//    private void persistTags(long parentId, List<OsmNodeTag> tags)
//    {
//        OsmTag osmTag = null;
//
//        try
//        {
//            // Now, save the tags
//            // TODO allow for Strings as node ids too
//            osmTagRepository.deleteByParentTableAndParentId("OSM_PLACES", parentId);
//            for (OsmNodeTag tag : tags)
//            {
//                // TODO allow for creation of lists of OsmTag entities from OsmNode objects
//                // TODO allow for creation of OsmTag entities from OsmNodeTag objects
//                osmTag = new OsmTag();
//                osmTag.setParentTable("OSM_PLACES");
//                osmTag.setParentId(parentId);
//                osmTag.setKey(tag.getKey());
//                osmTag.setValue(tag.getValue());
//                osmTag.setValid(true);
//
//                if (osmTag.getValue().length() > 79)
//                {
//                    log.error("Cutting down overly long tag value");
//                    log.info("    tag was: {} / '{}'", osmTag.getValue().length(), osmTag.getValue());
//                    osmTag.setValue(StringUtils.substring(osmTag.getValue(), 0, 79));
//                    log.info("    saving: '{}'", osmTag.getValue().length(), osmTag.getValue());
//                }
//
//                osmTag = osmTagRepository.save(osmTag);
//                log.debug("  saved tag {}", osmTag);
//            }
//        }
//        catch (Exception e)
//        {
//            log.error("While persisting place", e);
//            log.info("parent  : {}", parentId);
//            log.info("     tag: {}", osmTag);
//        }
//    }
}
