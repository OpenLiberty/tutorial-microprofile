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
        .add("shipName", "Titanic")
        .add("shipType", "Spaceship")
        .build();
        
        // Stop timing
        context.close();
        return value;
    }
    
    
}
