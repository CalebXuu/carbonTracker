package richardshen.carbon_tracker;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    public static final ArrayList<CarCollection> allBrands = LoadingScreen.allBrands;

    public static Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CarbonTrackerModel.getInstance().loadCar(this);
        CarbonTrackerModel.getInstance().loadRoute(this);
        CarbonTrackerModel.getInstance().loadJourney(this);
        CarbonTrackerModel.getInstance().loadUtilities(this);
        CarbonTrackerModel.getInstance().loadTips(this);

        face = Typeface.createFromAsset(getAssets(), "carbonbl.ttf");
        setupStartBtn();
        setupNotifications();
    }

    private void setupNotifications() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        if(cal.get(Calendar.HOUR_OF_DAY)>=21){
            cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 21);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
    }



    private void setupStartBtn() {
        Button btn1 = (Button) findViewById(R.id.startBtn);
        btn1.setTypeface(face);

        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = menu.makeIntent(MainActivity.this);
                setupNotifications();
                startActivity(intent1);
            }
        });

    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context,MainActivity.class);

        return intent;
    }

}