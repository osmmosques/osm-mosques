package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.QaData;
import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmPlace;

public interface QaScoreCalculator
{
    void calculateAllScores();

    void calculateAllDitibScores();

    void calculateAllOSMScores();

    void calculateDitibScore(QaData place, DitibPlace ditibPlace);

    void calculateOSMScore(QaData place, OsmPlace osmPlace);
}
