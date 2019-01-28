package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalizeCar extends AppCompatActivity {
    private final int CAR_SELECTED = 0;
    private final int CANCEL = -1;
    private int carBrandPos;
    private int carID;
    private String[] icons = {"Icon 1", "Icon 2", "Icon 3",
            "Icon 4", "Icon 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_car);


        Intent intent = getIntent();

        carBrandPos = intent.getIntExtra("BRAND", -1);
        carID = intent.getIntExtra("CAR ID", -1);

        CarCollection selectedBrand = MainActivity.allBrands.get(carBrandPos);
        Car selectedCar = selectedBrand.getCarByID(carID);

        TextView carInfo = (TextView)findViewById(R.id.PCCarInfo);

        carInfo.setText(selectedCar.getFormattedStr());

        setUpDoneBtn(carBrandPos, carID);
        setUpCancelBtn();

        setupIconSpinner();

    }

    private void setupIconSpinner() {
        Spinner iconSpinner = (Spinner)findViewById(R.id.PCSpinner);
        iconSpinner.setAdapter(new SpinnerAdapter(PersonalizeCar.this, R.layout.spinner_lay_out, icons));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.personalize_car_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.bar_btn_add_car_name:
                EditText nameField = (EditText)findViewById(R.id.PCCarNameField);

                String name = nameField.getText().toString();

                if(name.isEmpty()) {
                    Toast.makeText(PersonalizeCar.this, R.string.enterValidName, Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();

                intent.putExtra("BRAND", carBrandPos);
                intent.putExtra("CAR ID", carID);
                intent.putExtra("NAME", name);

                setResult(CAR_SELECTED, intent);
                finish();
                break;

            case R.id.bar_btn_cancel_add_car_name:
                finish();
                break;
        }
        return true;
    }

    private void setUpCancelBtn() {
        Button cancelBtn = (Button)findViewById(R.id.PCCancelBtn);

        cancelBtn.setTypeface(MainActivity.face);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(CANCEL);
                finish();
            }
        });
    }

    private void setUpDoneBtn(final int carBrandPos, final int carID) {
        Button btn = (Button)findViewById(R.id.PCDoneBtn);

        btn.setTypeface(MainActivity.face);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameField = (EditText)findViewById(R.id.PCCarNameField);

                String name = nameField.getText().toString();

                if(name.isEmpty()) {
                    Toast.makeText(PersonalizeCar.this, R.string.pls_enter_a_valid_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();

                Spinner iconSpinner = (Spinner)findViewById(R.id.PCSpinner);

                int iconId = iconSpinner.getSelectedItemPosition();

                intent.putExtra("BRAND", carBrandPos);
                intent.putExtra("CAR ID", carID);
                intent.putExtra("NAME", name);
                intent.putExtra("ICON", iconId);

                setResult(CAR_SELECTED, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(CANCEL);
        finish();
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context,PersonalizeCar.class);

        return intent;
    }

    private class SpinnerAdapter extends ArrayAdapter<String> {
        public SpinnerAdapter(Context context, int textViewResourceId,
                              String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_lay_out, parent, false);

            TextView label = (TextView) row.findViewById(R.id.SP_Text);
            label.setText(icons[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.SP_img);


            switch (icons[position]) {
                case "Icon 1":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c0));
                    break;
                case "Icon 2":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c1));
                    break;
                case "Icon 3":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c2));
                    break;
                case "Icon 4":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c3));
                    break;
                case "Icon 5":
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.c4));
                    break;
            }

            return row;
        }
    }
}
