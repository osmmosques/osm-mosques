package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.LinkedPlace;
import org.springframework.stereotype.Component;

@Component
public class QaScoreCalculatorImpl implements QaScoreCalculator
{
    private static final double DELTA_LAT_LON = 0.001;

    private static final double LAT_LAC_LEMAN = 46.42;
    private static final double LON_LAC_LEMAN = 6.55;

    @Override
    public void calculateAllScores()
    {

    }

    @Override
    public void calculateAllDitibScores()
    {

    }

    @Override
    public void calculateAllOSMScores()
    {

    }

    @Override
    public void calculateDitibScore(LinkedPlace qaPlace)
    {
        badnessIsGeocoded(qaPlace);
        sumOfAllBadness(qaPlace);
    }

    @Override
    public void calculateOSMScore(LinkedPlace qaPlace)
    {
        sumOfAllBadness(qaPlace);
    }

    private void badnessIsGeocoded(LinkedPlace qaPlace)
    {
        qaPlace.setBadnessIsGeocoded(0);

        if ((Math.abs(qaPlace.getLat()) < DELTA_LAT_LON) && (Math.abs(qaPlace.getLon()) < DELTA_LAT_LON))
        {
            qaPlace.setBadnessIsGeocoded(100);
        }

        // "Lake" hack for DITIB places
        if ((Math.abs(qaPlace.getLat() - LAT_LAC_LEMAN) < DELTA_LAT_LON) && (Math.abs(qaPlace.getLon() - LON_LAC_LEMAN) < DELTA_LAT_LON))
        {
            qaPlace.setBadnessIsGeocoded(100);
        }

    }

    private void sumOfAllBadness(LinkedPlace qaPlace)
    {
        qaPlace.setScore(0);
        qaPlace.setScore(qaPlace.getScore() + qaPlace.getBadnessIsGeocoded());
    }


}
