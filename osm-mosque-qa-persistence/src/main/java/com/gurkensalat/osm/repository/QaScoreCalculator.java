package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.LinkedPlace;
import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmPlace;

public interface QaScoreCalculator
{
    void calculateAllScores();

    void calculateAllDitibScores();

    void calculateAllOSMScores();

    void calculateDitibScore(LinkedPlace place, DitibPlace ditibPlace);

    void calculateOSMScore(LinkedPlace place, OsmPlace osmPlace);
}
