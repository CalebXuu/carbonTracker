package richardshen.carbon_tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity{

    public static final int NEW_ROUTE_RQ_CODE = 1001;

    private RouteCollection routes;
    private Route currentRoute;
    private int currentRoutePos;
    private static int indexOfCar;

    private CarbonTrackerModel ctModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        //Gets a Singleton Instance
        ctModel = CarbonTrackerModel.getInstance();
        //Gets the routes held in the Singleton
        routes = ctModel.getRoutes();

        getCar();

        populateRouteList();
        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.route_activity_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.bar_btn_add_route:
                Intent addRoute = AddRouteActivity.makeIntent(RouteActivity.this);
                startActivityForResult(addRoute, NEW_ROUTE_RQ_CODE);
                break;

            case R.id.bar_btn_delete_route:
                if (currentRoute != null){
                    AlertDialog.Builder deleteAlert = new AlertDialog.Builder(RouteActivity.this);
                    deleteAlert.setMessage(getString(R.string.are_you_sure_you_want_to_delete, currentRoute.getRouteName())).setPositiveButton(android.R.string.yes, deleteRouteDialog).setNegativeButton(android.R.string.no, deleteRouteDialog).show();
                }
                else{
                    Toast.makeText(RouteActivity.this, R.string.pls_select_a_route_to_delete, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bar_btn_back:
                finish();
                break;
        }
        return true;
    }


    //Sets up the button for the dialog box when the user wants to delete a route
    final DialogInterface.OnClickListener deleteRouteDialog = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    routes.deleteRoute(currentRoutePos);
                    CarbonTrackerModel.getInstance().updateSavedRoutes(RouteActivity.this);
                    populateRouteList();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private void getCar() {
        Intent intent = getIntent();
        indexOfCar = intent.getIntExtra("CAR_INDEX", 0);
    }

    /**
     * Populates the list of available routes to choose from
     */
    private void populateRouteList() {

        List<String> routes = CarbonTrackerModel.getInstance().getRoutes().getAllRouteDesc();

        ArrayAdapter<String> routeAdapter = new RouteListAdapter(this, R.layout.route_listview, routes);
                //new ArrayAdapter<>(this, R.layout.route_listview, R.id.RVRtNameField, routes);

        ListView routeList = (ListView) findViewById(R.id.list_routes);
        routeList.setAdapter(routeAdapter);
    }

    /**
     * Sets up the buttons for the activity
     */
    private void setupButtons() {
        //OK button - passes back the selected route to the journey activity
        Button confirmButton = (Button) findViewById(R.id.btn_Route_Confirm);

        confirmButton.setTypeface(MainActivity.face);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRoute != null){
                   Intent intent1= JourneyFootPrintActivity.makeIntent(RouteActivity.this, indexOfCar, currentRoute);
                    startActivity(intent1);
                }
                else{
                    Toast.makeText(RouteActivity.this, R.string.pls_select_a_route, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Sets the current route
        ListView routeList = (ListView) findViewById(R.id.list_routes);
        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentRoute = routes.getRoute(position);
                currentRoutePos = position;
            }
        });

        //Sets up the edit button
        Button editButton = (Button)findViewById(R.id.btn_EditRoute);

        editButton.setTypeface(MainActivity.face);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentRoute != null) {
                    routes.deleteRoute(currentRoutePos);
                    Intent addRoute = AddRouteActivity.makeIntent(RouteActivity.this);
                    startActivityForResult(addRoute, NEW_ROUTE_RQ_CODE);
                } else{
                    Toast.makeText(RouteActivity.this, R.string.pls_select_a_route_to_edit, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        populateRouteList();
    }

    //Creates an instance of this activity
    public static Intent makeIntent(Context context, int carIndex){
        Intent intent = new Intent(context, RouteActivity.class);
        intent.putExtra("CAR_INDEX", carIndex);
        return intent;
    }

    private class RouteListAdapter extends ArrayAdapter<String> {

        public RouteListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                itemView = inflater.inflate(R.layout.route_listview, parent, false);
            }

            ImageView icon = (ImageView)itemView.findViewById(R.id.RVRtImg);

            icon.setImageDrawable(getResources().getDrawable(R.drawable.route));

            TextView label = (TextView)itemView.findViewById(R.id.RVRtNameField);

            label.setText(CarbonTrackerModel.getInstance().getRoutes().getAllRoutes().get(position).getDescription());

            return itemView;
        }
    }
}
