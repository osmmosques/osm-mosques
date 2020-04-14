package com.gurkensalat.osm.mosques;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class PersistenceContext
{
    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.connectionTestQuery}")
    private String connectionTestQuery;

    // @Autowired
    // private MetricRegistry metricRegistry;

    @Bean(destroyMethod = "close")
    public DataSource dataSource()
    {
        Properties dsProps = new Properties();
        dsProps.setProperty("url", dataSourceUrl);
        dsProps.setProperty("user", user);
        dsProps.setProperty("password", password);

        Properties configProps = new Properties();
        configProps.setProperty("connectionTestQuery", connectionTestQuery);
        configProps.setProperty("driverClassName", driverClassName);
        configProps.setProperty("jdbcUrl", dataSourceUrl);

        HikariConfig hc = new HikariConfig(configProps);
        hc.setDataSourceProperties(dsProps);
        // hc.setMetricRegistry(metricRegistry);
        return new HikariDataSource(hc);
    }

    // @Bean(destroyMethod = "close")
    // DataSource dataSource(Environment env)
    // {
    //     HikariConfig dataSourceConfig = new HikariConfig();
    //     dataSourceConfig.setDriverClassName(env.getRequiredProperty("db.driver"));
    //     dataSourceConfig.setJdbcUrl(env.getRequiredProperty("db.url"));
    //     dataSourceConfig.setUsername(env.getRequiredProperty("db.username"));
    //     dataSourceConfig.setPassword(env.getRequiredProperty("db.password"));
    //
    //     return new HikariDataSource(dataSourceConfig);
    // }

}
