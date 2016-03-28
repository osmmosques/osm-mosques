package com.gurkensalat.osm.mosques.service;

import com.gurkensalat.osm.entity.OsmEntityType;
import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmNodeTag;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.entity.OsmTag;
import com.gurkensalat.osm.entity.OsmWay;
import com.gurkensalat.osm.entity.OsmWayTag;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.repository.OsmParserRepository;
import com.gurkensalat.osm.repository.OsmTagRepository;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Component
public class OsmConverterServiceImpl implements OsmConverterService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmConverterServiceImpl.class);

    @Autowired
    private OsmTagRepository osmTagRepository;

    @Autowired
    private OsmParserRepository osmParserRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @Value("${osm.data.location}")
    private String dataLocation;

    public void importNodes(String path)
    {
        LOGGER.info("Request to import nodes from {} arrived.", path);

        File dataFile = new File(dataLocation);

        // Actually, sanitize this input parameter first...
        if (!(isEmpty(path)))
        {
            try
            {
                path = URLDecoder.decode(path, CharEncoding.UTF_8);
            }
            catch (UnsupportedEncodingException e)
            {
                LOGGER.error("While decoding optional path", e);
            }

            dataFile = new File(dataFile, path);
        }

        LOGGER.info("Data Directory is {}", dataFile.getAbsolutePath());

        OsmRoot root = osmParserRepository.parse(dataFile);
        LOGGER.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());

        for (OsmNode node : root.getNodes())
        {
            persistOsmNode(node, null, null);
        }
    }

    public void importWays(String path)
    {
        LOGGER.info("Request to import ways from {} arrived.", path);

        File dataFile = new File(dataLocation);

        // Actually, sanitize this input parameter first...
        if (!(isEmpty(path)))
        {
            try
            {
                path = URLDecoder.decode(path, CharEncoding.UTF_8);
            }
            catch (UnsupportedEncodingException e)
            {
                LOGGER.error("While decoding optional path", e);
            }

            dataFile = new File(dataFile, path);
        }

        LOGGER.info("Data Directory is {}", dataFile.getAbsolutePath());

        OsmRoot root = osmParserRepository.parse(dataFile);
        LOGGER.info("Read {} ways from {}", root.getNodes().size(), dataFile.getName());

        for (OsmWay way : root.getWays())
        {
            persistOsmWay(way, null, null);
        }
    }

    @Override
    public void fetchAndImportNode(String id)
    {
        LOGGER.info("Request to re-import node {} arrived.", id);

        long parsedId = Long.parseLong(id);
        OsmRoot root = osmParserRepository.loadNodeFromServer(parsedId);

        LOGGER.info("Read {} nodes and {} ways from server.", root.getNodes().size(), root.getWays().size());

        for (OsmNode node : root.getNodes())
        {
            persistOsmNode(node, null, null);
        }
    }

    @Override
    public void fetchAndImportWay(String id)
    {
        LOGGER.info("Request to re-import way {} arrived.", id);

        long parsedId = Long.parseLong(id);
        OsmRoot root = osmParserRepository.loadWayFromServer(parsedId);

        LOGGER.info("Read {} nodes and {} ways from server.", root.getNodes().size(), root.getWays().size());

        for (OsmWay way : root.getWays())
        {
            persistOsmWay(way, null, null);
        }
    }
    /*
        LOGGER.info("About to import OSM data from {} / {}", dataLocation, path);

        LOGGER.info("OSM Repository is: {}", osmParserRepository);

        LOGGER.debug("Place Repository is: {}", osmMosquePlaceRepository);

        // TODO find a way to invalidate all tags belonging to nodes in a country...
        // osmTagRepository.invalidateAll();

        // First, import everything so we won't miss anything.
        for (String continent : Continents.getContinents().keySet())
        {
            importData(dataDirectory, continent, "all");
        }

        for (String country : Countries.getCountries().keySet())
        {
            String countryCode = Countries.getCountries().get(country);
            osmMosquePlaceRepository.invalidateByCountryCode(countryCode);

            if ("germany".equals(country))
            {
                for (String state : Countries.getGermanyStates())
                {
                    importData(dataDirectory, country, state);
                }
            }
            else
            {
                importData(dataDirectory, country, "all");
            }
        }

        statisticsService.calculate();

        // Lastly, remove all invalid places
        osmMosquePlaceRepository.deleteAllInvalid();

        // Now, return the amount of items in the database
        long loaded = osmMosquePlaceRepository.count();
        LOGGER.info("Loaded {} places into database", loaded);

        return new ImportDataResponse("O.K., Massa!", loaded);
    }
    */

        /*
    private void importData(File dataDirectory, String country, String state)
    {
        String countryCode = Countries.getCountries().get(country);
        if (isEmpty(countryCode))
        {
            // Hack to identify continental data
            countryCode = Continents.getContinents().get(country);
            if (isEmpty(countryCode))
            {
                // Still empty? Then, special hack for "world" :)
                countryCode = "ZZ";
            }
        }

        File dataFile = new File(dataDirectory, country + "-" + state + "-religion-muslim" + "-node" + ".osm");

        OsmRoot root = osmParserRepository.parse(dataFile);
        for (OsmNode node : root.getNodes())
        {
            persistOsmNode(node, countryCode, state);
        }

        LOGGER.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());
*/


    private void persistOsmNode(OsmNode node)
    {
        persistOsmNode(node, null, null);
    }

    private void persistOsmNode(OsmNode node, String countryCode, String state)
    {
        LOGGER.debug("Read node: {}, {}, {}", node, node.getLat(), node.getLon());

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
                persistTags(place.getId(), node.getTags());
            }
        }
    }

    private void persistOsmWay(OsmWay way)
    {
        persistOsmWay(way, null, null);
    }

    private void persistOsmWay(OsmWay way, String countryCode, String state)
    {

        LOGGER.debug("Read way: {}, {}, {}", way, way.getLat(), way.getLon());

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
                List<OsmNodeTag> tags = new ArrayList<>();
                for (OsmWayTag wayTag : way.getTags())
                {
                    OsmNodeTag tag = new OsmNodeTag();
                    tag.setKey(wayTag.getKey());
                    tag.setValue(wayTag.getValue());
                    tags.add(tag);
                }

                persistTags(place.getId(), tags);
            }
        }
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
            LOGGER.error("Place name too long ({} chars", placeName.length());
            LOGGER.error("  was '{}'", placeName);
            tempPlace.setName(placeName.substring(0, 79));
        }

        tempPlace.getContact().setWebsite(substring(tempPlace.getContact().getWebsite(), 0, 79));

        try
        {
            List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(key);
            if ((places == null) || (places.size() == 0))
            {
                // Place could not be found, insert it...
                tempPlace.setCreationTime(DateTime.now());
                tempPlace.setModificationTime(DateTime.now());
                place = osmMosquePlaceRepository.save(tempPlace);
            }
            else
            {
                // take the one from the database and update it
                place = places.get(0);
                LOGGER.debug("Found pre-existing entity {} / {}", place.getId(), place.getVersion());
                place = osmMosquePlaceRepository.findOne(place.getId());
                LOGGER.debug("  reloaded: {} / {}", place.getId(), place.getVersion());
            }

            double oldLat = place.getLat();
            double oldLon = place.getLon();

            tempPlace.copyTo(place);

            if (place.getLat() == 0)
            {
                place.setLat(oldLat);
            }

            if (place.getLon() == 0)
            {
                place.setLon(oldLon);
            }

            place.setValid(true);
            place.setModificationTime(DateTime.now());
            place = osmMosquePlaceRepository.save(place);

            LOGGER.info("Saved Place {}", place);
        }
        catch (Exception e)
        {
            LOGGER.error("While persisting place", e);
            LOGGER.info("Place: {}", tempPlace);
            LOGGER.info("OSM node: {}", tempPlace);
        }

        return place;
    }

    private void persistTags(long parentId, List<OsmNodeTag> tags)
    {
        OsmTag osmTag = null;

        try
        {
            // Now, save the tags
            // TODO allow for Strings as node ids too
            osmTagRepository.deleteByParentTableAndParentId("OSM_PLACES", parentId);
            for (OsmNodeTag tag : tags)
            {
                // TODO allow for creation of lists of OsmTag entities from OsmNode objects
                // TODO allow for creation of OsmTag entities from OsmNodeTag objects
                osmTag = new OsmTag();
                osmTag.setParentTable("OSM_PLACES");
                osmTag.setParentId(parentId);
                osmTag.setKey(tag.getKey());
                osmTag.setValue(tag.getValue());
                osmTag.setValid(true);

                if (osmTag.getValue().length() > 79)
                {
                    LOGGER.error("Cutting down overly long tag value");
                    LOGGER.info("    tag was: {} / '{}'", osmTag.getValue().length(), osmTag.getValue());
                    osmTag.setValue(StringUtils.substring(osmTag.getValue(), 0, 79));
                    LOGGER.info("    saving: '{}'", osmTag.getValue().length(), osmTag.getValue());
                }

                osmTag = osmTagRepository.save(osmTag);
                LOGGER.debug("  saved tag {}", osmTag);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("While persisting place", e);
            LOGGER.info("parent  : {}", parentId);
            LOGGER.info("     tag: {}", osmTag);
        }
    }
}
