package com.gurkensalat.osm.mosques;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@EnableAutoConfiguration
public class UserPreferencesController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(UserPreferencesController.class);

    private final static String REQUEST_ROOT = "/user/preferences";

    @RequestMapping(value = REQUEST_ROOT, method = RequestMethod.GET)
    String statistics(Model model)
    {
        return "user-preferences";
    }
}
