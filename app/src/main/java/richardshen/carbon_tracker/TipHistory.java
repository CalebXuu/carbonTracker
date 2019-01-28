package richardshen.carbon_tracker;

import java.util.ArrayList;


public class TipHistory {
    private ArrayList<String> tips; // the internal array
    private int[] tipCaseIndex;
    private int index;
   //constructor;
    public TipHistory(){
        tips = new ArrayList<>();
        tipCaseIndex =new int[7];
        for(int i=0;i<7;i++)
        {
            tipCaseIndex[i]=0;
        }
        index=0;
    }

    // return the size of array
    public int getSize(){
        return tips.size();
    }

    // is the array empty
    public boolean isEmpty(){
        return getSize() == 0;
    }

   // add an element
    public void addTip(String tip,int caseIndex){
        tips.add(tip);
        if(index>5)
        {

            tipCaseIndex[index]=caseIndex;
            index=0;
        }
        else {
            tipCaseIndex[index] = caseIndex;
            index++;
        }

    }
    public boolean checkNewTip(int caseIndex)
    {
        for(int i=0;i<7;i++)
        {
            if(tipCaseIndex[i]==caseIndex){
                return false;
            }
        }
        return true;
    }


    // remove an element
    public void removeTip(int position){
        tips.remove(position);

    }
    // edit the element in the position
    public void editTip(int position,String newTip,int newCaseIndex){
        tips.remove(position);
        tips.add(position,newTip);
    }

    public ArrayList<String> getTips(){
        return tips;
    }

    public int[] getTipCaseIndexs(){
        return tipCaseIndex;
    }

    public void setTipCaseIndexs(int[] tipCaseIndex){
        this.tipCaseIndex = tipCaseIndex;
    }

    public void setTips(ArrayList<String> tips){
        this.tips = tips;
    }

}
