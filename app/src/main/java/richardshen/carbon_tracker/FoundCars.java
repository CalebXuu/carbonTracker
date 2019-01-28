package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FoundCars extends AppCompatActivity {
    private final int CAR_SELECTED = 0;
    private final int CANCEL = -1;

    private int brandPosInList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_cars);

        Intent intent = getIntent();

        brandPosInList = intent.getIntExtra("BRAND", -1);
        int year = intent.getIntExtra("YEAR", -1);
        String model = intent.getStringExtra("MODEL");


        CarCollection selectedBrand = MainActivity.allBrands.get(brandPosInList);
        System.out.println(selectedBrand.getBrandName() + " " + year + " " + model);

        ArrayList<Car> matchingCars = selectedBrand.searchCars(selectedBrand.getBrandName(), year, model);

        TextView stats = (TextView)findViewById(R.id.FCTitle);

        stats.setText(getResources().getString(R.string.we_found_0_cars_in_out_database) + matchingCars.size()
                + getResources().getString(R.string.we_found_2));

        if(matchingCars.isEmpty()) {
            stats.setText(getResources().getString(R.string.we_found_0_cars_in_out_database) + matchingCars.size() +
                    getResources().getString(R.string.we_found_2));
            matchingCars = selectedBrand.searchCarsByBrandModel(selectedBrand.getBrandName(), model);
        }

        setupList(matchingCars);
    }

    private void setupList(final ArrayList<Car> matchingCars) {
        List<String> allCars = new ArrayList<String>(10);

        for(Car c : matchingCars) {
            allCars.add(c.getFormattedStr());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FoundCars.this, android.R.layout.simple_list_item_1,
                allCars);
        ListView list = (ListView)findViewById(R.id.FCList);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Car selectedCar = matchingCars.get(position);

                Intent intent = PersonalizeCar.makeIntent(FoundCars.this);

                intent.putExtra("BRAND", brandPosInList);
                intent.putExtra("CAR ID", selectedCar.getEngineId());

                startActivityForResult(intent, CAR_SELECTED);
            }
        });
    }


    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context,FoundCars.class);

        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(resultCode) {
            case CAR_SELECTED:
                setResult(CAR_SELECTED, data);
                finish();

            case CANCEL:
                setResult(CANCEL);
                finish();
        }

    }

    @Override
    public void onBackPressed() {
        setResult(2);
        finish();
    }
}
