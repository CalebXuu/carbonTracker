package richardshen.carbon_tracker;

import android.icu.util.DateInterval;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Class to handle both electric and gas utilities, they are combined into one
 */
public class Utilities {

    //conversion rate of CO2 in KG per KWH
    private static final float CO2inKG_PER_KWH = 0.009f;
    //conversion rate of CO2 in KG per GJ
    private static final float CO2inKG_PER_GJ = 56.1f;

    private Date startDate;
    private Date endDate;
    private long dayDifference;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

    private float electricBillCost;
    private float naturalBillCost;
    private float totalBillCost;
    private float dailyBillCost;
    private float numberOfPeople;

    private float kiloWatts;
    private float gigaJoules;
    private float dailyEmissions;

    public Utilities(Date billStartDate, Date billEndDate, float electricBill, float gW,
                     float naturalBill, float gJ, float people){
        setStartDate(billStartDate);
        setEndDate(billEndDate);
        setElectricBillCost(electricBill);
        setKiloWatts(gW);
        setNaturalBillCost(naturalBill);
        setGigaJoules(gJ);
        setTotalBillCost(naturalBill + electricBill);
        dayDifference = getDateDifference();
        setDailyBillCost(calculateDailyCost());
        setNumberOfPeople(people);
        setDailyEmissions(calculateDailyEmissions());
    }

    /**
     * Gets the date difference between the start and end date that was entered for the utility bill
     * @return long: the days between the start and end date of bill
     */
    private long getDateDifference() {
        long startMilis = startDate.getTime();
        long endMilis = endDate.getTime();
        long dayDiffInMilis = endMilis - startMilis;
        return TimeUnit.DAYS.convert(dayDiffInMilis, TimeUnit.MILLISECONDS);
    }

    /**
     * Calculates the daily cost of the utility bill
     * @return the daily cost of the bill
     */
    private float calculateDailyCost() {
        return totalBillCost/dayDifference;
    }

    /**
     * Calculates the CO2 emissions per day from the gas and electric bill consumption
     * @return CO2 emission in KG per day
     */
    private float calculateDailyEmissions() {
        return ((gigaJoules*CO2inKG_PER_GJ) + (kiloWatts * CO2inKG_PER_KWH)) / numberOfPeople;
    }

    /**
     * Calculates the user's total share of natural gas emissions for the given bill
     * @return CO2 emissions in KG
     */
    public float calculateNaturalGasEmissions(){
        return (gigaJoules*CO2inKG_PER_GJ) / numberOfPeople;
    }

    /**
     * Calculates the user's total share of electric bill emissions
     * @return CO2 emissions in KG
     */
    public float calculateElecticEmissions(){
        return (kiloWatts * CO2inKG_PER_KWH) / numberOfPeople;
    }

    public String getDescription(){
        return "Bill Duration: " + dateFormat.format(startDate) + " - " + dateFormat.format(endDate) + "\n"
                + "Electricity Consumption: " + kiloWatts + "KWh" + "\t" + "Cost: " + electricBillCost + " \n" +
                "Natural Gas Consumption: " + gigaJoules + "GJ" + "\t" + "Cost: $" + naturalBillCost + "\n"
                + "Daily Emissions: " + String.format("%.2f", dailyEmissions) +"kg/CO2 per day";
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public float getElectricBillCost() {
        return electricBillCost;
    }

    public void setElectricBillCost(float electricBillCost) {
        this.electricBillCost = electricBillCost;
    }

    public float getNaturalBillCost() {
        return naturalBillCost;
    }

    public void setNaturalBillCost(float naturalBillCost) {
        this.naturalBillCost = naturalBillCost;
    }

    public void setTotalBillCost(float totalBillCost) {
        this.totalBillCost = totalBillCost;
    }

    public void setDailyBillCost(float dailyBillCost) {
        this.dailyBillCost = dailyBillCost;
    }

    public float getKiloWatts() {
        return kiloWatts;
    }

    public void setKiloWatts(float kiloWatts) {
        this.kiloWatts = kiloWatts;
    }

    public float getGigaJoules() {
        return gigaJoules;
    }

    public void setGigaJoules(float gigaJoules) {
        this.gigaJoules = gigaJoules;
    }

    public float getDailyEmissions() {
        return dailyEmissions;
    }

    public void setDailyEmissions(float dailyEmissions) {
        this.dailyEmissions = dailyEmissions;
    }
    public float getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(float numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }
}
