package application.rest;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Timer;

import javax.json.JsonObject;
import javax.json.Json;

public class JaxrsManager {

    //private JaxrsManager jaxrsManager;
    private MetricRegistry registry;
    
    private Timer testDataCalcTimer;
    
    public JaxrsManager() {
        //this.jaxrsManager = jaxrsManager;
        registry = JaxrsApplication.registry;
        setupMetrics();
        
    }
    
    private void setupMetrics() {
        // Timer
        Metadata testDataCalcTimerMetadata = new Metadata(
                "testDataCalcTimer",                             // name
                "Test Data Calculation Time",                    // display name
                "Processing time to find the test data",         // description
                MetricType.TIMER,                                   // type
                MetricUnits.NANOSECONDS);                           // units
                testDataCalcTimer = registry.timer(testDataCalcTimerMetadata);
    }
    
    /**
     * getTestData - get the testing data
     *
     * @return JsonObject
     */
    public JsonObject getTestData() {
        
        // Start timing here
        Timer.Context context = testDataCalcTimer.time();
        
        JsonObject value = Json.createObjectBuilder()
        .add("firstName", "Jamie")
        .add("lastName", "Coleman")
        .add("age", 25)
        .add("address", Json.createObjectBuilder()
            .add("streetAddress", "1 Hurlsey Park")
            .add("city", "Winchester")
            .add("County", "Hampshire")
            .add("postalCode", "SO17 1PR"))
        .add("phoneNumber", Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("type", "home")
                .add("number", "212 555-1234"))
            .add(Json.createObjectBuilder()
                .add("type", "fax")
                .add("number", "646 555-4567")))
        .build();
        
        // Stop timing
        context.close();
        return value;
    }
    
    
}
