package richardshen.carbon_tracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewJourneys extends AppCompatActivity {
    private int REQUEST_CODE_GETMESSAGE = 1011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journeys);

        setupaddJourneyBtn();
        setupManageJourneyBtn();
    }

    private void setupaddJourneyBtn() {
        Button btn1 = (Button) findViewById(R.id.addJourneyBtn);
        btn1.setTypeface(MainActivity.face);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = ChooseTransportActivity.makeIntent(ViewJourneys.this);

                startActivityForResult(intent1,REQUEST_CODE_GETMESSAGE);
            }
        });
    }

    private void setupManageJourneyBtn() {
        Button btn1 = (Button) findViewById(R.id.VJManageJrnBtn);
        btn1.setTypeface(MainActivity.face);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = EditJourneys.makeIntent(ViewJourneys.this);
                startActivityForResult(intent1,REQUEST_CODE_GETMESSAGE);
            }
        });
    }
}
