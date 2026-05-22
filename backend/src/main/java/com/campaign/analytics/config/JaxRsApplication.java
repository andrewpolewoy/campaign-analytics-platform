package com.campaign.analytics.config;

import java.util.Set;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class JaxRsApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new java.util.HashSet<>();
        classes.add(MultiPartFeature.class);
        classes.add(com.campaign.analytics.resource.HealthResource.class);
        classes.add(com.campaign.analytics.resource.MetricsResource.class);
        classes.add(com.campaign.analytics.resource.MetricsImportResource.class);
        return classes;
    }
}
