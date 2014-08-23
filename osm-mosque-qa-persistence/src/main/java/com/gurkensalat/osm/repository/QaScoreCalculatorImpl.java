package com.gurkensalat.osm.repository;

import com.gurkensalat.osm.entity.LinkedPlace;
import org.springframework.stereotype.Component;

@Component
public class QaScoreCalculatorImpl implements QaScoreCalculator
{
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
    public void calculateDitibScore(LinkedPlace qaData)
    {
        qaData.setScore(42);
    }

    @Override
    public void calculateOSMScore(LinkedPlace qaData)
    {
        qaData.setScore(42);
    }
}
