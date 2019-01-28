package richardshen.carbon_tracker;

/**
 * Class to handle Skytrain and VehicleEmissions emission cases
 */
public class VehicleEmissions {
    private static float BUS_EMISSIONSINKG = 0.089f;
    private static float SKYT_EMISSIONS = 25.0f;


    public static float getBusEmissions(float distTravelled) {
        return BUS_EMISSIONSINKG * distTravelled;
    }

    public static float getSkytrainEmissions(float disTravelled) {
        return SKYT_EMISSIONS * disTravelled;
    }
}
