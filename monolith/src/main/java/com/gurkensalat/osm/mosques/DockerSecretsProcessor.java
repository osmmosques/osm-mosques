package com.gurkensalat.osm.mosques;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Inspired by <a href="https://github.com/kwonghung-YIP/spring-boot-docker-secret">kwonghungs</a> example code
 */

@Slf4j
public class DockerSecretsProcessor implements EnvironmentPostProcessor
{
    private String secretsDirectoryName = "/run/secrets";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
    {
        File secretsDirectory = new File(secretsDirectoryName);
        if (secretsDirectory.isDirectory() && secretsDirectory.canExecute())
        {
            Map<String,Object> dockerSecrets = new HashMap<>();
            try
            {
                File[] secretFiles = secretsDirectory.listFiles();
                for (File secretFile: secretFiles)
                {
                    String key = secretFile.getName();
                    if (!(key.startsWith("spring.")))
                    {
                        key = key.toLowerCase().replaceAll("\\.", "_");
                    }

                    byte[] content = FileCopyUtils.copyToByteArray(secretFile);
                    dockerSecrets.put(key, Arrays.toString(content));
                }
            }
            catch (IOException ioe)
            {
                log.error("while reading docker secrets", ioe);
            }

            MapPropertySource propertySource = new MapPropertySource("docker-secrets", dockerSecrets);
            environment.getPropertySources().addLast(propertySource);
        }
    }
}
