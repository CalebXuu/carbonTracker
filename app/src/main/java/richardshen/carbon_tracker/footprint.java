package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class footprint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footprint);
    }
    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context,footprint.class);

        return intent;
    }
}
