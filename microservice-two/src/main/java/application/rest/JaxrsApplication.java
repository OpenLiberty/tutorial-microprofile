package application.rest;


import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.metrics.MetricRegistry;

@ApplicationPath("/rest")
public class JaxrsApplication extends Application {

    public static MetricRegistry registry;

    private Set<Class<?>> classes = new HashSet<Class<?>>();
    private Set<Object> singletons = new HashSet<Object>();

    public JaxrsApplication() {
        //Uses a single LibertyRestEndpoint class to manage the endpoint in jaxrs
        singletons.add(new LibertyRestEndpoint());
    }
    
    @Inject
    public void setup(MetricRegistry registry) {
        JaxrsApplication.registry = registry;
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
=======
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class JaxrsApplication extends Application {

}
