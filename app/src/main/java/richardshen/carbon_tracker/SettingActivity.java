package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingActivity extends AppCompatActivity {
    private CarbonTrackerModel ctModel;
    private ArrayAdapter unitsAdapter = null;
    private String[] unitsList={"kg","tree-day","tree-year"};
    private int unitsNumber=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ctModel = CarbonTrackerModel.getInstance();
        setupButtons();
        setupSpinner();
    }

    private void setupSpinner() {
        Spinner carbonUnitsSpinner= (Spinner)findViewById(R.id.carbonUnit_spinner);
        unitsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, unitsList);
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carbonUnitsSpinner.setAdapter( unitsAdapter);
        carbonUnitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unitsNumber=position;

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setupButtons() {
        Button save_btn = (Button)findViewById(R.id.save_btn);

        save_btn.setTypeface(MainActivity.face);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitsNumber==0){
                    ctModel.resetCarbonUnits(0);
                }
                else if(unitsNumber==1){
                    ctModel.resetCarbonUnits(1);
                }
                else if(unitsNumber==2){
                    ctModel.resetCarbonUnits(2);
                }
                SharedPreferences settings = getSharedPreferences("shared_file", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("carbonUnitsSetting",unitsNumber );
                editor.commit();
                SettingActivity.this.finish();
            }
        });
        Button cancel_btn = (Button)findViewById(R.id.cancel_btn);

        cancel_btn.setTypeface(MainActivity.face);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SettingActivity.this.finish();
            }
        });

    }
    public static Intent makeIntent(Context context){
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
    }


}
