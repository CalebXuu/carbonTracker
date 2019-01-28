package richardshen.carbon_tracker;


import java.util.ArrayList;
import java.util.List;


public class CarCollection {
    //
    private int ID = 0;
    private int BRAND = 1;
    private int MODEL = 2;
    private int YEAR = 3;
    private int CYLINDERS = 4;
    private int FTYPE = 5;
    private int CITY08 = 6;
    private int HWY08 = 7;
    private int COM08 = 8;
    private int CO2 = 9;
    private int BAR = 10;
    private int DISPL = 11;
    private int DRIVE = 12;
    private int TRANY = 13;

    private String brandName;
    private ArrayList<Car> knownCars;

    public CarCollection(String bName) {
        this.brandName = bName;
        knownCars = new ArrayList<>(10);
    }

    public void addCar(String carData) {
        String[] fields = carData.split(",");

        int id = Integer.parseInt(fields[ID]);
        String name = fields[BRAND];
        String model = fields[MODEL];
        int year = Integer.parseInt(fields[YEAR]);

        String ftype = fields[FTYPE];
        int city = Integer.parseInt(fields[CITY08]);
        int hwy = Integer.parseInt(fields[HWY08]);
        int com = Integer.parseInt(fields[COM08]);

        int cyl;
        try {
            cyl = Integer.parseInt(fields[CYLINDERS]);
        } catch (NumberFormatException e) {
            cyl = 0;
        }

        Float co2;
        try {
            co2 = Float.parseFloat(fields[CO2]);
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
            co2 = 0.0f;
        }

        Double bar;
        try {
            bar = Double.parseDouble(fields[BAR]);
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
            bar = 0.0;
        }

        Double disp;
        try {
            disp = Double.parseDouble(fields[DISPL]);
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
            disp = 0.0;
        }

        String drive;
        try {
            drive = fields[DRIVE];
        }
        catch(ArrayIndexOutOfBoundsException e) {
            drive = "";
        }

        String trans;
        try {
            trans = fields[TRANY];
        }
        catch(ArrayIndexOutOfBoundsException e){
            trans = "";
        }

        Car newCar = new Car(id, name, model, year, cyl, ftype, city, hwy, com, co2, bar, disp, drive, trans);

        knownCars.add(newCar);
    }

    public String getBrandName() {
        return brandName;
    }

    public ArrayList<Car> searchCars(String brand, int year, String model){
        ArrayList<Car> foundCars = new ArrayList<>(5);

        for(Car c : knownCars) {
            if(c.getBrand().toLowerCase().equals(brand.toLowerCase())
                    && c.getYear() == year
                    && c.getModel().toLowerCase().equals(model.toLowerCase())) {
                foundCars.add(c);
            }
        }
        return foundCars;
    }

    public int getNumberOfCars(){
        return knownCars.size();
    }

    public Car getCarByID(int id){
        for(Car c : knownCars) {
            if(c.getEngineId() == id) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<String> getAllModels() {
        ArrayList<String> allModels = new ArrayList<>(getNumberOfCars());

        for(Car c : knownCars) {
            if(!(allModels.contains(c.getModel()))) {
                allModels.add(c.getModel());
            }
        }

        return allModels;
    }

    public String[] getCarDescriptions(){
        String strToBeReturned[] = new String[knownCars.size()];
        for(int i = 0; i < knownCars.size(); i++){
            strToBeReturned[i] = knownCars.get(i).toString();
        }
        return strToBeReturned;
    }

    public List<String> getCarDescriptionsInList(){
        List<String> stringsToBeReturned = new ArrayList<>(0);
        for(Car c: knownCars) {
            stringsToBeReturned.add(c.getFormattedStr());
        }
        return stringsToBeReturned;
    }

    public ArrayList<Car> searchCarsByBrandModel(String brand, String model) {
        ArrayList<Car> foundCars = new ArrayList<>(5);

        for(Car c : knownCars) {
            if(c.getBrand().equals(brand) && c.getModel().equals(model)) {
                foundCars.add(c);
            }
        }

        return foundCars;
    }

    public ArrayList<Integer> getAllYears() {
        ArrayList<Integer> allYears = new ArrayList<>(10);
        for(Car c : knownCars) {
            if(!(allYears.contains(c.getYear()))) {
                allYears.add(c.getYear());
            }
        }

        return allYears;
    }

    public ArrayList<Integer> getAllYearsForModel(String model) {
        ArrayList<Integer> allYears = new ArrayList<>(10);

        for(Car c : knownCars) {
            if(c.getName().toLowerCase().equals(model.toLowerCase())) {
                System.out.println("here");
                allYears.add(c.getYear());
            }
        }

        return allYears;
    }
}