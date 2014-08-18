package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.CombinedPlace;
import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmPlace;

public interface CombinedScoreCalculator
{
    void calculateAllScores();

    void calculateAllDitibScores();

    void calculateAllOSMScores();

    void calculateDitibScore(CombinedPlace place, DitibPlace ditibPlace);

    void calculateOSMScore(CombinedPlace place, OsmPlace osmPlace);
}
