package richardshen.carbon_tracker;

import java.io.Serializable;

public class Car implements Serializable{
    private int engineId;
    private String brand;
    private String model;
    private int year;
    private int cylinders;
    private String fuelType;
    private int city08;
    private int hwy08;
    private int comb08;
    private float co2Tailpipe08;
    private double barrels;
    private double displ;
    private String drive;
    private String trany;
    private String name = "";
    private int iconId = 0;

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public Car(int engineId, String brand, String model, int year, int cylinders, String fuelType, int city08, int hwy08, int comb08, float co2Tailpipe08, double barrels, double displ, String drive, String trany) {
        this.engineId = engineId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.cylinders = cylinders;
        this.fuelType = fuelType;
        this.city08 = city08;
        this.hwy08 = hwy08;
        this.comb08 = comb08;
        this.co2Tailpipe08 = co2Tailpipe08;
        this.barrels = barrels;
        this.displ = displ;
        this.drive = drive;
        this.trany = trany;
    }

    public Car(Car otherCar) {
        this.engineId = otherCar.engineId;
        this.brand = otherCar.brand;
        this.model = otherCar.model;
        this.year = otherCar.year;
        this.cylinders = otherCar.cylinders;
        this.fuelType = otherCar.fuelType;
        this.city08 = otherCar.city08;
        this.hwy08 = otherCar.hwy08;
        this.comb08 = otherCar.comb08;
        this.co2Tailpipe08 = otherCar.co2Tailpipe08;
        this.barrels = otherCar.barrels;
        this.displ = otherCar.displ;
        this.drive = otherCar.drive;
        this.trany = otherCar.trany;
    }
    public Car() {}

    public int getEngineId() {
        return engineId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getCylinders() {
        return cylinders;
    }

    public String getFuelType() {
        return fuelType;
    }

    public int getCity08() {
        return city08;
    }

    public int getHwy08() {
        return hwy08;
    }

    public int getComb08() {
        return comb08;
    }

    public float getCo2Tailpipe08() {
        return co2Tailpipe08;
    }

    public double getBarrels() {
        return barrels;
    }

    public double getDispl() {
        return displ;
    }

    public String getDrive() {
        return drive;
    }

    public String getTrany() {
        return trany;
    }

    public String toString(){
        return getBrand() + "-" + getModel() + "-" + getYear();
    }

    public String getFormattedStr() {
        return brand + " " + model + " (" + year + ") \n   "
                + "Transmission: " + trany + "\n   "
                + "Cyclinders: " + cylinders + "\n   "
                + "Fuel type: " + fuelType + "\n   "
                + "Drive: " + drive + "\n   ";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
