package richardshen.carbon_tracker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TipsHistoryActivity extends AppCompatActivity {
    private CarbonTrackerModel ctModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_history);
        ctModel = CarbonTrackerModel.getInstance();
        setupTipList();
        setupBackBtn();
    }
    public void setupBackBtn(){
        Button backBtn=(Button)findViewById(R.id.backMenu_btn);

        backBtn.setTypeface(MainActivity.face);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=menu.makeIntent(TipsHistoryActivity.this);
                startActivity(intent);
            }
        });
    }
    public void setupTipList() {
        ArrayList<String> tipList= ctModel.getTips();
        int size=ctModel.getTipListSize();
        String[] TipList1=new String[size];
        for(int i=0;i<size;i++)
        {
            TipList1[i]=tipList.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.tiplist_layout, TipList1);
        ListView tipList1 = (ListView) findViewById(R.id.TipList);
        tipList1.setAdapter(adapter);
    }
    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context,TipsHistoryActivity.class);

        return intent;
    }
}
