package richardshen.carbon_tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that holds utilities and provides utility methods to access and change utilities in the list
 */
public class UtilitiesCollection implements Iterable<Utilities>{
    private List<Utilities> utilities = new ArrayList<>();

    @Override
    public Iterator<Utilities> iterator() {
        return utilities.iterator();
    }

    public void addUtility(Utilities utility){utilities.add(utility);}

    public void deleteUtility(int index){utilities.remove(index);}

    public Utilities getUtility(int index){return utilities.get(index);}

    /**
     * Replaces the utility bill at specified index with a newly edited one
     */
    public void editUtility(Utilities utility, int indexOfEdit){
        utilities.remove(indexOfEdit);
        utilities.add(indexOfEdit, utility);
    }

    /**
     * Gets the description of each utility bill
     * @return String: description of the utilities, includes bill cost, consumption and date range
     */
    public ArrayList<String> getAllUtilityDesc(){
        ArrayList<String> descriptions = new ArrayList<>(10);
        for(Utilities utility: this){
            descriptions.add(utility.getDescription());
        }
        return descriptions;
    }

    public int getSize(){
        return utilities.size();
    }

}
