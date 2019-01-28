package richardshen.carbon_tracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Activity that displays the current carbon footprint of all the journeys that the user has entered
 * things commented out are to be added in - will not compile until the singleton class is implemented
 */
public class CarbonFootprintActivity extends AppCompatActivity {

    private static final float AVERAGE_DAILY_CO2_EMISSIONS_IN_KG = 36.9863f;
    private static final float AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET = 32.7485f;

    /** For future iterations when the user is given an option to choose which type of chart to use
     */
    private enum ChartType{
        PIE_CHART,
        PIE_CHARTLAST28,
        PIE_CHARTLAST28ROUTE,
        BAR_CHARTLAST28,
        PIE_CHARTLAST365,
        PIE_CHARTLAST365ROUTE,
        BAR_CHARTLAST365,
    }

    private String[] chartTypes = {"SINGLE DAY", "LAST 28 DAYS - PIE CHART TRANSPORTATION MODE",
            "LAST 28 DAYS - PIE CHART ROUTE MODE", "LAST 28 DAYS - BAR CHART",
            "LAST 365 DAYS - PIE CHART TRANSPORTATION MODE", "LAST 365 DAYS - PIE CHART ROUTE MODE",
            "LAST 365 DAYS - BAR CHART"};
    private int[] pieColors = new int[]{R.color.colorPrimary, R.color.chartColourBrightGreen,
            R.color.chartColourDarkGreen, R.color.chartColourLightBlue, R.color.chartColourLightBlue2,
            R.color.chartColourLightBlue3};
    private int[] barChartColors = new int[] {R.color.chartColourBrightGreen,
            R.color.chartColourLightBlue , R.color.chartColourDarkGreen, R.color.chartColourLightBlue3};

    private boolean checkTipLeft;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private Date date;

    private CarbonTrackerModel ctModel;
    private JourneyCollection journeys;
    private UtilitiesCollection utilities;
    private ArrayList<Journey> pastJourneys = new ArrayList<>(10);
    private ArrayList<String> pastJourneyNames = new ArrayList<>(10);

    private float totalCityDist;
    private float totalHighDist;
    private float totalEmissions;
    private float carEmissions;
    private float busEmissions;
    private float skytrainEmissions;
    private float naturalGasEmissions;
    private float electricEmissions;

    //Data entries for Pie Charts
    private List<PieEntry> pieEntriesLast28DaysMODE;
    private List<PieEntry> pieEntriesLast28DaysROUTE;
    private List<PieEntry> pieEntriesLast365DaysMODE;
    private List<PieEntry> pieEntriesLast365DaysROUTE;

    private float lastMonthMaxCarbonEmission;
    private float lastMonthMaxCarbonEmission_tree_day;
    private float lastMonthMaxCarbonEmission_tree_year;
    private float lastYearMaxCarbonEmission;
    private float lastYearMaxCarbonEmission_tree_day;
    private float lastYearMaxCarbonEmission_tree_year;
    private float lastMonthTotalCarbonEmission;
    private float lastMonthTotalCarbonEmission_tree_day;
    private float lastMonthTotalCarbonEmission_tree_year;
    private float lastYearTotalCarbonEmission;
    private float lastYearTotalCarbonEmission_tree_day;
    private float lastYearTotalCarbonEmission_tree_year;


    private PieChart carbonChart;

