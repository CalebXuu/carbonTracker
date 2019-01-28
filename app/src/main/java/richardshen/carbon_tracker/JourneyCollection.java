package richardshen.carbon_tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Richard on 06/03/2017.
 */

public class JourneyCollection implements Iterable<Journey>{
    private static List<Journey> journeys = new ArrayList<>();

    public void addJourney(Journey journey){
        journeys.add(journey);
    }

    public Journey getJourney(int index){
        return journeys.get(index);
    }

    public void deleteJourney(int index){
        journeys.remove(index);
    }

    public float[] getJourneyCOEmissions(){
        float[] emissions = new float[journeys.size()];
        for (int i = 0; i < journeys.size(); i++){
            emissions[i] = journeys.get(i).calculateCarbonFootPrint();
        }
        return emissions;
    }

    public int getSize(){
        return journeys.size();
    }

    @Override
    public Iterator<Journey> iterator() {
        return journeys.iterator();
    }

    public  void removeJourneyAtIndex(int position) {
        journeys.remove(position);
        return;
    }
}
