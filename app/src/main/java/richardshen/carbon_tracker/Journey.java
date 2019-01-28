package richardshen.carbon_tracker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to calculate the Carbon Footprint using selected routes and car
 */
public class Journey {

    private static final float MPG_TO_KM_CONVERT_RATE = 1.609344f;

    private Route route;

    private Car car;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

    private Date dateOfJourney;

    private float footPrintOverride = -1;

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    private int iconId = 0;

    public Journey(Route r, Car c, Date date){
        route = r;
        car = c;
        dateOfJourney = date;
    }

    /**
     * TODO: make formula more refined in the future.
     * Calculates the carbon footprint in kg - formula is currently pretty basic
     * @return the carbon emissions that the car produces
     */
    public float calculateCarbonFootPrint(){

        //MAKE SHIFT BUS/SKYRAIN/WALKING ADDITION
        if(footPrintOverride != -1) {
            return footPrintOverride;
        }

        float cityDist = route.getCityDistInKm();
        float highDist = route.getHighDistInKm();
        float kmpgCity = convertMPGtoKMPG(car.getCity08());
        float kmpgHigh = convertMPGtoKMPG(car.getHwy08());
        float emissions = car.getCo2Tailpipe08();
        return (((cityDist/kmpgCity) + (highDist/kmpgHigh)) * emissions)/1000;
    }

    /**
     * Gets the route for the current journey
     * @return Route object
     */
    public Route getRoute(){
        return route;
    }

    /**
     * Edits the current route of the journey
     * @param changedRoute the new route that the user enters via the add route activity
     */
    public void editRoute(Route changedRoute){
        route.setCityDistInKm(changedRoute.getCityDistInKm());
        route.setHighDistInKm(changedRoute.getHighDistInKm());
        route.setRouteName(changedRoute.getRouteName());
    }

    /**
     * Changes the current car used for the journey
     * @param changedCar the new car
     */
    public void editCar(Car changedCar){
        car = changedCar;
    }

    /**
     * Gets the car for the current journey
     * @return Car object
     */
    public Car getCar(){
        return car;
    }

    /** Retrieves the date of the journey
     */
    public Date getDate(){
        return dateOfJourney;
    }

    /**
     * Formats the date into a string and returns it
     * @return the Date in a String Format
     */
    public String getDateInString(){
        return dateFormat.format(dateOfJourney);
    }

    /**
     * Edits the date of the journey
     * @param newDate - new date to replace the old one
     */
    public void editDate(Date newDate){
        dateOfJourney = newDate;
    }

    /**
     * Converts miles to km
     * @param mpg the miles per gallon for the car
     * @return the miles per gallon to km per gallon
     */
    private float convertMPGtoKMPG(int mpg){
        return mpg * MPG_TO_KM_CONVERT_RATE;
    }

    public void overrideFootPrint(float f) {
        footPrintOverride = f;
    }

    public String getJourneyDesc() {


        int type = getCar().getEngineId();
        String mode;

        if(type == -1) {
            mode = "Bus";
            return getDateInString() + "\n" + getRoute().getDescription() + "\n\t" + mode;
        }
        if(type == -2) {
            mode = "Walking";
            return getDateInString() + "\n" + getRoute().getDescription() + "\n\t" + mode;
        }
        if(type == -3) {
            mode = "Skytrain";
            return getDateInString() + "\n" + getRoute().getDescription() + "\n\t" + mode;
        }
        else {
            mode = getCar().getFormattedStr();
            return getDateInString() + "\n" + getRoute().getDescription() + "\n\t" + mode;
        }
    }


}
