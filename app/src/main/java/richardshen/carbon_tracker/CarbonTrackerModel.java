package richardshen.carbon_tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CarbonTrackerModel {

    private static CarbonTrackerModel instance = new CarbonTrackerModel();
    public static CarbonTrackerModel getInstance(){
        return instance;
    }

    private RouteCollection routes = new RouteCollection();
    private JourneyCollection journeys = new JourneyCollection();
    private ArrayList<Car> userCars = new ArrayList<>();
    private UtilitiesCollection utilities = new UtilitiesCollection();
    private TipHistory tipList = new TipHistory();
    private SettingCollection settingCollection= new SettingCollection();

    /************************************************************
     * Methods to save/load the Route/Car/Journey Classes       *
     ************************************************************/

    //Use the following functions to sync between data saved in this model and database.
    //Use addXXXToDB() only after you've added the new journey into this model
    //Use updateSavedXXX() if you delete or modified(for Journey and Utilities) data. These methods will clean all data in database and rewrite it with ones in this model

    public void saveJourney(Context context){
        JourneyDBAdapter adapter = new JourneyDBAdapter(context);
        adapter.open();
        for(int i = 0; i < journeys.getSize(); i++){
            Journey journey = journeys.getJourney(i);
            adapter.insertRow(journey.getRoute().getRouteName(), journey.getCar().getName(), journey.getCar().getBrand(), journey.getCar().getEngineId(), journey.getDate(), journey.getIconId(), i+1);
        }
        adapter.close();

    }

    public void updateSavedJourney(Context context){
        JourneyDBAdapter adapter = new JourneyDBAdapter(context);
        adapter.open();
        adapter.deleteAll();
        saveJourney(context);
        adapter.close();
    }

    public void loadJourney(Context context){
        if(journeys.getSize()!=0){
            journeys = new JourneyCollection();
        }
        JourneyDBAdapter adapter = new JourneyDBAdapter(context);
        adapter.open();
        Cursor cursor = adapter.getAllRows();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(JourneyDBAdapter.COL_ROWID);
                String routeName = cursor.getString(JourneyDBAdapter.COL_ROUTENAME);
                String carName = cursor.getString(JourneyDBAdapter.COL_CARNAME);
                String carBrand = cursor.getString(JourneyDBAdapter.COL_CARBRAND);
                int engId = cursor.getInt(JourneyDBAdapter.COL_ENGID);
                SimpleDateFormat fromString = new SimpleDateFormat("dd/MM/yyyy");
                String dateInStr = cursor.getString(JourneyDBAdapter.COL_DATE);
                Date date = new Date();
                try {
                    date = fromString.parse(dateInStr);
                } catch (ParseException e) {
                    break;
                }
                Route routeToBeAdded = new Route();
                Car carToBeAdded = new Car();
                for(Route route:routes){
                    if(route.getRouteName().equals(routeName)){
                        routeToBeAdded = route;
                        break;
                    }
                }
                if(engId == -1){
                    carToBeAdded = new Car(-1, "Bus", "", 2017, 0, "Gasoline", 1, 1, 89, 0, 0, 0, "", "Automatic");
                }
                else if(engId == -2){
                    carToBeAdded = new Car(-2, "Walking", "", 2017, 0, "", 1, 1, 0, 0, 0, 0, "", "");
                }
                else if(engId == -3){
                    carToBeAdded = new Car(-3, "Skytrain", "", 2017, 0, "", 1, 1, 0, 0, 0, 0, "", "");
                }
                else {
                    for (Car car : userCars) {
                        if (car.getName().equals(carName) && car.getBrand().equals(carBrand) && car.getEngineId() == engId) {
                            carToBeAdded = car;
                            break;
                        }
                    }
                }
                Journey journeyToBeAdded = new Journey(routeToBeAdded, carToBeAdded, date);
                int iconId = cursor.getInt(JourneyDBAdapter.COL_ICON);
                journeyToBeAdded.setIconId(iconId);
                journeys.addJourney(journeyToBeAdded);
            }while(cursor.moveToNext());
        }
        adapter.close();
    }

    public void addJourneyToDB(Context context, Journey journey){
        JourneyDBAdapter adapter = new JourneyDBAdapter(context);
        adapter.open();
        adapter.insertRow(journey.getRoute().getRouteName(), journey.getCar().getName(), journey.getCar().getBrand(), journey.getCar().getEngineId(), journey.getDate(), journey.getIconId(), journeys.getSize());
        adapter.close();
    }

    public void saveCar(Context context){
        CarDBAdapter newAdapter = new CarDBAdapter(context);
        newAdapter.open();
        for (int i = 0; i < userCars.size(); i++) {
            Car currentCar = userCars.get(i);
            newAdapter.insertRow(
                    currentCar.getEngineId(), currentCar.getBrand(),
                    currentCar.getModel(), currentCar.getYear(),
                    currentCar.getCylinders(), currentCar.getFuelType(),
                    currentCar.getCity08(), currentCar.getHwy08(),
                    currentCar.getComb08(), currentCar.getCo2Tailpipe08(),
                    currentCar.getBarrels(), currentCar.getDispl(),
                    currentCar.getDrive(), currentCar.getTrany(),
                    currentCar.getName(), currentCar.getIconId(), i+1);
        }
        newAdapter.close();
    }

    public void loadCar(Context context){
        if(userCars.size() != 0){
            userCars = new ArrayList<>();
        }

        CarDBAdapter newAdapter = new CarDBAdapter(context);
        newAdapter.open();

        Cursor cursor = newAdapter.getAllRows();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(CarDBAdapter.COL_ROWID);
                int engId = cursor.getInt(CarDBAdapter.COL_ENGID);
                String brand = cursor.getString(CarDBAdapter.COL_BRNAD);
                String model = cursor.getString(CarDBAdapter.COL_MODEL);
                int year = cursor.getInt(CarDBAdapter.COL_YR);
                int cylinders = cursor.getInt(CarDBAdapter.COL_CYLINDERS);
                String fuelType = cursor.getString(CarDBAdapter.COL_FUELTYPE);
                int city08 = cursor.getInt(CarDBAdapter.COL_CTY08);
                int hwy08 = cursor.getInt(CarDBAdapter.COL_HWY08);
                int comb08 = cursor.getInt(CarDBAdapter.COL_COMB08);
                float co2Tailpipe08 = cursor.getFloat(CarDBAdapter.COL_CO2TAILPIPLINE);
                double barrels = cursor.getDouble(CarDBAdapter.COL_BARRELS);
                double displ = cursor.getDouble(CarDBAdapter.COL_DISPL);
                String drive = cursor.getString(CarDBAdapter.COL_DRIVE);
                String trany = cursor.getString(CarDBAdapter.COL_TRANY);
                String name = cursor.getString(CarDBAdapter.COL_NAME);
                int iconId = cursor.getInt(CarDBAdapter.COL_ICON);
                Car carToBeAdded = new Car(engId, brand, model, year, cylinders, fuelType, city08, hwy08, comb08, co2Tailpipe08, barrels, displ, drive, trany);
                carToBeAdded.setName(name);
                carToBeAdded.setIconId(iconId);
                userCars.add(carToBeAdded);
            }while(cursor.moveToNext());
        }
        newAdapter.close();

    }

    public void addNewCarToDB(Context context, Car newCar){
        CarDBAdapter newAdapter = new CarDBAdapter(context);
        newAdapter.open();
        newAdapter.insertRow(
                newCar.getEngineId(), newCar.getBrand(),
                newCar.getModel(), newCar.getYear(),
                newCar.getCylinders(), newCar.getFuelType(),
                newCar.getCity08(), newCar.getHwy08(),
                newCar.getComb08(), newCar.getCo2Tailpipe08(),
                newCar.getBarrels(), newCar.getDispl(),
                newCar.getDrive(), newCar.getTrany(),
                newCar.getName(), newCar.getIconId(), userCars.size());
        newAdapter.close();
    }

    public void editCarNameInDB(Context context, int index, String newName){
        CarDBAdapter adapter = new CarDBAdapter(context);
        adapter.open();
        adapter.updateRowName(index, newName);
        adapter.close();
    }

    public void updateSavedCars(Context context){
        CarDBAdapter adapter = new CarDBAdapter(context);
        adapter.open();
        adapter.deleteAll();
        saveCar(context);
        adapter.close();
    }

    public void loadRoute(Context context){
        if(routes.getSize()!=0){
            routes = new RouteCollection();
        }
        RouteDBAdapter adapter = new RouteDBAdapter(context);
        adapter.open();
        Cursor cursor = adapter.getAllRows();
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(RouteDBAdapter.COL_NAME);
                float hwyDist = cursor.getInt(RouteDBAdapter.COL_HWYDIST);
                float ctyDist = cursor.getInt(RouteDBAdapter.COL_CTYDIST);
                Route routeToBeAdded = new Route(ctyDist, hwyDist, name);
                routes.addRoute(routeToBeAdded);
            }while(cursor.moveToNext());
        }
        adapter.close();
    }

    public void saveRoute(Context context) {
        RouteDBAdapter adapter = new RouteDBAdapter(context);
        adapter.open();
        for(int i = 0; i < routes.getSize(); i++){
            Route route = routes.getRoute(i);
            adapter.insertRow(route.getCityDistInKm(), route.getHighDistInKm(), route.getRouteName(), i+1);
        }
        adapter.close();
    }

    public void addRouteToDB(Context context, Route route){
        RouteDBAdapter adapter = new RouteDBAdapter(context);
        adapter.open();
        adapter.insertRow(route.getCityDistInKm(), route.getHighDistInKm(), route.getRouteName(), routes.getSize());
        adapter.close();
    }

    public void updateSavedRoutes(Context context){
        RouteDBAdapter adapter = new RouteDBAdapter(context);
        adapter.open();
        adapter.deleteAll();
        saveRoute(context);
        adapter.close();
    }

    public void editRouteInDB(Context context, int index, Route route){
        RouteDBAdapter adapter = new RouteDBAdapter(context);
        adapter.open();
        adapter.updateRow(index, route.getCityDistInKm(), route.getHighDistInKm(), route.getRouteName());
        adapter.close();
    }

    public void saveUtilities(Context context){
        UtilitiesDBAdapter adapter = new UtilitiesDBAdapter(context);
        adapter.open();
        for(int i = 0; i < utilities.getSize(); i++){
            Utilities utility = utilities.getUtility(i);
            adapter.insertRow(utility.getStartDate(), utility.getEndDate(), utility.getElectricBillCost(), utility.getKiloWatts(), utility.getNaturalBillCost(), utility.getGigaJoules(), utility.getNumberOfPeople(), i+1);
        }
        adapter.close();
    }

    public void updateSavedUtilities(Context context){
        UtilitiesDBAdapter adapter = new UtilitiesDBAdapter(context);
        adapter.open();
        adapter.deleteAll();
        saveUtilities(context);
        adapter.close();
    }

    public void loadUtilities(Context context){
        UtilitiesDBAdapter adapter = new UtilitiesDBAdapter(context);
        adapter.open();
        Cursor cursor = adapter.getAllRows();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(UtilitiesDBAdapter.COL_ROWID);
                float elecBill = cursor.getFloat(UtilitiesDBAdapter.COL_ELECBILL);
                float kiloWatt = cursor.getFloat(UtilitiesDBAdapter.COL_KILOWATT);
                float naturalBill = cursor.getFloat(UtilitiesDBAdapter.COL_NATURALBILL);
                float gigaJoule = cursor.getFloat(UtilitiesDBAdapter.COL_GIGAJOULE);
                SimpleDateFormat fromString = new SimpleDateFormat("dd/MM/yyyy");
                String startDateInStr = cursor.getString(UtilitiesDBAdapter.COL_STARTDATE);
                String endDateInStr = cursor.getString(UtilitiesDBAdapter.COL_ENDDATE);
                float people = cursor.getFloat(UtilitiesDBAdapter.COL_HOUSEHOLDS);
                Date startDate = new Date();
                try {
                    startDate = fromString.parse(startDateInStr);
                } catch (ParseException e) {
                    break;
                }
                Date endDate = new Date();
                try {
                    endDate = fromString.parse(endDateInStr);
                } catch (ParseException e) {
                    break;
                }
                utilities.addUtility(new Utilities(startDate, endDate, elecBill, kiloWatt, naturalBill, gigaJoule, people));
            }while(cursor.moveToNext());
        }
        adapter.close();
    }

    public void addUtilityToDB(Context context, Utilities utility){
        UtilitiesDBAdapter adapter = new UtilitiesDBAdapter(context);
        adapter.open();
        adapter.insertRow(utility.getStartDate(), utility.getEndDate(), utility.getElectricBillCost(), utility.getKiloWatts(), utility.getNaturalBillCost(), utility.getGigaJoules(), utility.getNumberOfPeople(), utilities.getSize());
        adapter.close();
    }

    public void saveTips(Context context){
        String prefName = "Carbon Tracker Preferences";
        String prefixOfTips = "Tip ";
        String prefixOfIndexes = "Tip index ";
        SharedPreferences settings = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        ArrayList <String> listOfTips = getTips();
        int listOfIndexes[] = getTipCaseIndex();
        editor.putInt(prefixOfTips+"size", listOfTips.size());
        editor.putInt(prefixOfIndexes+"size", listOfIndexes.length);
        for(int i = 0; i < listOfTips.size(); i++){
            editor.putString(prefixOfTips+i, listOfTips.get(i));
        }
        for(int i = 0; i < listOfIndexes.length; i++){
            editor.putInt(prefixOfIndexes+i, listOfIndexes[i]);
        }
        editor.apply();

    }


    public void loadTips(Context context){
        if(getTips().size()!=0){
            tipList = new TipHistory();
        }
        if(getTipCaseIndex().length!=0){
            tipList = new TipHistory();
        }
        String prefName = "Carbon Tracker Preferences";
        String prefixOfTips = "Tip ";
        String prefixOfIndexes = "Tip index ";
        SharedPreferences settings = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        int sizeOfTipsList = settings.getInt(prefixOfTips + "size", 0);
        int sizeOfIndexesList = settings.getInt(prefixOfIndexes+"size", 0);
        ArrayList<String> tipsFromSaved = new ArrayList<>();
        int tipIndexesFromSaved[] = new int[7];
        for(int i = 0; i < 7; i++){
            tipIndexesFromSaved[i]=0;
        }
        for(int i = 0; i < sizeOfTipsList; i++){
            tipsFromSaved.add(settings.getString(prefixOfTips+i, ""));
        }
        for(int i = 0; i < sizeOfIndexesList; i++){
            tipIndexesFromSaved[i] = settings.getInt(prefixOfIndexes+i, 0);
        }
        if(sizeOfTipsList!=0){
            tipList.setTips(tipsFromSaved);
        }
        if(sizeOfIndexesList!=0){
            tipList.setTipCaseIndexs(tipIndexesFromSaved);
        }
        SharedPreferences settings2 = context.getSharedPreferences("shared_file", 0);
        int carbonUnits=settings2.getInt("carbonUnitsSetting", 0);
        resetCarbonUnits(carbonUnits);
    }

    /************************************************************
     * Methods to access the Route/Route Collection Classes     *
     ***********************************************************/
    public void addRoute(Route route){
        routes.addRoute(route);
    }

    public void removeRoute(int index){
        routes.deleteRoute(index);
    }

    public String[] getRouteDescriptions(){
        return (String[])routes.getAllRouteDesc().toArray();
    }

    public RouteCollection getRoutes(){
        return routes;
    }


    /***********************************************************
     * Methods to access the Car/Car Collection Classes        *
     ***********************************************************/
    public void addCar(Car newCar){
        userCars.add(newCar);
    }

    public ArrayList<Car> getUserCars() {
        return userCars;
    }

    /*****************************************************************
    * Methods to access the journeys                                *
    *****************************************************************/
    public JourneyCollection getJourneys(){
        return journeys;
    }

    public void addJourney(Journey journey){
        journeys.addJourney(journey);
    }

    public float getCOEmissions(int index){
        return journeys.getJourney(index).calculateCarbonFootPrint();
    }

    /*****************************************************************
     * Methods to access the utilities                               *
     *****************************************************************/
    public void addUtility(Utilities utility){
        utilities.addUtility(utility);
    }

    public void removeUtility(int index){
        utilities.deleteUtility(index);
    }

    public ArrayList<String> getUtiliyDescriptions(){
        return utilities.getAllUtilityDesc();
    }

    public Utilities getUtilities(int index){return utilities.getUtility(index);}

    public UtilitiesCollection getAllUtilities(){return utilities;}

    public UtilitiesCollection getUtilitiesCollection(){return utilities;
    }

    /*****************************************************************
     * Methods to access the tips                                   *
     *****************************************************************/
    public void addTip(String newTips,int caseIndex){
        tipList.addTip(newTips,caseIndex);}

    public void removeTip(int position){
        tipList.removeTip(position);}

    public void editTip(int position,String newTip,int newCaseIndex){
        tipList.editTip(position,newTip,newCaseIndex);}

    public int getTipListSize(){return tipList.getSize();}

    public ArrayList<String> getTips(){return tipList.getTips();}

    public int[] getTipCaseIndex(){return tipList.getTipCaseIndexs();}

    public boolean checkNewTips(int newCase){return tipList.checkNewTip(newCase);}

    /*****************************************************************
     * Methods to access the carbon units                                   *
     *****************************************************************/
    public int getCarbonUnits() {return settingCollection.getCarbonUnits();}

    public void resetCarbonUnits(int newCarbonUnits){settingCollection.resetCarbonUnits(newCarbonUnits);}

}