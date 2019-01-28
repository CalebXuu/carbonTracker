package richardshen.carbon_tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class UtilitiesBillActivity extends AppCompatActivity {

    private CarbonTrackerModel ctModel;
    private int currentUtilPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilities_bill);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);

        ctModel = CarbonTrackerModel.getInstance();

        populateUtilitiesList();
        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.utilities_activity_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //starts the add new utility activity
            case R.id.bar_btn_confirm_add_new_utility:
                Intent intent = AddUtilitiesActivity.makeIntent(UtilitiesBillActivity.this);
                startActivity(intent);
                break;

            case R.id.bar_btn_cancel_add:
                finish();
                break;
        }
        return true;
    }

    /**
     * Populates the utilities list with previously created utilities
     */
    private void populateUtilitiesList() {
        ArrayAdapter<String> utilAdapter = new ArrayAdapter<String>(this, R.layout.utilities_list_layout, R.id.UBNameField, ctModel.getUtiliyDescriptions());
        ListView utilList = (ListView) findViewById(R.id.list_utilities);
        utilList.setAdapter(utilAdapter);
    }

    //Sets up the buttons for the activity
    private void setupButtons() {
        Button backBtn = (Button) findViewById(R.id.btn_Util_Cancel);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView utilList = (ListView) findViewById(R.id.list_utilities);
        utilList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = AddUtilitiesActivity.makeIntent(UtilitiesBillActivity.this);
                startActivity(intent);
            }
        });

        //Sets up the button for the dialog box when the user wants to delete a route
        final DialogInterface.OnClickListener deleteUtilityDialog = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ctModel.removeUtility(currentUtilPosition);
                        ctModel.updateSavedUtilities(UtilitiesBillActivity.this);
                        populateUtilitiesList();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        utilList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentUtilPosition = position;
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(UtilitiesBillActivity.this);
                deleteAlert.setMessage(R.string.are_you_sure_you_want_to_delete_selected_bill)
                        .setPositiveButton(android.R.string.yes, deleteUtilityDialog).setNegativeButton(android.R.string.no, deleteUtilityDialog).show();
                return true;
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, UtilitiesBillActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateUtilitiesList();
    }
}
