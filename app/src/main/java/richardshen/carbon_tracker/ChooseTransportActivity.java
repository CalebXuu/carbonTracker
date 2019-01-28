package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class ChooseTransportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_transport);



        configureSpinner();

        setUpNextButton();

    }

    private void setUpNextButton() {
        Button nextBtn = (Button) findViewById(R.id.CTNextBtn);
        nextBtn.setTypeface(MainActivity.face);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner ctSpinner = (Spinner)findViewById(R.id.CTSpinner);

                String transMode = (String)ctSpinner.getSelectedItem();

                if(transMode.equals("Bus")) {
                    Intent intent = RouteActivity.makeIntent(ChooseTransportActivity.this, -1);
                    startActivity(intent);
                }
                if(transMode.equals("Walking")) {
                    Intent intent = RouteActivity.makeIntent(ChooseTransportActivity.this, -2);
                    startActivity(intent);
                }
                if(transMode.equals("Skytrain")) {
                    Intent intent = RouteActivity.makeIntent(ChooseTransportActivity.this, -3);
                    startActivity(intent);
                }
                if(transMode.equals("Car")) {
                    Intent intent = AddJourney.makeIntent(ChooseTransportActivity.this);
                    startActivity(intent);
                }
            }
        });
    }

    private void configureSpinner() {
        Spinner ctSpinner = (Spinner)findViewById(R.id.CTSpinner);

        ArrayList<String> transportModes = new ArrayList<>(5);

        transportModes.add(getString(R.string.walking));
        transportModes.add(getString(R.string.car));
        transportModes.add(getString(R.string.bus));
        transportModes.add(getString(R.string.skytrain));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChooseTransportActivity.this,
                android.R.layout.simple_spinner_item, transportModes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ctSpinner.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, ChooseTransportActivity.class);
    }
}