    private int carbonUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carbon_footprint);

        ctModel = CarbonTrackerModel.getInstance();

        journeys = ctModel.getJourneys();
        utilities = ctModel.getUtilitiesCollection();
        carbonUnits=ctModel.getCarbonUnits();

        carbonChart = (PieChart) findViewById(R.id.carbonFootPrint_PieChart);
        pieEntriesLast28DaysMODE = createLastXDayPieDataMODE(28);
        pieEntriesLast28DaysROUTE = createLastXDayPieDataROUTE(28);
        pieEntriesLast365DaysMODE = createLastXDayPieDataMODE(365);
        pieEntriesLast365DaysROUTE = createLastXDayPieDataROUTE(365);

        //checkTips();

        calculateDataTotals();

        setupSpinner();
        setupDateSelector();
        setupTipsButton();

    }

    /**
     * Creates a pie chart for a single date, when the date is chosen
     */
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Button dateSelect = (Button) findViewById(R.id.btn_CarbonFootprint_DateSelect);
            dateSelect.setText(dateFormat.format(calendar.getTime()));
            date = calendar.getTime();
            List<PieEntry> entries = new ArrayList<>();
            carEmissions = 0;
            busEmissions = 0;
            skytrainEmissions = 0;
            try {
                getJourneyEmissionsAtDate(date);
                if (carEmissions != 0) {
                    if(carbonUnits==1){
                        if((carEmissions*1000/60)>=200) {
                            entries.add(new PieEntry(200, "Car Emissions(tree-day)"));
                        }
                        else if((carEmissions*1000/60)<200&&(carEmissions*1000/60)>0.1) {
                            entries.add(new PieEntry(carEmissions*1000/60, "Car Emissions(tree-day)"));
                        }
                        else {

                            entries.add(new PieEntry(1/10, "Car Emission(tree-day)"));
                        }

                    }
                    else if(carbonUnits==2){
                        if((carEmissions/22)>=200) {
                            entries.add(new PieEntry(200, "Car Emissions(tree-year)"));
                        }
                        else if((carEmissions/22)<200&&(carEmissions/22)>0.1) {
                            entries.add(new PieEntry(carEmissions*22, "Car Emissions(tree-year)"));
                        }
                        else {
                            entries.add(new PieEntry(1/10, "Car Emissions(tree-year)"));
                        }

                    }
                    else {
                        entries.add(new PieEntry(carEmissions, "Car Emissions(kg)"));
                    }
                }
                if (busEmissions != 0) {
                    if(carbonUnits==1){
                        if((busEmissions*1000/60)>=200) {
                            entries.add(new PieEntry(200, "Bus Emissions(tree-day)"));
                        }
                        else if((busEmissions*1000/60)<200&&(busEmissions*1000/60)>0.1) {
                            entries.add(new PieEntry(busEmissions*1000/60, "Bus Emissions(tree-day)"));
                        }
                        else {

                            entries.add(new PieEntry(1/10, "Bus Emission(tree-day)"));
                        }

                    }
                    else if(carbonUnits==2){
                        if((busEmissions/22)>=200) {
                            entries.add(new PieEntry(200, "Bus Emissions(tree-year)"));
                        }
                        else if((busEmissions/22)<200&&(busEmissions/22)>0.1) {
                            entries.add(new PieEntry(busEmissions/22, "Bus Emissions(tree-year)"));
                        }
                        else {
                            entries.add(new PieEntry(1/10, "Bus Emissions(tree-year)"));
                        }

                    }
                    else {
                        entries.add(new PieEntry(busEmissions, "Bus Emissions(kg)"));
                    }

                }
                if (skytrainEmissions != 0) {
                    if(carbonUnits==1){
                        if((skytrainEmissions*1000/60)>=200) {
                            entries.add(new PieEntry(200, "SkyTrain Emissions(tree-day)"));
                        }
                        else if((skytrainEmissions*1000/60)<200&&(skytrainEmissions*1000/60)>0.1) {
                            entries.add(new PieEntry(skytrainEmissions*1000/60, "SkyTrain Emissions(tree-day)"));
                        }
                        else {

                            entries.add(new PieEntry(1/10, "SkyTrain Emission(tree-day)"));
                        }

                    }
                    else if(carbonUnits==2){
                        if((skytrainEmissions/22)>=200) {
                            entries.add(new PieEntry(200, "SkyTrain Emissions(tree-year)"));
                        }
                        else if((skytrainEmissions/22)<200&&(skytrainEmissions/22)>0.1) {
                            entries.add(new PieEntry(skytrainEmissions/22, "SkyTrain Emissions(tree-year)"));
                        }
                        else {
                            entries.add(new PieEntry(1/10, "SkyTrain Emissions(tree-year)"));
                        }

                    }
                    else {
                        entries.add(new PieEntry(skytrainEmissions, "SkyTrain Emissions(kg)"));
                    }

                }

                if (getUtilitiesEmissionAtDate(date) != 0) {
                    if(carbonUnits==1){
                        if((getUtilitiesEmissionAtDate(date)*1000/60)>=200) {
                            entries.add(new PieEntry(200, "Utilities Emission(tree-day)"));
                        }
                        else if((getUtilitiesEmissionAtDate(date)*1000/60)<200&&(getUtilitiesEmissionAtDate(date)*1000/60)>0.1) {
                            entries.add(new PieEntry(getUtilitiesEmissionAtDate(date)*1000/60, "Utilities Emission(tree-day)"));
                        }
                        else {

                            entries.add(new PieEntry(1/10, "Utilities Emission(tree-day)"));
                        }

                    }
                    else if(carbonUnits==2){
                        if((getUtilitiesEmissionAtDate(date)/22)>=200) {
                            entries.add(new PieEntry(200, "Utilities Emission(tree-year)"));
                        }
                        else if((getUtilitiesEmissionAtDate(date)/22)<200&&(getUtilitiesEmissionAtDate(date)/22)>0.1) {
                            entries.add(new PieEntry(getUtilitiesEmissionAtDate(date)/22, "Utilities Emission(tree-year)"));
                        }
                        else {
                            entries.add(new PieEntry(1/10, "Utilities Emission(tree-year)"));
                        }

                    }
                    else {
                        entries.add(new PieEntry(getUtilitiesEmissionAtDate(date), "Utilities Emission(kg)"));
                    }

                }

                PieDataSet carbonDataSet = new PieDataSet(entries, getResources().getString(R.string.carbon_chart_title));
                PieData carbonData = new PieData(carbonDataSet);
                carbonDataSet.setColors(pieColors, CarbonFootprintActivity.this);

                carbonChart = (PieChart) findViewById(R.id.carbonFootPrint_PieChart);
                carbonChart.setData(carbonData);
                carbonDataSet.setValueTextSize(12f);
                carbonDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                //chart settings
                setPieChartSettings();

            } catch (NullPointerException e) {
            }
        }
    };

    private List<PieEntry> createLastXDayPieDataMODE(int daysBack) {
        resetEmissions();
        List<PieEntry> pieEntries = new ArrayList<>(10);
        Date date;
        for (int i = 0; i < daysBack; i++) {
            date = getLastXDate((daysBack - 1) - i);
            getJourneyEmissionsAtDate(date);
            calculateUtilitiesEmissionsAtDate(date);
        }
        try {
            if (carEmissions != 0) {
                if(carbonUnits==1){
                    if((carEmissions*1000/60)>=200) {
                        pieEntries.add(new PieEntry(200, "Car Emissions(tree-day)"));
                    }
                    else if((carEmissions*1000/60)<200&&(carEmissions*1000/60)>0.1) {
                        pieEntries.add(new PieEntry(carEmissions*1000/60, "Car Emissions(tree-day)"));
                    }
                    else {

                        pieEntries.add(new PieEntry(1/10, "Car Emission(tree-day)"));
                    }

                }
                else if(carbonUnits==2){
                    if((carEmissions/22)>=200) {
                        pieEntries.add(new PieEntry(200, "Car Emissions(tree-year)"));
                    }
                    else if((carEmissions/22)<200&&(carEmissions/22)>0.1) {
                        pieEntries.add(new PieEntry(carEmissions/22, "Car Emissions(tree-year)"));
                    }
                    else {
                        pieEntries.add(new PieEntry(1/10, "Car Emissions(tree-year)"));
                    }

                }
                else {
                    pieEntries.add(new PieEntry(carEmissions, "Car Emissions(kg)"));
                }
            }
            if (busEmissions != 0) {
                if(carbonUnits==1){
                    if((busEmissions*1000/60)>=200) {
                        pieEntries.add(new PieEntry(200, "Bus Emissions(tree-day)"));
                    }
                    else if((busEmissions*1000/60)<200&&(busEmissions*1000/60)>0.1) {
                        pieEntries.add(new PieEntry(busEmissions*1000/60, "Bus Emissions(tree-day)"));
                    }
                    else {

                        pieEntries.add(new PieEntry(1/10, "Bus Emission(tree-day)"));
                    }

                }
                else if(carbonUnits==2){
                    if((busEmissions/22)>=200) {
                        pieEntries.add(new PieEntry(200, "Bus Emissions(tree-year)"));
                    }
                    else if((busEmissions/22)<200&&(busEmissions/22)>0.1) {
                        pieEntries.add(new PieEntry(busEmissions/22, "Bus Emissions(tree-year)"));
                    }
                    else {
                        pieEntries.add(new PieEntry(1/10, "Bus Emissions(tree-year)"));
                    }

                }
                else {
                    pieEntries.add(new PieEntry(busEmissions, "Bus Emissions(kg)"));
                }

            }
            if (skytrainEmissions != 0) {
                if(carbonUnits==1){
                    if((skytrainEmissions*1000/60)>=200) {
                        pieEntries.add(new PieEntry(200, "SkyTrain Emissions(tree-day)"));
                    }
                    else if((skytrainEmissions*1000/60)<200&&(skytrainEmissions*1000/60)>0.1) {
                        pieEntries.add(new PieEntry(skytrainEmissions*1000/60, "SkyTrain Emissions(tree-day)"));
                    }
                    else {

                        pieEntries.add(new PieEntry(1/10, "SkyTrain Emission(tree-day)"));
                    }

                }
                else if(carbonUnits==2){
                    if((skytrainEmissions/22)>=200) {
                        pieEntries.add(new PieEntry(200, "SkyTrain Emissions(tree-year)"));
                    }
                    else if((skytrainEmissions/22)<200&&(skytrainEmissions/22)>0.1) {
                        pieEntries.add(new PieEntry(skytrainEmissions/22, "SkyTrain Emissions(tree-year)"));
                    }
                    else {
                        pieEntries.add(new PieEntry(1/10, "SkyTrain Emissions(tree-year)"));
                    }

                }
                else {
                    pieEntries.add(new PieEntry(skytrainEmissions, "SkyTrain Emissions(kg)"));
                }

            }
            if (naturalGasEmissions != 0) {
                if(carbonUnits==1){
                    if((naturalGasEmissions*1000/60)>=200) {
                        pieEntries.add(new PieEntry(200, "Natural Gas Bill Emissions(tree-day)"));
                    }
                    else if((naturalGasEmissions*1000/60)<200&&(naturalGasEmissions*1000/60)>0.1) {
                        pieEntries.add(new PieEntry(naturalGasEmissions*1000/60, "Natural Gas Bill Emissions(tree-day)"));
                    }
                    else {

                        pieEntries.add(new PieEntry(1/10, "Natural Gas Bill Emissions(tree-day)"));
                    }

                }
                else if(carbonUnits==2){
                    if((naturalGasEmissions/22)>=200) {
                        pieEntries.add(new PieEntry(200, "Natural Gas Bill Emissions(tree-year)"));
                    }
                    else if((naturalGasEmissions/22)<200&&(naturalGasEmissions/22)>0.1) {
                        pieEntries.add(new PieEntry(naturalGasEmissions/22, "Natural Gas Bill Emissions(tree-year)"));
                    }
                    else {
                        pieEntries.add(new PieEntry(1/10, "Natural Gas Bill Emissions(tree-year)"));
                    }

                }
                else {
                    pieEntries.add(new PieEntry(naturalGasEmissions, "Natural Gas Bill Emissions(kg)"));
                }

            }
            if (electricEmissions != 0) {
                if(carbonUnits==1){
                    if((electricEmissions*1000/60)>=200) {
                        pieEntries.add(new PieEntry(200, "Electric Bill Emissions(tree-day)"));
                    }
                    else if((electricEmissions*1000/60)<200&&(electricEmissions*1000/60)>0.1) {
                        pieEntries.add(new PieEntry(electricEmissions*1000/60, "Electric Bill Emissions(tree-day)"));
                    }
                    else {

                        pieEntries.add(new PieEntry(1/10, "Electric Bill Emissions(tree-day)"));
                    }

                }
                else if(carbonUnits==2){
                    if((electricEmissions/22)>=200) {
                        pieEntries.add(new PieEntry(200, "Electric Bill Emissions(tree-year)"));
                    }
                    else if((electricEmissions/22)<200&&(electricEmissions/22)>0.1) {
                        pieEntries.add(new PieEntry(electricEmissions/22, "Electric Bill Emissions(tree-year)"));
                    }
                    else {
                        pieEntries.add(new PieEntry(1/10, "Electric Bill Emissions(tree-year)"));
                    }

                }
                else {
                    pieEntries.add(new PieEntry(electricEmissions, "Electric Bill Emissions(kg)"));
                }
            }
        }
        catch(NullPointerException e){
        }
        return pieEntries;
    }

    private List<PieEntry> createLastXDayPieDataROUTE(int daysBack){
        resetEmissions();
        getJourneysInLastXDays(daysBack);
        List <PieEntry> pieEntries = new ArrayList<>(10);
        for (int i = 0; i < daysBack; i++) {
            date = getLastXDate((daysBack - 1) - i);
            calculateUtilitiesEmissionsAtDate(date);
        }
        try {
            if (naturalGasEmissions != 0) {
                if(carbonUnits==1){
                    if((naturalGasEmissions*1000/60)>=200) {
                        pieEntries.add(new PieEntry(200, "Natural Gas Bill Emissions(tree-day)"));
                    }
                    else if((naturalGasEmissions*1000/60)<200&&(naturalGasEmissions*1000/60)>0.1) {
                        pieEntries.add(new PieEntry(naturalGasEmissions*1000/60, "Natural Gas Bill Emissions(tree-day)"));
                    }
                    else {

                        pieEntries.add(new PieEntry(1/10, "Natural Gas Bill Emissions(tree-day)"));
                    }

                }
                else if(carbonUnits==2){
                    if((naturalGasEmissions/22)>=200) {
                        pieEntries.add(new PieEntry(200, "Natural Gas Bill Emissions(tree-year)"));
                    }
                    else if((naturalGasEmissions/22)<200&&(naturalGasEmissions/22)>0.1) {
                        pieEntries.add(new PieEntry(naturalGasEmissions/22, "Natural Gas Bill Emissions(tree-year)"));
                    }
                    else {
                        pieEntries.add(new PieEntry(1/10, "Natural Gas Bill Emissions(tree-year)"));
                    }

                }
                else {
                    pieEntries.add(new PieEntry(naturalGasEmissions, "Natural Gas Bill Emissions(kg)"));
                }

            }
            if (electricEmissions != 0) {
                if(carbonUnits==1){
                    if((electricEmissions*1000/60)>=200) {
                        pieEntries.add(new PieEntry(200, "Electric Bill Emissions(tree-day)"));
                    }
                    else if((electricEmissions*1000/60)<200&&(electricEmissions*1000/60)>0.1) {
                        pieEntries.add(new PieEntry(electricEmissions*1000/60, "Electric Bill Emissions(tree-day)"));
                    }
                    else {

                        pieEntries.add(new PieEntry(1/10, "Electric Bill Emissions(tree-day)"));
                    }

                }
                else if(carbonUnits==2){
                    if((electricEmissions/22)>=200) {
                        pieEntries.add(new PieEntry(200, "Electric Bill Emissions(tree-year)"));
                    }
                    else if((electricEmissions/22)<200&&(electricEmissions/22)>0.1) {
                        pieEntries.add(new PieEntry(electricEmissions/22, "Electric Bill Emissions(tree-year)"));
                    }
                    else {
                        pieEntries.add(new PieEntry(1/10, "Electric Bill Emissions(tree-year)"));
                    }

                }
                else {
                    pieEntries.add(new PieEntry(electricEmissions, "Electric Bill Emissions(kg)"));
                }
            }
        }
        catch (NullPointerException e){
        }

        for (String routeName: pastJourneyNames){
            float emissions = 0;
            for (Journey journey: pastJourneys){
                if (routeName == journey.getRoute().getRouteName()){
                    int carId = journey.getCar().getEngineId();
                    float distanceTravelled = journey.getRoute().getCityDistInKm() + journey.getRoute().getHighDistInKm();
                    if (carId > 0) {
                        emissions += journey.calculateCarbonFootPrint();
                    }
                    else if(carId == -1){
                        emissions += VehicleEmissions.getBusEmissions(distanceTravelled);
                    }
                    else if(carId == -3){
                        emissions += VehicleEmissions.getSkytrainEmissions(distanceTravelled);
                    }
                }
            }
            if(carbonUnits==1){
                if((emissions*1000/60)>=200) {
                    pieEntries.add(new PieEntry(200, routeName+"(tree-day)"));
                }
                else if((emissions*1000/60)<200&&(emissions*1000/60)>0.1) {
                    pieEntries.add(new PieEntry(emissions, routeName+"(tree-day)"));
                }
                else {

                    pieEntries.add(new PieEntry(1/10, routeName+"(tree-day)"));
                }

            }
            else if(carbonUnits==2){
                if((emissions/22)>=200) {
                    pieEntries.add(new PieEntry(200, routeName+"(tree-year)"));
                }
                else if((emissions/22)<200&&(emissions/22)>0.1) {
                    pieEntries.add(new PieEntry(emissions, routeName+"(tree-year)"));
                }
                else {
                    pieEntries.add(new PieEntry(1/10, routeName+"(tree-year"));
                }

            }
            else {
                pieEntries.add(new PieEntry(emissions, routeName+"(kg)"));
            }
        }
        return pieEntries;
    }

    private void getJourneysInLastXDays(int daysBack) {
        for (int i = 0; i < daysBack; i++) {
            Date date = getLastXDate(i);
            for (Journey journey: journeys){
                if (dateFormat.format(date).equals(dateFormat.format(journey.getDate()))) {
                    pastJourneys.add(journey);
                    if (!pastJourneyNames.contains(journey.getRoute().getRouteName())) {
                        pastJourneyNames.add(journey.getRoute().getRouteName());
                    }
                }
            }
        }
    }

    private void resetEmissions() {
        carEmissions = 0;
        busEmissions = 0;
        skytrainEmissions = 0;
        naturalGasEmissions = 0;
        electricEmissions = 0;
    }

    private void setPieChartSettings() {
        carbonChart.setCenterTextSize(14f);
        carbonChart.setEntryLabelTextSize(12f);
        carbonChart.setEntryLabelColor(Color.BLACK);
        carbonChart.setExtraOffsets(15, 10, 15, 10);
        carbonChart.animateX(2000);
        carbonChart.setCenterText("Carbon Footprint of All Journeys");
        carbonChart.getLegend().setEnabled(false);
        carbonChart.setDescription(null);
        carbonChart.invalidate();
    }

    private void setupDateSelector() {
        Button dateSelector = (Button) findViewById(R.id.btn_CarbonFootprint_DateSelect);
        dateSelector.setTypeface(MainActivity.face);

        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CarbonFootprintActivity.this, datePicker, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setupTipsButton(){
        Button showTips =(Button)findViewById(R.id.btn_ShowTips);

        showTips.setTypeface(MainActivity.face);

        showTips.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                lastMonthMaxCarbonEmission=(getMaxJourneyEmissionsInLastXDate(28)/1000)+getMaxUtilitiesEmissionsInLastXDate(28);
                lastMonthMaxCarbonEmission_tree_day=lastMonthMaxCarbonEmission*1000/60;
                lastMonthMaxCarbonEmission_tree_year=lastMonthMaxCarbonEmission/22;
                lastYearMaxCarbonEmission=(getMaxJourneyEmissionsInLastXDate(365)/1000)+getMaxUtilitiesEmissionsInLastXDate(365);
                lastYearMaxCarbonEmission_tree_day=lastYearMaxCarbonEmission*1000/60;
                lastYearMaxCarbonEmission_tree_year=lastYearMaxCarbonEmission/22;
                lastMonthTotalCarbonEmission=(getTotalJourneyEmissionsInLastXDate(28)/1000)+getTotalUtilitiesEmissionsInLastXDate(28);
                lastMonthTotalCarbonEmission_tree_day=lastMonthTotalCarbonEmission*1000/60;
                lastMonthTotalCarbonEmission_tree_year=lastMonthTotalCarbonEmission/22;
                lastYearTotalCarbonEmission=(getTotalJourneyEmissionsInLastXDate(365)/1000)+getTotalUtilitiesEmissionsInLastXDate(365);
                lastYearTotalCarbonEmission_tree_day=lastYearTotalCarbonEmission*1000/60;
                lastYearTotalCarbonEmission_tree_year=lastYearTotalCarbonEmission/22;
                checkTips(lastMonthMaxCarbonEmission,lastYearMaxCarbonEmission,lastMonthTotalCarbonEmission,lastYearTotalCarbonEmission);
            }
        });
    }

    private void checkTips(float lastMonthMaxCarbonEmission,float lastYearMaxCarbonEmission,float lastMonthTotalCarbonEmission, float lastYearTotalCarbonEmission) {
        checkTipLeft = false;

        if (lastMonthMaxCarbonEmission>36.99&&ctModel.checkNewTips(14) == true) {
            if(carbonUnits==1){
                if(lastMonthMaxCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last month is over 200 tree-day and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",14);
                    showTips("Your Max daily Carbon Emission in last month is over 200 tree-day and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }
                else if(lastMonthMaxCarbonEmission_tree_day>0.1&&lastMonthMaxCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",14);
                    showTips("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastMonthMaxCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last month is over 200 tree-year and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",14);
                    showTips("Your Max daily Carbon Emission in last month is over 200 tree-year and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }
                else if(lastMonthMaxCarbonEmission_tree_year>0.1&&lastMonthMaxCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",14);
                    showTips("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",14);
                showTips("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                checkTipLeft = true;
            }

        }
        else if (lastMonthMaxCarbonEmission<36.99&&ctModel.checkNewTips(24) == true) {
            if(carbonUnits==1){
                if(lastMonthMaxCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last month is over 200 tree-day and your carbon emission is lower than the average, ,please keep it!",24);
                    showTips("Your Max daily Carbon Emission in last month is over 200 tree-day and your carbon emission is lower than the average, ,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastMonthMaxCarbonEmission_tree_day>0.1&&lastMonthMaxCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_day + "tree-day , your carbon emission is lower than the average, please keep it!",24);
                    showTips("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_day + "tree-day , your carbon emission is lower than the average, ,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastMonthMaxCarbonEmission_tree_day<0.1) {
                    ctModel.addTip("Your Max daily Carbon Emission in last month is  less than 0.1 tree-day, your carbon emission is lower than the average,please keep it!",24);
                    showTips("Your Max daily Carbon Emission in last month is  less than 0.1 tree-day, your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastMonthMaxCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last month is over 200 tree-year, your carbon emission is lower than the average,please keep it!",24);
                    showTips("Your Max daily Carbon Emission in last month is over 200 tree-year,  your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastMonthMaxCarbonEmission_tree_year>0.1&&lastMonthMaxCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_year + "tree-year ,  your carbon emission is lower than the average,please keep it!",24);
                    showTips("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission_tree_year + "tree-year , your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastMonthMaxCarbonEmission_tree_year<0.1) {
                    ctModel.addTip("Your Max daily Carbon Emission in last month is  less than 0.1 tree-year, your carbon emission is lower than the average,please keep it!",24);
                    showTips("Your Max daily Carbon Emission in last month is  less than 0.1 tree-year, your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is lower than the average,please keep it!",24);
                showTips("Your Max daily Carbon Emission in last month is  " + lastMonthMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is lower than the average,please keep it!");
                checkTipLeft = true;
            }

        }
        else if (lastYearMaxCarbonEmission>36.99&&ctModel.checkNewTips(25) == true) {
            if(carbonUnits==1){
                if(lastYearMaxCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last year is over 200 tree-day and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",25);
                    showTips("Your Max daily Carbon Emission in last year is over 200 tree-day and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }
                else if(lastYearMaxCarbonEmission_tree_day>0.1&&lastYearMaxCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",25);
                    showTips("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastYearMaxCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last year is over 200 tree-year and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",25);
                    showTips("Your Max daily Carbon Emission in last year is over 200 tree-year and your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }
                else if(lastYearMaxCarbonEmission_tree_year>0.1&&lastYearMaxCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",25);
                    showTips("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.",25);
                showTips("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is higher than the average, please choose a more environmentally friendly lifestyle.");
                checkTipLeft = true;
            }
        }
        else if (lastYearMaxCarbonEmission<36.99&&ctModel.checkNewTips(26) == true) {
            if(carbonUnits==1){
                if(lastYearMaxCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last year is over 200 tree-day and your carbon emission is lower than the average,please keep it!",26);
                    showTips("Your Max daily Carbon Emission in last year is over 200 tree-day and your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastYearMaxCarbonEmission_tree_day>0.1&&lastYearMaxCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission_tree_day + "tree-day , your carbon emission is lower than the average,please keep it!",26);
                    showTips("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission_tree_day + "tree-day , your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastYearMaxCarbonEmission_tree_day<0.1) {
                    ctModel.addTip("Your Max daily Carbon Emission in last year is  less than 0.1 tree-day, your carbon emission is lower than the average,please keep it!",26);
                    showTips("Your Max daily Carbon Emission in last year is  less than 0.1 tree-day, your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastYearMaxCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Max daily Carbon Emission in last year is  200 tree-year, your carbon emission is lower than the average,please keep it!",26);
                    showTips("Your Max daily Carbon Emission in last year is  200 tree-year,  your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastYearMaxCarbonEmission_tree_year>0.1&&lastYearMaxCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission_tree_year + "tree-year ,  your carbon emission is lower than the average,please keep it!",26);
                    showTips("Your Max daily Carbon Emission in lastyear is  " + lastYearMaxCarbonEmission_tree_year + "tree-year , your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }
                else if(lastMonthMaxCarbonEmission_tree_year<0.1) {
                    ctModel.addTip("Your Max daily Carbon Emission in last year is  less than 0.1 tree-year, your carbon emission is lower than the average,please keep it!",26);
                    showTips("Your Max daily Carbon Emission in last year is  less than 0.1 tree-year, your carbon emission is lower than the average,please keep it!");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is lower than the average,please keep it!",26);
                showTips("Your Max daily Carbon Emission in last year is  " + lastYearMaxCarbonEmission + "kg , and the average of Canadian carbon emission is " + 36.99+"kg, your carbon emission is lower than the average,please keep it!");
                checkTipLeft = true;
            }

        }
        else if (lastMonthTotalCarbonEmission>1125&&ctModel.checkNewTips(27) == true) {
            if(carbonUnits==1){
                if(lastMonthTotalCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Total  Carbon Emission in last month is over 200 tree-day and your carbon emission is higher than the average, keep your everyday into low carbon style!",27);
                    showTips("Your Total  Carbon Emission in last month is over 200 tree-day and your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }
                else if(lastMonthTotalCarbonEmission_tree_day>0.1&&lastMonthTotalCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Total  Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!",27);
                    showTips("Your Total  Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastMonthTotalCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Total  Carbon Emission in last month  is over 200 tree-year and your carbon emission is higher than the average,keep your everyday into low carbon style!",27);
                    showTips("Your Total  Carbon Emission in last month  is over 200 tree-year and your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }
                else if(lastMonthTotalCarbonEmission_tree_year>0.1&&lastMonthTotalCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Total  Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!",27);
                    showTips("Your Total  Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 1125+"kg, your carbon emission is higher than the average, keep your everyday into low carbon style!",27);
                showTips("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 1125+"kg, your carbon emission is higher than the average,keep your everyday into low carbon style!!");
                checkTipLeft = true;
            }

        }
        else if (lastMonthTotalCarbonEmission<1125&&ctModel.checkNewTips(28) == true) {
            if(carbonUnits==1){
                if(lastMonthTotalCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Total Carbon Emission in last month is over 200 tree-day and your carbon emission is lower than the average, ,proud of you!",28);
                    showTips("Your Total Carbon Emission in last month is over 200 tree-day and your carbon emission is lower than the average, ,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastMonthTotalCarbonEmission_tree_day>0.1&&lastMonthTotalCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_day + "tree-day , your carbon emission is lower than the average, proud of you!",28);
                    showTips("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_day + "tree-day , your carbon emission is lower than the average, ,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastMonthTotalCarbonEmission_tree_day<0.1) {
                    ctModel.addTip("Your Total Carbon Emission in last month is  less than 0.1 tree-day, your carbon emission is lower than the average,proud of you!",28);
                    showTips("Your Total Carbon Emission in last month is  less than 0.1 tree-day, your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastMonthTotalCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Total Carbon Emission in last month is over 200 tree-year, your carbon emission is lower than the average,proud of you!",28);
                    showTips("Your Total Carbon Emission in last month is over 200 tree-year,  your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastMonthTotalCarbonEmission_tree_year>0.1&&lastMonthTotalCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_year + "tree-year ,  your carbon emission is lower than the average,proud of you!",28);
                    showTips("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission_tree_year + "tree-year , your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastMonthTotalCarbonEmission_tree_year<0.1) {
                    ctModel.addTip("Your Total Carbon Emission in last month is  less than 0.1 tree-year, your carbon emission is lower than the average,proud of you!",28);
                    showTips("Your Total Carbon Emission in last month is  less than 0.1 tree-year, your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 1125+"kg, your carbon emission is lower than the average, proud of you!",28);
                showTips("Your Total Carbon Emission in last month is  " + lastMonthTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 1125+"kg, your carbon emission is lower than the average,proud of you!");
                checkTipLeft = true;
            }


        }
        else if (lastYearTotalCarbonEmission>13500&&ctModel.checkNewTips(29) == true) {
            if(carbonUnits==1){
                if(lastYearTotalCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Total  Carbon Emission in last year is over 200 tree-day and your carbon emission is higher than the average, keep your everyday into low carbon style!",29);
                    showTips("Your Total  Carbon Emission in last year is over 200 tree-day and your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }
                else if(lastYearTotalCarbonEmission_tree_day>0.1&&lastYearTotalCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Total  Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!",29);
                    showTips("Your Total  Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_day + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastYearTotalCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Total  Carbon Emission in last year  is over 200 tree-year and your carbon emission is higher than the average,keep your everyday into low carbon style!",29);
                    showTips("Your Total  Carbon Emission in last year  is over 200 tree-year and your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }
                else if(lastYearTotalCarbonEmission_tree_year>0.1&&lastYearTotalCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Total  Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!",29);
                    showTips("Your Total  Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_year + "tree-day , your carbon emission is higher than the average, keep your everyday into low carbon style!");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Total Carbon Emission in last year is  " + lastYearTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 13500+"kg, your carbon emission is higher than the average, keep your everyday into low carbon style!",29);
                showTips("Your Total Carbon Emission in last year is  " + lastMonthTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 13500+"kg, your carbon emission is higher than the average,keep your everyday into low carbon style!!");
                checkTipLeft = true;
            }

        }
        else if (lastYearTotalCarbonEmission<13500&&ctModel.checkNewTips(30) == true) {
            if(carbonUnits==1){
                if(lastYearTotalCarbonEmission_tree_day>=200) {
                    ctModel.addTip("Your Total Carbon Emission in last year is over 200 tree-day and your carbon emission is lower than the average, ,proud of you!",30);
                    showTips("Your Total Carbon Emission in last year is over 200 tree-day and your carbon emission is lower than the average, ,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastYearTotalCarbonEmission_tree_day>0.1&&lastYearTotalCarbonEmission_tree_day<200){
                    ctModel.addTip("Your Total Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_day + " tree-day , your carbon emission is lower than the average, proud of you!",30);
                    showTips("Your Total Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_day + " tree-day , your carbon emission is lower than the average, ,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastYearTotalCarbonEmission_tree_day<0.1) {
                    ctModel.addTip("Your Total Carbon Emission in last year is  less than 0.1 tree-day, your carbon emission is lower than the average,proud of you!",30);
                    showTips("Your Total Carbon Emission in last year is  less than 0.1 tree-day, your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }

            }
            else if(carbonUnits==2){
                if(lastYearTotalCarbonEmission_tree_year>=200) {
                    ctModel.addTip("Your Total Carbon Emission in last year is over 200 tree-year, your carbon emission is lower than the average,proud of you!",30);
                    showTips("Your Total Carbon Emission in last year is over 200 tree-year,  your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastYearTotalCarbonEmission_tree_year>0.1&&lastYearTotalCarbonEmission_tree_year<200){
                    ctModel.addTip("Your Total Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_year + "tree-year ,  your carbon emission is lower than the average,proud of you!",30);
                    showTips("Your Total Carbon Emission in last year is  " + lastYearTotalCarbonEmission_tree_year + "tree-year , your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }
                else if(lastYearTotalCarbonEmission_tree_year<0.1) {
                    ctModel.addTip("Your Total Carbon Emission in last year is  less than 0.1 tree-year, your carbon emission is lower than the average,proud of you!",30);
                    showTips("Your Total Carbon Emission in last year is  less than 0.1 tree-year, your carbon emission is lower than the average,proud of you!");
                    checkTipLeft = true;
                }

            }
            else {
                ctModel.addTip("Your Total Carbon Emission in last year is  " + lastYearTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 13500+"kg, your carbon emission is lower than the average, Good of you!",30);
                showTips("Your Total Carbon Emission in last year is  " + lastYearTotalCarbonEmission + "kg , and the average of Canadian carbon emission is " + 13500+"kg, your carbon emission is lower than the average,Good of you!");
                checkTipLeft = true;
            }

        }
        ctModel.saveTips(this);
        if(checkTipLeft==false)
        {
        }
    }

    private void showTips(String tipsView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CarbonFootprintActivity.this);
        builder.setTitle("Tips");
        builder.setMessage(tipsView);
        builder.setNegativeButton("skip Tips", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Next Tip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkTips(lastMonthMaxCarbonEmission,lastYearMaxCarbonEmission,lastMonthTotalCarbonEmission,lastYearTotalCarbonEmission);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void calculateDataTotals() {
        Route route;
        for (Journey journey: journeys){
            route = journey.getRoute();
            totalCityDist += route.getCityDistInKm();
            totalHighDist += route.getHighDistInKm();
            totalEmissions += journey.calculateCarbonFootPrint();
        }
    }

    private void setupSpinner() {
        Spinner chartType = (Spinner) findViewById(R.id.CarbonFootprint_spinner_ChartType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CarbonFootprintActivity.this,
                android.R.layout.simple_spinner_item, chartTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chartType.setAdapter(adapter);

        chartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setupChart(ChartType.values()[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Method to display the charts given a certain type of chart value
     *
     * @param chartType - PIE CHART = 0, BAR_CHARTLAST28 = 1, BAR_CHARTLAST365 = 2, TABLE_CHART = 3
     */
    private void setupChart(ChartType chartType) {
        LinearLayout pieChartLayout = (LinearLayout) findViewById(R.id.CarbonFootprint_PieChartLayout);
        CombinedChart barChart = (CombinedChart) findViewById(R.id.carbonFootPrint_BarChart);
        PieChart pieChart = (PieChart) findViewById(R.id.carbonFootPrint_PieChart);
        TextView titleText = (TextView) findViewById(R.id.carbonFootprint_TitleText);
        Button dateSelect = (Button) findViewById(R.id.btn_CarbonFootprint_DateSelect);

        pieChart.setNoDataText("");
        barChart.setNoDataText("");
        barChart.setDescription(null);
        switch (chartType) {
            case PIE_CHART:

                pieChartLayout.bringToFront();
                ((View) pieChartLayout.getParent()).invalidate();
                barChart.setVisibility(View.INVISIBLE);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                //Clears the previous charts
                titleText.setVisibility(TextView.INVISIBLE);
                break;

            case PIE_CHARTLAST28:
                showPieChart(barChart, pieChart);

                PieDataSet carbonPieDataLast28Set = new PieDataSet(pieEntriesLast28DaysMODE,
                        getResources().getString(R.string.carbon_chart_title));
                PieData carbonData = new PieData(carbonPieDataLast28Set);
                carbonPieDataLast28Set.setColors(pieColors, CarbonFootprintActivity.this);

                pieChart.setData(carbonData);
                carbonPieDataLast28Set.setValueTextSize(12f);
                carbonPieDataLast28Set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                //chart settings
                setPieChartSettings();
                break;

            case PIE_CHARTLAST28ROUTE:
                showPieChart(barChart, pieChart);

                PieDataSet carbonPieDataLast28SetROUTE = new PieDataSet(pieEntriesLast28DaysROUTE,
                        "Your Carbon Footprint");
                PieData carbonDataROUTE = new PieData(carbonPieDataLast28SetROUTE);
                carbonPieDataLast28SetROUTE.setColors(pieColors, CarbonFootprintActivity.this);

                pieChart.setData(carbonDataROUTE);
                carbonPieDataLast28SetROUTE.setValueTextSize(12f);
                carbonPieDataLast28SetROUTE.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                setPieChartSettings();
                break;

            case BAR_CHARTLAST28:

                if (carbonUnits == 1) {
                    showBarChart(pieChartLayout, barChart);

                    //Sets the visibility of previous charts
                    titleText.setVisibility(TextView.INVISIBLE);
                    dateSelect.setVisibility(View.INVISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);

                    List<BarEntry> barEntriesLast28 = new ArrayList<>(10);
                    ArrayList<Entry> averageDailyCo2Last28 = new ArrayList<>();
                    ArrayList<Entry> targetDailyCo2Last28 = new ArrayList<>();
                    String[] xAxisLabels_28Days = new String[28];
                    //Get all utilities and journey emission entries on each day
                    for (int i = 0; i < 28; i++) {
                        resetEmissions();
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -27 + i);
                        Date dateOfEntry = calendar.getTime();
                        xAxisLabels_28Days[i] = dateFormat.format(dateOfEntry);
                        getJourneyEmissionsAtDate(dateOfEntry);
                        float utilitiesEmission = getUtilitiesEmissionAtDate(dateOfEntry);
                        averageDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG * 1000 / 60));
                        targetDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET * 1000 / 60));
                        barEntriesLast28.add(new BarEntry(i, new float[]{carEmissions * 1000 / 60, busEmissions * 1000 / 60, skytrainEmissions * 1000 / 60, utilitiesEmission * 1000 / 60}));


                    }

                    //add the entries to data set


                    BarDataSet barDataSetLast28 = new BarDataSet(barEntriesLast28, "Your Carbon Footprint in Last 28 Days(tree-day)");
                    barDataSetLast28.setStackLabels(new String[]{"Car Emissions(tree-day)", "Bus Emissions(tree-day)", "SkyTrain Emissions(tree-day)", "Utilities Emissions(tree-day)"});
                    barDataSetLast28.setColors(barChartColors, this);
                    BarData barDataLast28 = new BarData(barDataSetLast28);
                    LineData last28DayAverageCO2 = new LineData();
                    LineData last28DayTargetCO2 = new LineData();
                    LineDataSet last28DayTargetCO2set = new LineDataSet(targetDailyCo2Last28, "Target daily C02 per person(tree-day)");
                    LineDataSet last28AverageCO2set = new LineDataSet(averageDailyCo2Last28, "Average daily CO2 per person(tree-day)");
                    last28DayAverageCO2.addDataSet(last28AverageCO2set);
                    last28DayAverageCO2.addDataSet(last28DayTargetCO2set);


                    //Sets the X axis of the graph to show the dates
                    MyXAxisValueFormatter xAxisFormat = new MyXAxisValueFormatter(xAxisLabels_28Days);
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(xAxisFormat);

                    //display it
                    barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                            CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.LINE});
                    CombinedData data = new CombinedData();
                    data.setData(barDataLast28);
                    data.setData(last28DayAverageCO2);
                    barChart.setData(data);
                    barChart.invalidate();

                    break;
                } else if (carbonUnits == 2) {
                    showBarChart(pieChartLayout, barChart);

                    //Sets the visibility of previous charts
                    titleText.setVisibility(TextView.INVISIBLE);
                    dateSelect.setVisibility(View.INVISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);

                    List<BarEntry> barEntriesLast28 = new ArrayList<>(10);
                    ArrayList<Entry> averageDailyCo2Last28 = new ArrayList<>();
                    ArrayList<Entry> targetDailyCo2Last28 = new ArrayList<>();
                    String[] xAxisLabels_28Days = new String[28];
                    //Get all utilities and journey emission entries on each day
                    for (int i = 0; i < 28; i++) {
                        resetEmissions();
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -27 + i);
                        Date dateOfEntry = calendar.getTime();
                        xAxisLabels_28Days[i] = dateFormat.format(dateOfEntry);
                        getJourneyEmissionsAtDate(dateOfEntry);
                        float utilitiesEmission = getUtilitiesEmissionAtDate(dateOfEntry);
                        averageDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG / 22));
                        targetDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET / 22));
                        barEntriesLast28.add(new BarEntry(i, new float[]{carEmissions / 22, busEmissions / 22, skytrainEmissions / 22, utilitiesEmission / 22}));


                    }

                    //add the entries to data set


                    BarDataSet barDataSetLast28 = new BarDataSet(barEntriesLast28, "Your Carbon Footprint in Last 28 Days");
                    barDataSetLast28.setStackLabels(new String[]{"Car Emissions(tree-year)", "Bus Emissions(tree-year)", "SkyTrain Emissions(tree-year)", "Utilities Emissions(tree-year)"});
                    barDataSetLast28.setColors(barChartColors, this);
                    BarData barDataLast28 = new BarData(barDataSetLast28);
                    LineData last28DayAverageCO2 = new LineData();
                    LineData last28DayTargetCO2 = new LineData();
                    LineDataSet last28DayTargetCO2set = new LineDataSet(targetDailyCo2Last28, "Target daily C02 per person(tree-year)");
                    LineDataSet last28AverageCO2set = new LineDataSet(averageDailyCo2Last28, "Average daily CO2 per person(tree-year)");
                    last28DayAverageCO2.addDataSet(last28AverageCO2set);
                    last28DayAverageCO2.addDataSet(last28DayTargetCO2set);


                    //Sets the X axis of the graph to show the dates
                    MyXAxisValueFormatter xAxisFormat = new MyXAxisValueFormatter(xAxisLabels_28Days);
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(xAxisFormat);

                    //display it
                    barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                            CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.LINE});
                    CombinedData data = new CombinedData();
                    data.setData(barDataLast28);
                    data.setData(last28DayAverageCO2);
                    barChart.setData(data);
                    barChart.invalidate();

                    break;
                } else {
                    showBarChart(pieChartLayout, barChart);

                    //Sets the visibility of previous charts
                    titleText.setVisibility(TextView.INVISIBLE);
                    dateSelect.setVisibility(View.INVISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);

                    List<BarEntry> barEntriesLast28 = new ArrayList<>(10);
                    ArrayList<Entry> averageDailyCo2Last28 = new ArrayList<>();
                    ArrayList<Entry> targetDailyCo2Last28 = new ArrayList<>();
                    String[] xAxisLabels_28Days = new String[28];
                    //Get all utilities and journey emission entries on each day
                    for (int i = 0; i < 28; i++) {
                        resetEmissions();
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -27 + i);
                        Date dateOfEntry = calendar.getTime();
                        xAxisLabels_28Days[i] = dateFormat.format(dateOfEntry);
                        getJourneyEmissionsAtDate(dateOfEntry);
                        float utilitiesEmission = getUtilitiesEmissionAtDate(dateOfEntry);
                        if (carbonUnits == 1) {
                            averageDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG * 1000 / 60));
                            targetDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET * 1000 / 60));
                            barEntriesLast28.add(new BarEntry(i, new float[]{carEmissions * 1000 / 60, busEmissions * 1000 / 60, skytrainEmissions * 1000 / 60, utilitiesEmission * 1000 / 60}));
                        } else if (carbonUnits == 2) {
                            averageDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG / 22));
                            targetDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET / 22));
                            barEntriesLast28.add(new BarEntry(i, new float[]{carEmissions / 22, busEmissions / 22, skytrainEmissions / 22, utilitiesEmission / 22}));
                        } else {
                            averageDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG));
                            targetDailyCo2Last28.add(new Entry(i, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET));
                            barEntriesLast28.add(new BarEntry(i, new float[]{carEmissions, busEmissions, skytrainEmissions, utilitiesEmission}));
                        }

                    }

                    //add the entries to data set


                    BarDataSet barDataSetLast28 = new BarDataSet(barEntriesLast28, "Your Carbon Footprint in Last 28 Days");
                    barDataSetLast28.setStackLabels(new String[]{"Car Emissions(kg)", "Bus Emissions(kg)", "SkyTrain Emissions(kg)", "Utilities Emissions(kg)"});
                    barDataSetLast28.setColors(barChartColors, this);
                    BarData barDataLast28 = new BarData(barDataSetLast28);
                    LineData last28DayAverageCO2 = new LineData();
                    LineData last28DayTargetCO2 = new LineData();
                    LineDataSet last28DayTargetCO2set = new LineDataSet(targetDailyCo2Last28, "Target daily C02 per person(kg)");
                    LineDataSet last28AverageCO2set = new LineDataSet(averageDailyCo2Last28, "Average daily CO2 per person(kg)");
                    last28DayAverageCO2.addDataSet(last28AverageCO2set);
                    last28DayAverageCO2.addDataSet(last28DayTargetCO2set);


                    //Sets the X axis of the graph to show the dates
                    MyXAxisValueFormatter xAxisFormat = new MyXAxisValueFormatter(xAxisLabels_28Days);
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(xAxisFormat);

                    //display it
                    barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                            CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.LINE});
                    CombinedData data = new CombinedData();
                    data.setData(barDataLast28);
                    data.setData(last28DayAverageCO2);
                    barChart.setData(data);
                    barChart.invalidate();

                    break;
                }
            case PIE_CHARTLAST365:
                showPieChart(barChart, pieChart);

                PieDataSet carbonPieDataLast365Set = new PieDataSet(pieEntriesLast365DaysMODE,
                        getResources().getString(R.string.carbon_chart_title));
                PieData carbonPieDataLast365 = new PieData(carbonPieDataLast365Set);
                carbonPieDataLast365Set.setColors(pieColors, CarbonFootprintActivity.this);

                pieChart.setData(carbonPieDataLast365);
                carbonPieDataLast365Set.setValueTextSize(12f);
                carbonPieDataLast365Set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                //chart settings
                setPieChartSettings();
                break;

            case PIE_CHARTLAST365ROUTE:
                showPieChart(barChart, pieChart);
                PieDataSet carbonPieDataLast365RouteSet = new PieDataSet(pieEntriesLast365DaysROUTE,
                        "Your Carbon Footprint");
                carbonPieDataLast365RouteSet.setColors(pieColors, CarbonFootprintActivity.this);
                carbonPieDataLast365RouteSet.setValueTextSize(12f);
                carbonPieDataLast365RouteSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


                PieData carbonPieDataLast365Route = new PieData(carbonPieDataLast365RouteSet);
                pieChart.setData(carbonPieDataLast365Route);

                setPieChartSettings();
                break;

            case BAR_CHARTLAST365:
                if (carbonUnits == 1) {
                    showBarChart(pieChartLayout, barChart);

                    //Sets the previous charts to invisible
                    dateSelect.setVisibility(View.INVISIBLE);
                    titleText.setVisibility(TextView.VISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);

                    List<BarEntry> barEntries = new ArrayList<>(10);
                    ArrayList<Entry> averageDailyCo2LastYear = new ArrayList<>();
                    ArrayList<Entry> targetDailyCo2LastYear = new ArrayList<>();

                    //Get all utilities and journey emission entries on each day
                    float weeklyCarEmission = 0;
                    float weeklyBusEmission = 0;
                    float weeklyTrainEmission = 0;
                    float utilitiesEmission = 0;
                    int dataEntryCounter = 0;
                    String[] xAxisLabels_365Days = new String[53];
                    for (int i = 0; i < 365; i++) {
                        resetEmissions();
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -364 + i);
                        Date dateOfEntry = calendar.getTime();
                        getJourneyEmissionsAtDate(dateOfEntry);
                        weeklyCarEmission += carEmissions;
                        weeklyBusEmission += busEmissions;
                        weeklyTrainEmission += skytrainEmissions;
                        utilitiesEmission += getUtilitiesEmissionAtDate(dateOfEntry);

                        if (i % 7 == 0) {
                            averageDailyCo2LastYear.add(new Entry(dataEntryCounter, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG * 1000 / 60 * 7));
                            targetDailyCo2LastYear.add(new Entry(dataEntryCounter, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET * 1000 / 60 * 7));
                            barEntries.add(new BarEntry(dataEntryCounter, new float[]{weeklyCarEmission * 1000 / 60, weeklyBusEmission * 1000 / 60, weeklyTrainEmission * 1000 / 60, utilitiesEmission * 1000 / 60}));
                            xAxisLabels_365Days[dataEntryCounter] = dateFormat.format(dateOfEntry);
                            weeklyCarEmission = 0;
                            weeklyBusEmission = 0;
                            weeklyTrainEmission = 0;
                            utilitiesEmission = 0;
                            dataEntryCounter++;
                        }
                    }

                    //add the entries to chart
                    BarDataSet barDataSet = new BarDataSet(barEntries, "Your Carbon Footprint in Last 365 Days");
                    barDataSet.setStackLabels(new String[]{"Car Emissions(tree-day)", "Bus Emissions(tree-day)", "SkyTrain Emissions(tree-day)", "Utilities Emissions(tree-day)"});
                    barDataSet.setColors(barChartColors, this);
                    BarData barData = new BarData(barDataSet);
                    LineData last365DayAverageCO2 = new LineData();
                    LineDataSet last365AverageCO2set = new LineDataSet(averageDailyCo2LastYear, "Average daily CO2 per person(tree-day)");
                    LineDataSet last365TargetCO2set = new LineDataSet(targetDailyCo2LastYear, "Target weekly CO2 per person(tree-day)");
                    last365DayAverageCO2.addDataSet(last365AverageCO2set);
                    last365DayAverageCO2.addDataSet(last365TargetCO2set);

                    //formats the xAxis
                    XAxis xAxis365 = barChart.getXAxis();
                    xAxis365.setValueFormatter(new MyXAxisValueFormatter(xAxisLabels_365Days));

                    //displays it
                    barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                            CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.LINE});
                    CombinedData data365 = new CombinedData();
                    data365.setData(barData);
                    data365.setData(last365DayAverageCO2);
                    barChart.setData(data365);
                    barChart.invalidate();
                    break;

                } else if (carbonUnits == 2) {
                    showBarChart(pieChartLayout, barChart);

                    //Sets the previous charts to invisible
                    dateSelect.setVisibility(View.INVISIBLE);
                    titleText.setVisibility(TextView.VISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);

                    List<BarEntry> barEntries = new ArrayList<>(10);
                    ArrayList<Entry> averageDailyCo2LastYear = new ArrayList<>();
                    ArrayList<Entry> targetDailyCo2LastYear = new ArrayList<>();

                    //Get all utilities and journey emission entries on each day
                    float weeklyCarEmission = 0;
                    float weeklyBusEmission = 0;
                    float weeklyTrainEmission = 0;
                    float utilitiesEmission = 0;
                    int dataEntryCounter = 0;
                    String[] xAxisLabels_365Days = new String[53];
                    for (int i = 0; i < 365; i++) {
                        resetEmissions();
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -364 + i);
                        Date dateOfEntry = calendar.getTime();
                        getJourneyEmissionsAtDate(dateOfEntry);
                        weeklyCarEmission += carEmissions;
                        weeklyBusEmission += busEmissions;
                        weeklyTrainEmission += skytrainEmissions;
                        utilitiesEmission += getUtilitiesEmissionAtDate(dateOfEntry);

                        if (i % 7 == 0) {
                            averageDailyCo2LastYear.add(new Entry(dataEntryCounter, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG / 22 * 7));
                            targetDailyCo2LastYear.add(new Entry(dataEntryCounter, AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET / 22 * 7));
                            barEntries.add(new BarEntry(dataEntryCounter, new float[]{weeklyCarEmission / 22, weeklyBusEmission / 22, weeklyTrainEmission / 22, utilitiesEmission / 22}));
                            xAxisLabels_365Days[dataEntryCounter] = dateFormat.format(dateOfEntry);
                            weeklyCarEmission = 0;
                            weeklyBusEmission = 0;
                            weeklyTrainEmission = 0;
                            utilitiesEmission = 0;
                            dataEntryCounter++;
                        }
                    }

                    //add the entries to chart
                    BarDataSet barDataSet = new BarDataSet(barEntries, "Your Carbon Footprint in Last 365 Days");
                    barDataSet.setStackLabels(new String[]{"Car Emissions(tree-year)", "Bus Emissions(tree-year)", "SkyTrain Emissions(tree-year)", "Utilities Emissions(tree-year)"});
                    barDataSet.setColors(barChartColors, this);
                    BarData barData = new BarData(barDataSet);
                    LineData last365DayAverageCO2 = new LineData();
                    LineDataSet last365AverageCO2set = new LineDataSet(averageDailyCo2LastYear, "Average daily CO2 per person(tree-year)");
                    LineDataSet last365TargetCO2set = new LineDataSet(targetDailyCo2LastYear, "Target weekly CO2 per person(tree-year)");
                    last365DayAverageCO2.addDataSet(last365AverageCO2set);
                    last365DayAverageCO2.addDataSet(last365TargetCO2set);

                    //formats the xAxis
                    XAxis xAxis365 = barChart.getXAxis();
                    xAxis365.setValueFormatter(new MyXAxisValueFormatter(xAxisLabels_365Days));

                    //displays it
                    barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                            CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.LINE});
                    CombinedData data365 = new CombinedData();
                    data365.setData(barData);
                    data365.setData(last365DayAverageCO2);
                    barChart.setData(data365);
                    barChart.invalidate();
                    break;

                }
                else {
                    showBarChart(pieChartLayout, barChart);

                    //Sets the previous charts to invisible
                    dateSelect.setVisibility(View.INVISIBLE);
                    titleText.setVisibility(TextView.VISIBLE);
                    pieChart.setVisibility(View.INVISIBLE);

                List<BarEntry> barEntries = new ArrayList<>(10);
                ArrayList<Entry> averageDailyCo2LastYear = new ArrayList<>();
                ArrayList<Entry> targetDailyCo2LastYear = new ArrayList<>();

                    //Get all utilities and journey emission entries on each day
                    float weeklyCarEmission = 0;
                    float weeklyBusEmission = 0;
                    float weeklyTrainEmission = 0;
                    float utilitiesEmission = 0;
                    int dataEntryCounter = 0;
                    String[] xAxisLabels_365Days = new String[53];
                    for (int i = 0; i < 365; i++) {
                        resetEmissions();
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -364 + i);
                        Date dateOfEntry = calendar.getTime();
                        getJourneyEmissionsAtDate(dateOfEntry);
                        weeklyCarEmission += carEmissions;
                        weeklyBusEmission += busEmissions;
                        weeklyTrainEmission += skytrainEmissions;
                        utilitiesEmission += getUtilitiesEmissionAtDate(dateOfEntry);

                    if (i % 7 == 0) {
                        targetDailyCo2LastYear.add(new Entry(dataEntryCounter,
                                AVERAGE_DAILY_CO2_EMISSIONS_IN_KG_TARGET * 7));
                        averageDailyCo2LastYear.add(new Entry(dataEntryCounter,
                                AVERAGE_DAILY_CO2_EMISSIONS_IN_KG * 7));
                        barEntries.add(new BarEntry(dataEntryCounter, new float[]{weeklyCarEmission,
                                weeklyBusEmission, weeklyTrainEmission, utilitiesEmission}));
                        xAxisLabels_365Days[dataEntryCounter] = dateFormat.format(dateOfEntry);
                        weeklyCarEmission = 0;
                        weeklyBusEmission = 0;
                        weeklyTrainEmission = 0;
                        utilitiesEmission = 0;
                        dataEntryCounter++;
                    }
                }

                //add the entries to chart
                BarDataSet barDataSet = new BarDataSet(barEntries, "Your Carbon Footprint in Last 365 Days");
                barDataSet.setStackLabels(new String[]{"Car Emissions", "Bus Emissions", "SkyTrain Emissions", "Utilities Emissions"});
                barDataSet.setColors(barChartColors, this);
                BarData barData = new BarData(barDataSet);
                LineData last365DayAverageCO2 = new LineData();
                LineDataSet last365AverageCO2set = new LineDataSet(averageDailyCo2LastYear, "Average weekly CO2 per person");
                LineDataSet last365TargetCO2set = new LineDataSet(targetDailyCo2LastYear, "Target weekly CO2 per person");
                last365DayAverageCO2.addDataSet(last365AverageCO2set);
                last365DayAverageCO2.addDataSet(last365TargetCO2set);

                    //formats the xAxis
                    XAxis xAxis365 = barChart.getXAxis();
                    xAxis365.setValueFormatter(new MyXAxisValueFormatter(xAxisLabels_365Days));

                    //displays it
                    barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,
                            CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.LINE});
                    CombinedData data365 = new CombinedData();
                    data365.setData(barData);
                    data365.setData(last365DayAverageCO2);
                    barChart.setData(data365);
                    barChart.invalidate();
                    break;
                }
        }
    }

    /**
     * Sets other charts invisible and makes the pie chart visible
     * @param barChart
     * @param pieChart
     */
    private void showPieChart(CombinedChart barChart, PieChart pieChart) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        barChart.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.VISIBLE);
        pieChart.clear();
    }

    /**
     * Clears all previous bar charts and set the bar chart to be visible
     * @param pieChartLayout the parent layout of the bar chart
     * @param barChart the actual bar chart, used to bring it to the front and set visibility to true
     */
    private void showBarChart(LinearLayout pieChartLayout, CombinedChart barChart) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        barChart.clear();
        barChart.setVisibility(View.VISIBLE);
        pieChartLayout.bringToFront();
        barChart.bringToFront();
        ((View) barChart.getParent()).invalidate();
    }

    /**
     * Calculates the date before the current date
     * @param daysBack
     * @return Date Object, X date before the current date
     */
    private Date getLastXDate(int daysBack) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysBack);
        return cal.getTime();
    }

    /**
     * Calculates and returns the emissions of every journey x days before and up until today
     * @param days the amount of days to go back from today
     * @return float, the emissions from past x days up until today
     */
    private float getMaxJourneyEmissionsInLastXDate(int days){
        float journeyEmission = 0;
        Date date;
        for (int i = 0; i < days; i ++){
            date = getLastXDate(i);
            if(journeyEmission <= getTotalJourneyEmissionsAtDate(date)) {
                journeyEmission = getTotalJourneyEmissionsAtDate(date);
            }
        }
        return journeyEmission;
    }

    private float getTotalJourneyEmissionsInLastXDate(int days){
        float journeyEmission;
        float totalJourneyEmission=0;
        Date date;
        for (int i = 0; i < days; i ++){
            date = getLastXDate(i);
            journeyEmission = getTotalJourneyEmissionsAtDate(date);
            totalJourneyEmission=journeyEmission+totalJourneyEmission;
        }
        return totalJourneyEmission;
    }

    /**
     * Calculates and returns the emissions of every utilities x days before and up until today
     * @param days the amount of days to go back from today
     * @return float, the emissions from past x days up until today
     */
    private float getMaxUtilitiesEmissionsInLastXDate(int days){
        float utilitiesEmission = 0;
        Date date;
        for (int i = 0; i < days; i ++){
            date = getLastXDate(i);
            if(utilitiesEmission <= getUtilitiesEmissionAtDate(date)) {
                utilitiesEmission = getUtilitiesEmissionAtDate(date);
            }
        }
        return utilitiesEmission;
    }
    private float getTotalUtilitiesEmissionsInLastXDate(int days){
        float utilitiesEmission = 0;
        float totalUtilitiesEmission=0;
        Date date;
        for (int i = 0; i < days; i ++){
            date = getLastXDate(i);
            utilitiesEmission = getUtilitiesEmissionAtDate(date);
            totalUtilitiesEmission=utilitiesEmission+ totalUtilitiesEmission;
        }
        return totalUtilitiesEmission;
    }

    private float getTotalJourneyEmissionsAtDate(Date date){
        float journeyEmissionsAtDate = 0;
        for (Journey journey : journeys){
            if (dateFormat.format(date).equals(dateFormat.format(journey.getDate()))){
                journeyEmissionsAtDate += journey.calculateCarbonFootPrint();
            }
        }
        return journeyEmissionsAtDate;
    }

    private void getJourneyEmissionsAtDate(Date date){
        for (Journey journey : journeys){
            if (dateFormat.format(date).equals(dateFormat.format(journey.getDate()))){
                int carId = journey.getCar().getEngineId();
                float distanceTravelled = journey.getRoute().getCityDistInKm() + journey.getRoute().getHighDistInKm();
                if (carId > 0) {
                    carEmissions += journey.calculateCarbonFootPrint();
                }
                else if(carId == -1){
                    busEmissions += VehicleEmissions.getBusEmissions(distanceTravelled);
                }
                else if(carId == -3){
                    skytrainEmissions += VehicleEmissions.getSkytrainEmissions(distanceTravelled);
                }
            }
        }
    }

    private float getUtilitiesEmissionAtDate(Date date){
        float utilitiesEmissionsAtDate = 0;
        for(Utilities utility : utilities){
            if (date.after(utility.getStartDate()) && date.before(utility.getEndDate())){
                utilitiesEmissionsAtDate += utility.getDailyEmissions();
            }
        }
        return utilitiesEmissionsAtDate;
    }

    private void calculateUtilitiesEmissionsAtDate(Date date){
        for (Utilities utility: utilities){
            if(date.after(utility.getStartDate()) && date.before(utility.getEndDate()) ||
                    date.equals(utility.getStartDate()) || date.equals(utility.getEndDate())){
                naturalGasEmissions += utility.calculateNaturalGasEmissions();
                electricEmissions += utility.calculateElecticEmissions();
            }
        }
    }

    public static Intent makeIntent(Context context){
        Intent intent = new Intent(context, CarbonFootprintActivity.class);
        return intent;
    }
}
