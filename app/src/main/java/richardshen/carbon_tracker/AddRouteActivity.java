package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.NoRouteToHostException;

public class AddRouteActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTE_NAME = "AddRouteActivity - New Route Name";
    public static final String EXTRA_ROUTE_CITY_DISTANCE_IN_KM = "AddRouteActivity - New Route City Distance in km";
    public static final String EXTRA_ROUTE_HIGHWAY_DISTANCE_IN_KM = "AddRouteActivity - New Route Highway Distance in km";

    private CarbonTrackerModel ctModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        ctModel = CarbonTrackerModel.getInstance();

        setupButtons();
    }

    /**
     * Creates and displays the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_route_activity_actionbar, menu);
        return true;
    }


    /**
     * Sets up the buttons on the action bar
     * @param item - bar_btn_confirm_add: confirms the addition and saving of the route
     *             bar_btn_cancel_route: cancels the activity and closes it
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.bar_btn_confirm_add:
                String routeName = getRouteName();
                if (routeName.isEmpty()){
                    Toast.makeText(AddRouteActivity.this, R.string.enterValidName, Toast.LENGTH_SHORT).show();
                }
                else{
                    float CityDistanceKm = getCityDistInKm();
                    float HighDistanceKm = getHighDistInKm();
                    Route routeToBeAdded = new Route(CityDistanceKm, HighDistanceKm, routeName);
                    ctModel.addRoute(routeToBeAdded);
                    ctModel.addRouteToDB(AddRouteActivity.this, routeToBeAdded);

                    finish();
                }
                break;
            case R.id.bar_btn_cancel_route:
                finish();
                break;
        }
        return true;
    }

    /**
     * Sets up the buttons for the activity
     */
    private void setupButtons() {
        Button finishRouteAddBtn = (Button) findViewById(R.id.ARDoneBtn);

        finishRouteAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routeName = getRouteName();
                if (routeName.isEmpty()){
                    Toast.makeText(AddRouteActivity.this, R.string.enterValidName, Toast.LENGTH_SHORT).show();
                }
                else{
                    float CityDistanceKm = getCityDistInKm();
                    float HighDistanceKm = getHighDistInKm();
                    Route routeToBeAdded = new Route(CityDistanceKm, HighDistanceKm, routeName);
                    ctModel.addRoute(routeToBeAdded);
                    ctModel.addRouteToDB(AddRouteActivity.this, routeToBeAdded);

                    finish();
                }
            }
        });

        Button cancelRouteAddBtn = (Button) findViewById(R.id.ARCancelBtn);
        cancelRouteAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Retrieves the route name from the EditText input, makes sure that the user has entered at least
     * a character for the name(even if it's blank)
     */
    private String getRouteName(){
        EditText name = (EditText) findViewById(R.id.ARRouteNameField);
        if(name.getText().toString().isEmpty()){
            return "";
        }
        else{
            return name.getText().toString();
        }
    }

    /**
     * Retrieves City distance of the route from EditText input and returns it
     * @return double distance of the city route traveled
     */
    private float getCityDistInKm(){
        EditText cityDistance = (EditText) findViewById(R.id.ARCityField);
        if(cityDistance.getText().toString().isEmpty()){
            return 0.0f;
        }
        else{
            return Float.parseFloat(cityDistance.getText().toString());
        }
    }

    /**
     * Retrieves Highway distance of the route from EditText input and returns it
     * @return double distance of the highway route traveled
     */
    private float getHighDistInKm(){
        EditText highDistance = (EditText) findViewById(R.id.ARHwyField);

        if (highDistance.getText().toString().isEmpty()) {
            return 0.0f;
        }
        else {
            return Float.parseFloat(highDistance.getText().toString());
        }
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, AddRouteActivity.class);
    }
}
