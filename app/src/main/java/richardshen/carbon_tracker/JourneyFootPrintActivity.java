package richardshen.carbon_tracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JourneyFootPrintActivity extends AppCompatActivity {

    public static final String CAR_INDEX = "CAR INDEX - From Route Activity";
    public static final String ROUTE_NAME = "ROUTE NAME - From Route Activity";
    public static final String ROUTE_CITY_DISTANCE = "ROUTE CITY DISTANCE - From Route Activity";
    public static final String ROUTE_HIGHWAY_DISTANCE = "ROUTE HIGHWAY DISTANCE - From Route Activity";
    public static final String DATE_YEAR="DATE YEAR - From Add Date Activity";
    public static final String DATE_MONTH="DATE MONTH - From Add Date Activity";
    public static final String DATE_DAY="DATE DAY - From Add Date Activity";
    public static final int REQUEST_CODE_GETMESSAGE = 101;

    private Route newRoute;
    private Car newTransMode;
    private CarbonTrackerModel ctModel;
    private Journey newJourney;
    private int year;
    private int month;
    private int day;
    TextView showtime;
    final int DATE_DIALOG = 1;
    int mYear, mMonth, mDay;
    boolean checkTipLeft;
    int index;
    float cityDistance,highwayDistance;

    private String[] icons = {"Icon 1", "Icon 2", "Icon 3", "Icon 4", "Icon 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_foot_print);


        ctModel = CarbonTrackerModel.getInstance();
        showtime=(TextView)findViewById(R.id.showtime);

        extractCarRouteData();

        setupCurrentDate();
        setupInformation();
        setupDatePicker();
        setupButtons();
        setupIconSpinner();
    }

    private void setupIconSpinner() {
        Spinner iconSpinner = (Spinner)findViewById(R.id.JFPSpinner);
        iconSpinner.setAdapter(new SpinnerAdapter(JourneyFootPrintActivity.this, R.layout.spinner_lay_out, icons));
    }

    /**
     * Gets the information for the Route and Car and displays it
     */
    private void extractCarRouteData() {
        Intent intent = getIntent() ;
        newRoute = new Route(intent.getFloatExtra(ROUTE_CITY_DISTANCE, 0),
                intent.getFloatExtra(ROUTE_HIGHWAY_DISTANCE, 0),
                intent.getStringExtra(ROUTE_NAME));

        index = intent.getIntExtra(CAR_INDEX,0);

        if(index == -1) {
            newTransMode = new Car(-1, "Bus", "", 2017, 0, "Gasoline", 1, 1, 89, 0, 0, 0, "", "Automatic");
        } else if(index == -2) {
            newTransMode = new Car(-2, "Walking", "", 2017, 0, "", 1, 1, 0, 0, 0, 0, "", "");
        } else if(index == -3) {
            newTransMode = new Car(-3, "Skytrain", "", 2017, 0, "", 1, 1, 0, 0, 0, 0, "", "");
        } else {
            newTransMode = ctModel.getUserCars().get(index);
        }

        newJourney = new Journey(newRoute, newTransMode, new Date());
    }
    private void setupCurrentDate() {
        long time=System.currentTimeMillis();
        final Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        year=mCalendar.get(Calendar.YEAR);
        month=mCalendar.get(Calendar.MONTH)+1;
        day=mCalendar.get(Calendar.DATE);
        showtime.setText(getString(R.string.current_date_yy_mm_dd_if_you_want_to_change, day, month, year));
    }
    private void setupButtons() {
        Button saveBtn = (Button) findViewById(R.id.saveDataBtn);
        final Spinner iconSpinner = (Spinner)findViewById(R.id.JFPSpinner);

        saveBtn.setTypeface(MainActivity.face);

        Button cancelSaveBtn =(Button)findViewById(R.id.cancelSaveBtn);

        cancelSaveBtn.setTypeface(MainActivity.face);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JourneyFootPrintActivity.this);
                builder.setTitle(R.string.hint);
                builder.setMessage(R.string.sure_to_save);
                builder.setNegativeButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //save the data
                        //Intent intent = new Intent(getApplicationContext(), menu.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        String newDate = "" + day + "/" + month + "/" + year;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                        try{
                            newJourney.editDate(dateFormat.parse(newDate));
                        }
                        catch(ParseException e){
                        }
                        highwayDistance=newRoute.getHighDistInKm();
                        cityDistance=newRoute.getCityDistInKm();

                        //startActivity(intent);

                        newJourney.setIconId(iconSpinner.getSelectedItemPosition());

                        ctModel.addJourney(newJourney);
                        ctModel.addJourneyToDB(JourneyFootPrintActivity.this, newJourney);
                        checkTips(index,cityDistance,highwayDistance);
                    }
                });
                builder.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        cancelSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    public void setupDatePicker(){
        Button dateBtn = (Button) findViewById(R.id.setdate);

        dateBtn.setTypeface(MainActivity.face);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(DATE_DIALOG);

            }

        });
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year1, int monthOfYear,
                              int dayOfMonth) {
            mYear = year1;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            mMonth++;
            showtime.setText(getString(R.string.now_the_date_is, mYear, mMonth, mDay));
            year=mYear;
            month=mMonth;
            day=mDay;
        }
    };

    public void setupInformation(){
        TextView carInformation=(TextView)findViewById(R.id.carInformation);
        float footprint = 0;
        float footprint_tree_day=0;
        float footprint_tree_year=0;

        int transMode = newTransMode.getEngineId();

        if(transMode == -1) {
            carInformation.setText(R.string.travelled_by_bus);
            footprint = VehicleEmissions.getBusEmissions(newRoute.getCityDistInKm() + newRoute.getHighDistInKm());
            newJourney.overrideFootPrint(footprint);

        } else if(transMode == -2) {
            carInformation.setText(R.string.walking);
            newJourney.overrideFootPrint(footprint);
        } else if (transMode == -3) {
            carInformation.setText(R.string.travelled_by_skytrain);
            footprint = VehicleEmissions.getSkytrainEmissions(newRoute.getCityDistInKm() + newRoute.getHighDistInKm());
            newJourney.overrideFootPrint(footprint);

        } else {
            carInformation.setText(newTransMode.getFormattedStr());
            footprint = newJourney.calculateCarbonFootPrint();
        }
        footprint_tree_day=(footprint*1000)/60;
        footprint_tree_year=footprint/22;

        TextView routeInformation=(TextView)findViewById(R.id.routeInformation);
        routeInformation.setText(newRoute.getDescription());
        int carbonUnits=ctModel.getCarbonUnits();
        TextView carbonEmission = (TextView) findViewById(R.id.carbonEmission);

        if(carbonUnits==1){
            if(footprint_tree_day>=200) {
                carbonEmission.setText(R.string.it_takes_over_200_tree_day_to_compensate);
            }
            else if(footprint_tree_day>0.1&&footprint_tree_day<200){
                carbonEmission.setText(getString(R.string.it_takes_x_tree_day, footprint_tree_day));
            }
            else if(footprint_tree_day<=0.1){
                carbonEmission.setText(R.string.it_takes_less_than_0_1_tree_day_to_compensate);
            }
        }
        else if(carbonUnits==2){
            if(footprint_tree_year>=200) {
                carbonEmission.setText(R.string.it_takes_over_200_tree_yr_to_compensate);
            }
            else if(footprint_tree_year>0.1&&footprint_tree_year<200){
                carbonEmission.setText(getString(R.string.it_takes_x_tree_yr, footprint_tree_year));
            }
            else if(footprint_tree_year<=0.1){
                carbonEmission.setText(R.string.it_takes_less_than_0_1_tree_yr_to_compensate);
            }
        }
        else {

            carbonEmission.setText(getString(R.string.x_kg_of_co2, footprint));
        }

    }

    public static Intent makeIntent(Context context, int carIndex, Route route){
        Intent intent = new Intent(context, JourneyFootPrintActivity.class);
        intent.putExtra(CAR_INDEX, carIndex);
        intent.putExtra(ROUTE_NAME, route.getRouteName());
        intent.putExtra(ROUTE_CITY_DISTANCE, route.getCityDistInKm());
        intent.putExtra(ROUTE_HIGHWAY_DISTANCE, route.getHighDistInKm());

        return intent;
    }
    public static Intent makeIntent2(Context context){
        Intent intent = new Intent(context, JourneyFootPrintActivity.class);

        return intent;
    }
    private void checkTips(int index,float cityDistance,float highwayDistance) {
        checkTipLeft = false;

        if (index!=-1&&index!=-2&&index!=-3&&ctModel.checkNewTips(1) == true) {
            ctModel.addTip(getString(R.string.tips1, newTransMode.getFormattedStr()),1);
            showTips(getString(R.string.tips1, newTransMode.getFormattedStr()));
            checkTipLeft = true;
        } else if (index!=-1&&index!=-2&&index!=-3&& ctModel.checkNewTips(2) == true) {
            ctModel.addTip(getString(R.string.tips2),2);
            showTips(getString(R.string.tips2));
            checkTipLeft = true;
        } else if (index!=-1&&index!=-2&&index!=-3&& ctModel.checkNewTips(4) == true) {
            ctModel.addTip(getString(R.string.tips3), 4);
            showTips(getString(R.string.tips3));
            checkTipLeft = true;
        } else if (index!=-1&&index!=-2&&index!=-3&&(cityDistance+highwayDistance)>20&&ctModel.checkNewTips(5) == true) {
            ctModel.addTip(getString(R.string.tips4, (cityDistance+highwayDistance)), 5);
            showTips(getString(R.string.tips4, (cityDistance+highwayDistance)));
            checkTipLeft = true;
        } else if (index!=-1&&index!=-2&&index!=-3&&ctModel.checkNewTips(6) == true) {
            ctModel.addTip(getString(R.string.tips5),6);
            showTips(getString(R.string.tips5));
            checkTipLeft = true;
        }else if (index!=-1&&index!=-2&&index!=-3&&(cityDistance+highwayDistance)<3&&ctModel.checkNewTips(7) == true) {
            ctModel.addTip(getString(R.string.tips6, (cityDistance+highwayDistance)),7);
            showTips(getString(R.string.tips6, (cityDistance+highwayDistance)));
            checkTipLeft = true;
        }else if (index==-1&&ctModel.checkNewTips(20) == true) {
            ctModel.addTip(getString(R.string.tips7),20);
            showTips(getString(R.string.tips7));
            checkTipLeft = true;
        }else if (index==-2&&ctModel.checkNewTips(21) == true) {
            ctModel.addTip(getString(R.string.tips8),21);
            showTips(getString(R.string.tips8));
            checkTipLeft = true;
        }else if (index==-3&&ctModel.checkNewTips(22) == true) {
            ctModel.addTip(getString(R.string.tips9),22);
            showTips(getString(R.string.tips9));
            checkTipLeft = true;
        }else if (index!=-1&&index!=-2&&index!=-3&&highwayDistance<cityDistance&&ctModel.checkNewTips(23) == true){
            ctModel.addTip(getString(R.string.tips10, cityDistance, highwayDistance),23);
            showTips(getString(R.string.tips10, cityDistance, highwayDistance));
            checkTipLeft = true;
        }
        ctModel.saveTips(this);

        if (checkTipLeft == false) {
            Intent intent = new Intent(getApplicationContext(), menu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }



    private void showTips(String tipsView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JourneyFootPrintActivity.this);
        builder.setTitle(R.string.tips);
        builder.setMessage(tipsView);
        builder.setNegativeButton(R.string.skip_tips, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setPositiveButton(R.string.next_tip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                   checkTips(index,cityDistance,highwayDistance);
            }

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get data from option activity
        switch (requestCode) {
            case REQUEST_CODE_GETMESSAGE:
                if (resultCode == Activity.RESULT_OK) {
                    year = data.getIntExtra(DATE_YEAR, 1990);
                    month = data.getIntExtra(DATE_MONTH, 1);
                    day = data.getIntExtra(DATE_DAY, 1);
                    TextView showtime = (TextView) findViewById(R.id.showtime);
                    showtime.setText("Year :" + year + " Month: " + month + " Day: " + day);
                    String newDate = "" + day + "/" + month + "/" + year;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    try {
                        newJourney.editDate(dateFormat.parse(newDate));
                    } catch (ParseException e) {
                        break;
                    }
                }
        }
    }

    private class SpinnerAdapter extends ArrayAdapter<String> {
        public SpinnerAdapter(Context context, int textViewResourceId,
                              String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_lay_out, parent, false);

            TextView label = (TextView) row.findViewById(R.id.SP_Text);
            label.setText(icons[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.SP_img);


            switch (icons[position]) {
                case "Icon 1":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j0));
                    break;
                case "Icon 2":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j1));
                    break;
                case "Icon 3":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j2));
                    break;
                case "Icon 4":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j3));
                    break;
                case "Icon 5":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j4));
                    break;
            }

            return row;
        }
    }

}