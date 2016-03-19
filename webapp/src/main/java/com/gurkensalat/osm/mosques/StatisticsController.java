package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.entity.StatisticsEntry;
import com.gurkensalat.osm.mosques.repository.StatisticsRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class StatisticsController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);

    private final static String REQUEST_ROOT = "/statistics";

    @Autowired
    private StatisticsRepository statisticsRepository;

    @RequestMapping(value = REQUEST_ROOT, method = RequestMethod.GET)
    String statistics(Model model)
    {
        int overallCount = 0;

        List<StatisticsEntry> countries = new ArrayList<>();
        List<StatisticsEntry> continents = new ArrayList<>();

        Iterable<StatisticsEntry> statisticsEntries = statisticsRepository.findAll();
        for (StatisticsEntry statisticsEntry : statisticsEntries)
        {
            overallCount += statisticsEntry.getOsmMosqueNodes();
            statisticsEntry.setCountryCode(StringUtils.trimToEmpty(statisticsEntry.getCountryCode()).toLowerCase());

            statisticsEntry.setCountryFlagImgUrl("https://blog.osmmosques.org/wp-content/uploads/2016/03/" + statisticsEntry.getCountryCode() + ".png");

            if (statisticsEntry.getCountryCode().startsWith("z"))
            {
                continents.add(statisticsEntry);
            }
            else
            {
                countries.add(statisticsEntry);
            }
        }

        List<StatisticsEntry> topCountries = new ArrayList<>();
        topCountries.addAll(countries.subList(0, 10));

        model.addAttribute("statisticsEntries", statisticsEntries);

        model.addAttribute("countries", countries);

        model.addAttribute("topCountries", topCountries);

        model.addAttribute("continents", continents);

        model.addAttribute("overall_count", overallCount);

        return "statistics";
    }
}
