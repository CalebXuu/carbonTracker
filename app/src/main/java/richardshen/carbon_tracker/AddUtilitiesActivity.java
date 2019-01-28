package richardshen.carbon_tracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddUtilitiesActivity extends AppCompatActivity {

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private CarbonTrackerModel ctModel;
    private boolean checkTipLeft;
    private float elecCost,elecUsage,gasCost,gasUsage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_utilities);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);

        ctModel = CarbonTrackerModel.getInstance();

        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_utilities_activity_actionbar, menu);
        return true;
    }

    /**
     * Sets up the buttons on the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //saves the current utility bill
            case R.id.bar_btn_save_utility:
                try{
                    Date startDate = getBillStartDate();
                    Date endDate = getBillEndDate();
                    elecCost = getElectricCost();
                    elecUsage = getElectricConsumption();
                    gasCost = getGasCost();
                    gasUsage = getGasConsumption();
                    float people = getPeople();
                    Utilities newUtility = new Utilities(startDate, endDate, elecCost, elecUsage, gasCost, gasUsage, people);
                    ctModel.addUtility(newUtility);
                    ctModel.addUtilityToDB(AddUtilitiesActivity.this, newUtility);
                    checkTips(elecCost, elecUsage, gasCost, gasUsage);
                } catch (NullPointerException e) {
                    Toast.makeText(AddUtilitiesActivity.this, R.string.enterStartDateEndDate, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bar_btn_cancel_add:
                finish();
                break;
        }
        return true;
    }

    /**
     * Creates the calendar dialog window for the user to select the date
     */
    DatePickerDialog.OnDateSetListener startDatePicker = new DatePickerDialog.OnDateSetListener(){
        @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                EditText startDate = (EditText) findViewById(R.id.in_AddUtil_StartDate);
            startDate.setText(dateFormat.format(calendar.getTime()));
            }
    };

    DatePickerDialog.OnDateSetListener endDatePicker = new DatePickerDialog.OnDateSetListener(){
        @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                EditText endDate = (EditText) findViewById(R.id.in_AddUtil_EndDate);
            endDate.setText(dateFormat.format(calendar.getTime()));
            }
    };

    /**
     * Sets up the buttons of the activity
     */
    private void setupButtons() {
        Button btnAddBill = (Button) findViewById(R.id.btn_AddUtilities_Done);

        btnAddBill.setTypeface(MainActivity.face);

        btnAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Date startDate = getBillStartDate();
                    Date endDate = getBillEndDate();
                    elecCost = getElectricCost();
                    elecUsage = getElectricConsumption();
                    gasCost = getGasCost();
                    gasUsage = getGasConsumption();
                    float people = getPeople();
                    Utilities newUtility = new Utilities(startDate, endDate, elecCost, elecUsage, gasCost, gasUsage, people);
                    ctModel.addUtility(newUtility);
                    ctModel.addUtilityToDB(AddUtilitiesActivity.this, newUtility);
                    checkTips(elecCost, elecUsage, gasCost, gasUsage);
                } catch (NullPointerException e) {
                    Toast.makeText(AddUtilitiesActivity.this, R.string.enterStartDateEndDate, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btn_AddUtilities_Cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCancel.setTypeface(MainActivity.face);

        EditText addStartDate = (EditText) findViewById(R.id.in_AddUtil_StartDate);
        addStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddUtilitiesActivity.this, startDatePicker, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        EditText addEndDate = (EditText) findViewById(R.id.in_AddUtil_EndDate);
        addEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddUtilitiesActivity.this, endDatePicker, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void checkTips(float elecCost,float elecUsage,float gasCost,float gasUsage) {
        checkTipLeft = false;

        if (elecCost>15&&ctModel.checkNewTips(8) == true) {
            ctModel.addTip(getString(R.string.ElecCost) + elecCost + getString(R.string.dollar) + elecUsage+getString(R.string.kwHLed),8);
            showTips(getString(R.string.ELECcOSTIS) + elecCost + getString(R.string.DOLeLUSAGE) + elecUsage+"KwH, LED lights contain no harmful materials and provide the same amount of light as a 60-watt incandescent, using up to 85 percent less power.");
            checkTipLeft = true;
        }
        else if (elecCost>15&&ctModel.checkNewTips(9) == true) {
            ctModel.addTip(getString(R.string.ELECuSAGE) + elecUsage+getString(R.string.kwhProg), 9);
            showTips(getString(R.string.elecUsage) + elecUsage+getString(R.string.costs));
            checkTipLeft = true;
        }
        else if (elecCost>15&&ctModel.checkNewTips(10) == true) {
            ctModel.addTip(getString(R.string.eCost) + elecCost + getString(R.string.dolelcus) + elecUsage+getString(R.string.kwhEn),10);
            showTips(getString(R.string.elecostis) + elecCost + getString(R.string.uselec) + elecUsage+getString(R.string.kwhEnable));
            checkTipLeft = true;
        }
        else if (elecCost>15&&ctModel.checkNewTips(11) == true) {
            ctModel.addTip(getString(R.string.elecCostHigh),11);
            showTips(getString(R.string.elecSo));
            checkTipLeft = true;
        }
        else if (elecUsage>1000&&ctModel.checkNewTips(19)==true){
            ctModel.addTip(getString(R.string.dskjn) + elecCost + getString(R.string.ds) + elecUsage+getString(R.string.dsf),19);
            showTips(getString(R.string.ea) + elecCost + getString(R.string.dol) + elecUsage+getString(R.string.dsff));
            checkTipLeft = true;
        }
         else if (gasUsage>242&&ctModel.checkNewTips(12)==true){
            ctModel.addTip(getString(R.string.csd) + gasCost + getString(R.string.dolgas) + gasUsage+getString(R.string.usnatgas),12);
            showTips(getString(R.string.gc) + gasCost + getString(R.string.dolda) + gasUsage+getString(R.string.consNat));
            checkTipLeft = true;
        }
        ctModel.saveTips(this);


        if (checkTipLeft == false) {
            finish();
        }
    }

    private void showTips(String tipsView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddUtilitiesActivity.this);
        builder.setTitle(R.string.TIps);
        builder.setMessage(tipsView);
        builder.setNegativeButton(R.string.skiptips, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(R.string.nextTip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkTips(elecCost,elecUsage,gasCost,gasUsage);
            }

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Gets the start of the user's bill
     * @return Date: the start date of the bill
     */
    private Date getBillStartDate(){
        EditText startDate = (EditText) findViewById(R.id.in_AddUtil_StartDate);
        try{
            Date date = dateFormat.parse(startDate.getText().toString());
            return date;
        } catch(ParseException e){
            return null;
        }
    }

    /**
     * Gets the end date of the utilities bill from user input box
     * @return Date : the end date of the bill
     */
    private Date getBillEndDate(){
        EditText endDate = (EditText) findViewById(R.id.in_AddUtil_EndDate);
        try{
            Date date = dateFormat.parse(endDate.getText().toString());
            return date;
        } catch(ParseException e){
            return null;
        }
    }

    /**
     * Gets the amount of people in the current household of the user from the editText input box
     * @return float: the number of people in the household
     */
    private float getPeople() {
        EditText households = (EditText) findViewById(R.id.in_AddUtil_NumberOfHouseholds);
        return Float.parseFloat(households.getText().toString());
    }

    /**
     * Gets the cost of the user's electrical costs
     * @return float: the cost of the user's electric bill for the given days
     */
    private float getElectricCost(){
        EditText elecCost = (EditText) findViewById(R.id.in_AddUtil_ElectricCost);
        if (elecCost.getText().toString().isEmpty()){
            return 0;
        }
        else{
            return Float.parseFloat(elecCost.getText().toString());
        }
    }

    /**
     * Gets the user's electric consumption in KW/h
     * @return float: electric consumption in KW/h
     */
    private float getElectricConsumption(){
        EditText elecConsumption = (EditText) findViewById(R.id.in_AddUtil_ElectricConsumption);
        if (elecConsumption.getText().toString().isEmpty()){
            return 0;
        }
        else{
            return Float.parseFloat(elecConsumption.getText().toString());
        }
    }

    /**
     * Gets the user's gas bill cost
     * @return float: gets the dollar amount of gas costs
     */
    private float getGasCost(){
        EditText gasCost = (EditText) findViewById(R.id.in_AddUtil_GasCost);
        if (gasCost.getText().toString().isEmpty()){
            return 0;
        }
        else{
            return Float.parseFloat(gasCost.getText().toString());
        }
    }

    /**
     * Gets the user's gas consumption from editText input box
     * @return float: gas consumption in gigaJoules (GJ)
     */
    private float getGasConsumption(){
        EditText gasConsumption = (EditText) findViewById(R.id.in_AddUtil_GasConsumption);
        if (gasConsumption.getText().toString().isEmpty()){
            return 0;
        }
        else{
            return Float.parseFloat(gasConsumption.getText().toString());
        }
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, AddUtilitiesActivity.class);
    }
}
