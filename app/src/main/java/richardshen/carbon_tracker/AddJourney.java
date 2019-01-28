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

import java.util.ArrayList;
import java.util.List;

public class AddJourney extends AppCompatActivity {
    private final int REQUEST_CODE_GETMESSAGE3 = 1012;
    private final int CAR_SELECTED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);
        updateList();
        registerClickCallback();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.car_activity_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.bar_btn_add_car:
                Intent intent1 = new Intent(AddJourney.this,AddCarScreen.class);
                startActivityForResult(intent1, REQUEST_CODE_GETMESSAGE3);
                break;
        }
        return true;
    }
    private void registerClickCallback() {
        final ListView carList = (ListView) findViewById(R.id.AJCarList);
        carList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                Intent intent = RouteActivity.makeIntent(AddJourney.this, Integer.valueOf(position));

                startActivity(intent);
            }
        });

        carList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddJourney.this);
                builder.setTitle(R.string.options);

                final ArrayList<Car> userCars = CarbonTrackerModel.getInstance().getUserCars();
                builder.setMessage(getString(R.string.selOps) + userCars.get(position));

                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userCars.remove(position);
                        CarbonTrackerModel.getInstance().updateSavedCars(AddJourney.this);
                        updateList();
                    }
                });
                builder.setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = PersonalizeCar.makeIntent(AddJourney.this);
                        Car selectedCar = userCars.get(position);
                        int engineID = selectedCar.getEngineId();
                        int selectedBrandPos = 0;

                        int x = 0;
                        for (x = 0; x < MainActivity.allBrands.size(); x++) {
                            if (selectedCar.getBrand().equals(MainActivity.allBrands.get(x).getBrandName())) {
                                selectedBrandPos = x;
                                System.out.println(MainActivity.allBrands);
                                break;
                            }
                        }

                        CarbonTrackerModel.getInstance().getUserCars().remove(position);
                        CarbonTrackerModel.getInstance().updateSavedCars(AddJourney.this);

                        intent.putExtra("BRAND", selectedBrandPos);
                        intent.putExtra("CAR ID", engineID);

                        startActivityForResult(intent, CAR_SELECTED);
                    }
                });

                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, AddJourney.class);

        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {

            case REQUEST_CODE_GETMESSAGE3:
            case CAR_SELECTED:
                CarCollection brand = MainActivity.allBrands.get(data.getIntExtra("BRAND", -1));

                Car newCar = new Car(brand.getCarByID(data.getIntExtra("CAR ID", -1)));

                newCar.setName(data.getStringExtra("NAME"));
                newCar.setIconId(data.getIntExtra("ICON", 0));

                CarbonTrackerModel.getInstance().addCar(newCar);
                CarbonTrackerModel.getInstance().addNewCarToDB(this, newCar);

                updateList();
        }
    }

    private void updateList() {
        ListView list = (ListView) findViewById(R.id.AJCarList);
        ArrayList<Car> listSource = CarbonTrackerModel.getInstance().getUserCars();

        ArrayAdapter<Car> adapter = new CarListAdapter(AddJourney.this, R.layout.car_list_layout, listSource);

        list.setAdapter(adapter);
    }


    private class CarListAdapter extends ArrayAdapter<Car> {

        public CarListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Car> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                itemView = inflater.inflate(R.layout.car_list_layout, parent, false);
            }

            Car currentCar = CarbonTrackerModel.getInstance().getUserCars().get(position);

            ImageView icon = (ImageView) itemView.findViewById(R.id.LVCarIcon);


            //TODO: ADD CAR ICON HERE.

            switch (currentCar.getIconId()) {
                case 0:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c0));
                    break;
                case 1:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c1));
                    break;
                case 2:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c2));
                    break;
                case 3:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c3));
                    break;
                case 4:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c4));
                    break;
            }

            TextView carName = (TextView) itemView.findViewById(R.id.LVCarNameField);
            TextView carSpec = (TextView) itemView.findViewById(R.id.LVCarSpecField);

            carName.setText(currentCar.getName());
            carSpec.setText(currentCar.getFormattedStr());

            return itemView;
        }
    }
}