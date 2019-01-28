package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        setupViewUtilitiesBtn();
        setupViewJourneysBtn();
        setupViewHistoryBtn();
        setupAboutBtn();
        setupTipsHistoryBtn();
        setupSettingBtn();

    }

    private void setupSettingBtn() {
        Button btn = (Button)findViewById(R.id.settingBtn);

        btn.setTypeface(MainActivity.face);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, SettingActivity.class);

                startActivity(intent);
            }
        });

    }

    private void setupViewJourneysBtn() {
        Button btn = (Button)findViewById(R.id.viewJourneysBtn);
        btn.setTypeface(MainActivity.face);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, ViewJourneys.class);

                startActivity(intent);
            }
        });
    }


    private void setupAboutBtn() {
        Button btn = (Button)findViewById(R.id.MAboutBtn);
        btn.setTypeface(MainActivity.face);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, AboutActivity.class);

                startActivity(intent);
            }
        });
    }



    private void setupViewUtilitiesBtn() {
        Button btnViewUtil = (Button) findViewById(R.id.viewUtilitiesBtn);
        btnViewUtil.setTypeface(MainActivity.face);
        btnViewUtil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UtilitiesBillActivity.makeIntent(menu.this);
                startActivity(intent);
            }
        });
    }
    private void setupTipsHistoryBtn(){
        Button btnViewTipsHistory = (Button)findViewById(R.id.viewTipHistoryBtn);
        btnViewTipsHistory.setTypeface(MainActivity.face);
        btnViewTipsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = TipsHistoryActivity.makeIntent(menu.this);
                startActivity(intent);
            }
        });
    }

    private void setupViewHistoryBtn() {
        Button btn1 = (Button) findViewById(R.id.viewHistoryBtn);
        btn1.setTypeface(MainActivity.face);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = CarbonFootprintActivity.makeIntent(menu.this);
                startActivity(intent1);
            }
        });

    }
    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context,menu.class);

        return intent;
    }
}