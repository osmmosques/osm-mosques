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

import static org.apache.commons.lang3.StringUtils.isEmpty;

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
        int overallNodes = 0;
        int overallWays = 0;

        List<StatisticsEntry> countries = new ArrayList<>();
        List<StatisticsEntry> continents = new ArrayList<>();

        Iterable<StatisticsEntry> statisticsEntries = statisticsRepository.findAll();
        for (StatisticsEntry statisticsEntry : statisticsEntries)
        {
            if (statisticsEntry.getOsmMosqueNodes() == null)
            {
                statisticsEntry.setOsmMosqueNodes(0);
            }

            if (statisticsEntry.getOsmMosqueWays() == null)
            {
                statisticsEntry.setOsmMosqueWays(0);
            }

            overallNodes += statisticsEntry.getOsmMosqueNodes();
            overallWays += statisticsEntry.getOsmMosqueWays();

            statisticsEntry.setOsmMosqueTotal(statisticsEntry.getOsmMosqueNodes() + statisticsEntry.getOsmMosqueWays());

            statisticsEntry.setCountryCode(StringUtils.trimToEmpty(statisticsEntry.getCountryCode()).toLowerCase());

            String countryName = Countries.getCountries().get(statisticsEntry.getCountryCode());

            if (isEmpty(countryName))
            {
                // countryName = Countries.getCountries().get("??");
                countryName = statisticsEntry.getCountryCode();
            }

            statisticsEntry.setCountryName(countryName);

            statisticsEntry.setCountryFlagImgUrl("https://blog.osmmosques.org/wp-content/uploads/2016/03/" + statisticsEntry.getCountryCode() + ".png");

            // if (statisticsEntry.getCountryCode().startsWith("z"))
            // {
            // continents.add(statisticsEntry);
            // }

            countries.add(statisticsEntry);
        }

        List<StatisticsEntry> topCountries = new ArrayList<>();
        topCountries.addAll(countries.subList(0, 10));

        model.addAttribute("statisticsEntries", statisticsEntries);

        model.addAttribute("countries", countries);

        model.addAttribute("topCountries", topCountries);

        model.addAttribute("continents", continents);

        model.addAttribute("overall_osm_nodes", overallNodes);
        model.addAttribute("overall_osm_ways", overallWays);

        model.addAttribute("overall_count", overallNodes + overallWays);

        return "statistics";
    }
}
