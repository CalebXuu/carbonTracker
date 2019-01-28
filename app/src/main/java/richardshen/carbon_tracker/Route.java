package richardshen.carbon_tracker;

/**
 * Class to handle a single route
 */
public class Route {

    //Distance of the Route in km (assumption, can switch to miles) of city driving
    private float cityDistInKm;

    //Distance of highway driving (km)
    private float highDistInKm;

    //Name of the Route
    private String routeName;

    public Route (){
        cityDistInKm = 0;
        highDistInKm = 0;
    }

    public Route(float city, float highway, String name){
        cityDistInKm = city;
        highDistInKm = highway;
        routeName = name;
    }

    //Gets the highway distance
    public float getHighDistInKm() {
        return highDistInKm;
    }

    //Sets the highway distance
    public void setHighDistInKm(float highDistInKm) {
        this.highDistInKm = highDistInKm;
    }

    //Gets city distance
    public float getCityDistInKm() {
        return cityDistInKm;
    }

    //Sets City Distance
    public void setCityDistInKm(float cityDistInKm) {
        this.cityDistInKm = cityDistInKm;
    }

    //Gets route name
    public String getRouteName() {
        return routeName;
    }

    //Sets route name
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    //Gets the Route description
    public String getDescription() {
        return "Route Name: " + routeName + "\n"
                + "City Distance: " + cityDistInKm + " km\n"
                + "Highway Distance: " + highDistInKm + " km";
    }
}
