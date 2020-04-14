package com.gurkensalat.osm.mosques;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    // see http://stackoverflow.com/questions/36359505/extending-springbootwebsecurityconfiguration-with-custom-httpsecurity-configurat
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        // see http://www.baeldung.com/spring-security-session
        LOGGER.debug("--- Always create a session for users...");
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

        // LOGGER.info("--- ALLOW all HTTP OPTIONS Requests");
        // http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "*//**").permitAll();

        LOGGER.debug("--- ALLOW all HTTP Requests");
        http.authorizeRequests().anyRequest().permitAll();

        // Allow access to internal REST urls
        http.csrf().ignoringAntMatchers("/rest/internal/**/*");
    }

    @Bean
    @Order(value = 0)
    public FilterRegistrationBean sessionRepositoryFilterRegistration(SessionRepositoryFilter springSessionRepositoryFilter)
    {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new DelegatingFilterProxy(springSessionRepositoryFilter));
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        return filterRegistrationBean;
    }

    // @Autowired
    // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    // }
}
