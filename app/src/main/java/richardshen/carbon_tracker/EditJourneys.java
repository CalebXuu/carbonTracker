package richardshen.carbon_tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EditJourneys extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journeys);

        setupJourneysList();
    }

    private void setupJourneysList() {
        ListView list = (ListView)findViewById(R.id.EJJourneys);

        updateJourneyList();

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CarbonTrackerModel.getInstance().getJourneys().removeJourneyAtIndex(position);
                CarbonTrackerModel.getInstance().updateSavedJourney(EditJourneys.this);
                updateJourneyList();

                return true;
            }
        });
    }

    private void updateJourneyList() {
        ListView list = (ListView)findViewById(R.id.EJJourneys);

        JourneyCollection allJourneys = CarbonTrackerModel.getInstance().getJourneys();
        final ArrayList<String> listSource = new ArrayList<>();

        for(Journey j : allJourneys) {
            listSource.add(j.getJourneyDesc());
        }

        ArrayAdapter<String> adapter = new JourneyListAdapter(this, R.layout.route_listview, listSource);

        /*final ArrayAdapter<String> adapter = new ArrayAdapter<>(EditJourneys.this, android.R.layout.simple_list_item_1,
                listSource);*/

        list.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, EditJourneys.class);
    }

    private class JourneyListAdapter extends ArrayAdapter<String> {

        public JourneyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
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

            Journey currentJourney = CarbonTrackerModel.getInstance().getJourneys().getJourney(position);

            switch (currentJourney.getIconId()) {
                case 0:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j0));
                    break;
                case 1:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j1));
                    break;
                case 2:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j2));
                    break;
                case 3:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j3));
                    break;
                case 4:
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.j4));
                    break;
            }

            TextView label = (TextView)itemView.findViewById(R.id.RVRtNameField);

            label.setText(CarbonTrackerModel.getInstance().getJourneys().getJourney(position).getJourneyDesc());

            return itemView;
        }
    }

}
