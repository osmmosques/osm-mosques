package com.gurkensalat.osm.mosques;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gurkensalat.osm")
public class Monolith implements CommandLineRunner
{
    private static final Logger log = LoggerFactory.getLogger(Monolith.class);

    public static void main(String[] args)
    {
        SpringApplication.run(Monolith.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        log.info("Joining Thread, Ctrl+C to terminate...");
        Thread.currentThread().join();
    }
}
