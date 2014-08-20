package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.repository.QaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class QaDataRestHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(QaDataRestHandler.class);

    private static final String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";

    private final static String REQUEST_ROOT = "/rest/qadata";

    private final static String REQUEST_CALCULATE_DITIB_SCORE = REQUEST_ROOT + "/calculate/ditib/{ditibCode}";

    // void calculateAllScores();

    // void calculateAllDitibScores();

    // void calculateAllOSMScores();

    // void calculateDitibScore(QaData place, DitibPlace ditibPlace);

    // void calculateOSMScore(QaData place, OsmPlace osmPlace);

    @Autowired
    private QaDataRepository qaDataRepository;

    @RequestMapping(value = REQUEST_CALCULATE_DITIB_SCORE, produces = APPLICATION_JSON_UTF8)
    ResponseEntity<String> calculateDitibScore(@PathVariable("ditibCode") String ditibCode)
    {
        LOGGER.info("Have to calculate {}", ditibCode);

        String result = "Done, Massa";

        return new ResponseEntity<String>(result, null, HttpStatus.OK);
    }

}
