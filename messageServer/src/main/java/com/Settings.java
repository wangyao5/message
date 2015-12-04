package com;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@PropertySource(value = {"classpath:project.properties"})
@Component
public class Settings {
    @Value("${znode.path}")
    private String znodePath;

    @Value("${mina.port}")
    private int minaPort;

    @Value("${jgroups.cluster.name}")
    private String jgroupsClusterName;

    public String getZnodePath() {
        return znodePath;
    }

    public int getMinaPort() {
        return minaPort;
    }

    public String getJClusterName() {
        return jgroupsClusterName;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
