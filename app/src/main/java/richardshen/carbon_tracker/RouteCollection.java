package richardshen.carbon_tracker;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Class to hold all the routes in an ArrayList
public class RouteCollection implements Iterable<Route>{

    // ArrayList to hold all the routes
    private List<Route> routes = new ArrayList<>();

    //Method to add a route
    public void addRoute(Route route){
        routes.add(route);
    }

    //Method to delete a route at given index
    public void deleteRoute(int index){
        routes.remove(index);
    }

    //Method to edit a route
    public void editRoute(Route route, int indexOfRouteToEdit){
        routes.remove(indexOfRouteToEdit);
        routes.add(indexOfRouteToEdit, route);
    }

    @Override
    public Iterator<Route> iterator() {
        return routes.iterator();
    }

    //Retrieves a given route
    public Route getRoute(int index){
        return routes.get(index);
    }

    //Gets each route's description
    public ArrayList<String> getAllRouteDesc(){
        ArrayList<String> descriptions = new ArrayList<>(10);
        for (Route route : this){
            descriptions.add(route.getDescription());
        }
        return descriptions;
    }

    public List<Route> getAllRoutes() {
        return routes;
    }

    public int getSize(){
        return routes.size();
    }
}
