package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddCarScreen
        extends AppCompatActivity {
    private int REQUEST_CODE_GETMESSAGE3=1012;

    private final int CAR_SELECTED = 0;
    private final int CANCEL = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_screen);
        setupcancelBtn();

        Spinner brandName = (Spinner)findViewById(R.id.ACBrandSpinner);

        List<String> brands = new ArrayList<>(10);
        for(CarCollection cc : MainActivity.allBrands) {
            brands.add(cc.getBrandName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCarScreen.this,
                android.R.layout.simple_spinner_item, brands);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        brandName.setAdapter(adapter);
        setUpSpinners(brandName);
        setUpSearchBtn();
    }

    private void setUpSearchBtn() {
        Button button = (Button)findViewById(R.id.ACSearchCarBtn);

        button.setTypeface(MainActivity.face);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner yearSpinner = (Spinner)findViewById(R.id.ACYearSpinner);

                String yearStr = yearSpinner.getSelectedItem().toString();

                int year = Integer.parseInt(yearStr);

                Intent intent = FoundCars.makeIntent(AddCarScreen.this);

                Spinner brandName = (Spinner)findViewById(R.id.ACBrandSpinner);
                Spinner model = (Spinner)findViewById(R.id.ACModelSpinner);

                intent.putExtra("BRAND", brandName.getSelectedItemPosition());
                intent.putExtra("MODEL", model.getSelectedItem().toString());
                intent.putExtra("YEAR", year);

                startActivityForResult(intent, CAR_SELECTED);
            }
        });
    }

    private void setUpSpinners(final Spinner brandName) {
        brandName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner models = (Spinner)findViewById(R.id.ACModelSpinner);

                CarCollection selectedCol = MainActivity.allBrands.get(position);

                ArrayList<String> allModels = selectedCol.getAllModels();

                Collections.sort(allModels);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCarScreen.this,
                        android.R.layout.simple_spinner_item, allModels);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                models.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner models = (Spinner)findViewById(R.id.ACModelSpinner);
        models.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int brandPos = brandName.getSelectedItemPosition();

                CarCollection selectedCol = MainActivity.allBrands.get(brandPos);

                String model = models.getSelectedItem().toString();

                ArrayList<Car> allCars = selectedCol.searchCarsByBrandModel(selectedCol.getBrandName(), model);
                ArrayList<Integer> allYears = new ArrayList<Integer>();

                for(Car c : allCars) {
                    if(!(allYears.contains(c.getYear()))) {
                        allYears.add(c.getYear());
                    }
                }

                Collections.sort(allYears);

                ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(AddCarScreen.this,
                        android.R.layout.simple_spinner_item, allYears);

                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Spinner years = (Spinner)findViewById(R.id.ACYearSpinner);

                years.setAdapter(yearAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupcancelBtn() {
        Button btnCancel = (Button) findViewById(R.id.ACCancelBtn);

        btnCancel.setTypeface(MainActivity.face);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(CANCEL);
                finish();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(resultCode) {
            case CAR_SELECTED:
                setResult(REQUEST_CODE_GETMESSAGE3, data);
                finish();

            case CANCEL:
                setResult(CANCEL);
                finish();
        }

    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context,AddCarScreen.class);

        return intent;
    }

    @Override
    public void onBackPressed() {
        setResult(CANCEL);
        finish();
    }
}