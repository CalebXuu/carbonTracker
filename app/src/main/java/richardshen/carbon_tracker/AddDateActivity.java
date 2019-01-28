package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.Calendar;

public class AddDateActivity extends AppCompatActivity {
    public static final String DATE_YEAR="DATE YEAR - From Add Date Activity";
    public static final String DATE_MONTH="DATE MONTH - From Add Date Activity";
    public static final String DATE_DAY="DATE DAY - From Add Date Activity";
    public static final int REQUEST_CODE_GETMESSAGE = 101;
    private int year=1990;
    private int month=1;
    private int day=1;
    private int[] years=new int[30];
    private String[] yearList=new String[30];
    private int[] months=new int[12];
    private String[] monthList=new String[12];
    private int maxDaysByDate=1;



    private int[] days=new int[31];
    private String[] dayList=new String[31];
    private ArrayAdapter YearAdapter = null;
    private ArrayAdapter MonthAdapter = null;
    private ArrayAdapter DayAdapter = null;
    private ArrayAdapter DayAdapter1 = null;
    Intent Date_Data = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);
        setupIntArray();
        setupSpinners();
        setupButtons();
    }

    private void setupButtons() {
        Button saveBtn=(Button)findViewById(R.id.saveBtn1);

        saveBtn.setTypeface(MainActivity.face);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Data.putExtra(DATE_YEAR, year);
                Date_Data.putExtra(DATE_MONTH, month);
                Date_Data.putExtra(DATE_DAY, day);
                setResult(JourneyFootPrintActivity.RESULT_OK,Date_Data);
                AddDateActivity.this.finish();
            }
        });


        Button cancelBtn=(Button)findViewById(R.id.cancelBtn);

        cancelBtn.setTypeface(MainActivity.face);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(JourneyFootPrintActivity.RESULT_CANCELED,Date_Data);
                AddDateActivity.this.finish();
            }
        });
    }



    private void setupSpinners() {
        Spinner yearSpinner=(Spinner)findViewById(R.id.yearSpinner);
        YearAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, yearList);
        YearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter( YearAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year=years[position];
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Spinner monthSpinner=(Spinner)findViewById(R.id.monthSpinner);
        MonthAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, monthList);
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter( MonthAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month=months[position];
                maxDaysByDate = getDaysByYearMonth(year,month);
                //int[] newDays=new int[maxDaysByDate];
                //String[] newDayList=new String[maxDaysByDate];
                days=new int[maxDaysByDate];
                dayList=new String[maxDaysByDate];
                for(int i=1;i<maxDaysByDate+1;i++)
                {
                    days[i-1]=i;
                    dayList[i-1]= String.valueOf(i);
                }
                Spinner daySpinner=(Spinner)findViewById(R.id.daySpinner);
                DayAdapter = new ArrayAdapter(AddDateActivity.this, android.R.layout.simple_list_item_1, dayList);
                DayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                daySpinner.setAdapter( DayAdapter);
                daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        day=days[position];
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Spinner daySpinner=(Spinner)findViewById(R.id.daySpinner);
        DayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dayList);
        DayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter( DayAdapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                day=days[position];
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    private void setupIntArray() {
        for(int i=1990;i<2020;i++)
        {
            years[i-1990]= i;
            yearList[i-1990]= String.valueOf(i);
        }

        for(int i=1;i<13;i++)
        {
            months[i-1]=i;
            monthList[i-1]= String.valueOf(i);
        }
        for(int i=1;i<32;i++)
        {
            days[i-1]=i;
            dayList[i-1]= String.valueOf(i);
        }
    }

    public static Intent makeIntent(Context context){
        Intent intent = new Intent(context, AddDateActivity.class);
        return intent;
    }
}
