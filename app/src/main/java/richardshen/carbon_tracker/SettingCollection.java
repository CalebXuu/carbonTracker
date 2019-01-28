package richardshen.carbon_tracker;


import android.content.SharedPreferences;

public class SettingCollection {
    private int carbonUnits;
    public SettingCollection(){
        carbonUnits=0;

    }

    public int getCarbonUnits(){
        return carbonUnits;
    }

    public void resetCarbonUnits(int newCarbonUnits){
        carbonUnits=newCarbonUnits;

    }


}
