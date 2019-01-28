package richardshen.carbon_tracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private final int ENTER_MORE_JOURNEY = 100;
    private final int ENTER_NEW_BILL = 200;
    private final int TODAY_FOORPRINT_BELOW_AVG = 300;
    private final int ENTER_NEW_JOURNEY = 400;
    private final int GENERAL_NOTIF = 500;
    private int numEnteredJourneysForToday = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        int mId = getNotificationId();
        NotificationCompat.Builder mBuilder;
        Intent resultIntent;
        TaskStackBuilder stackBuilder;
        PendingIntent resultPendingIntent;
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        switch(mId){
            case ENTER_MORE_JOURNEY:
                mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.wheel_icon)
                                .setContentTitle(context.getString(R.string.enter_more_journey))
                                .setContentText(context.getString(R.string.youve_entered_x_journeys_today_wanna_more, numEnteredJourneysForToday)).setAutoCancel(true);
                resultIntent = new Intent(context, ChooseTransportActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(ChooseTransportActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                mNotificationManager.notify(mId, mBuilder.build());
                break;
            case ENTER_NEW_BILL:
                mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.wheel_icon)
                                .setContentTitle(context.getString(R.string.enter_new_bill))
                                .setContentText(context.getString(R.string.you_havent_entered_any_bill_in_last_45_days)).setAutoCancel(true);
                resultIntent = AddUtilitiesActivity.makeIntent(context);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(AddUtilitiesActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                mNotificationManager.notify(mId, mBuilder.build());
                break;
            case TODAY_FOORPRINT_BELOW_AVG:
                mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.wheel_icon)
                                .setContentTitle(context.getString(R.string.good_evening))
                                .setContentText(context.getString(R.string.good_your_footprint_is_below_avg)).setAutoCancel(true);
                resultIntent = ChooseTransportActivity.makeIntent(context);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(ChooseTransportActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                mNotificationManager.notify(mId, mBuilder.build());
                break;
            case ENTER_NEW_JOURNEY:
                mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.wheel_icon)
                                .setContentTitle(context.getString(R.string.enter_a_journey))
                                .setContentText(context.getString(R.string.you_havent_entered_any_journey_today)).setAutoCancel(true);
                resultIntent = new Intent(context, ChooseTransportActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(ChooseTransportActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                mNotificationManager.notify(mId, mBuilder.build());
                break;
            case GENERAL_NOTIF:
                mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.wheel_icon)
                                .setContentTitle(context.getString(R.string.good_evening))
                                .setContentText(context.getString(R.string.would_you_like_to_review)).setAutoCancel(true);
                resultIntent = ChooseTransportActivity.makeIntent(context);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(ChooseTransportActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                mNotificationManager.notify(mId, mBuilder.build());
                break;

        }


    }

    private int getNotificationId() {
        Date today = getCurrentDate();
        numEnteredJourneysForToday = countJourneys(today);
        boolean enteredJourneyToday = (numEnteredJourneysForToday!=0);
        boolean enteredUtilityLast45Days = checkIfEnteredUtilityLastXDays(45);
        boolean footprintTodayBelowAvgLast45Days = checkIfFootprintBelowAvg(45, today);
        if(!enteredJourneyToday){
            return ENTER_NEW_JOURNEY;
        }
        else if(!enteredUtilityLast45Days){
            return ENTER_NEW_BILL;
        }
        else if(footprintTodayBelowAvgLast45Days){
            return TODAY_FOORPRINT_BELOW_AVG;
        }
        else if(numEnteredJourneysForToday>=2){
            return ENTER_MORE_JOURNEY;
        }
        else{
            return GENERAL_NOTIF;
        }
    }

    private boolean checkIfFootprintBelowAvg(int days, Date date) {
        float avgEmissions = getAvgEmissionsInLastXDays(days);
        return getTotalJourneyEmissionsAtDate(date)+getUtilitiesEmissionAtDate(date)<avgEmissions;
    }

    private float getAvgEmissionsInLastXDays(int days) {
        return (getTotalUtilitiesEmissionsInLastXDate(days)+getTotalJourneyEmissionsInLastXDate(days))/days;
    }

    private float getTotalUtilitiesEmissionsInLastXDate(int days){
        float utilitiesEmission = 0;
        float totalUtilitiesEmission=0;
        Date date;
        for (int i = 0; i < days; i ++){
            date = getLastXDate(i);
            utilitiesEmission = getUtilitiesEmissionAtDate(date);
            totalUtilitiesEmission=utilitiesEmission+ totalUtilitiesEmission;
        }
        return totalUtilitiesEmission;
    }

    private float getTotalJourneyEmissionsInLastXDate(int days){
        float journeyEmission = 0;
        float totalJourneyEmission=0;
        Date date;
        for (int i = 0; i < days; i ++){
            date = getLastXDate(i);
            journeyEmission = getTotalJourneyEmissionsAtDate(date);
            totalJourneyEmission=journeyEmission+totalJourneyEmission;
        }
        return totalJourneyEmission;
    }

    private Date getCurrentDate(){
        long time=System.currentTimeMillis();
        final Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        return mCalendar.getTime();
    }

    private float getUtilitiesEmissionAtDate(Date date){
        CarbonTrackerModel ctModel = CarbonTrackerModel.getInstance();
        float utilitiesEmissionsAtDate = 0;
        for(Utilities utility : ctModel.getUtilitiesCollection()){
            if (date.after(utility.getStartDate()) && date.before(utility.getEndDate())){
                utilitiesEmissionsAtDate += utility.getDailyEmissions();
            }
        }
        return utilitiesEmissionsAtDate;
    }

    private float getTotalJourneyEmissionsAtDate(Date date){
        CarbonTrackerModel ctModel = CarbonTrackerModel.getInstance();
        float journeyEmissionsAtDate = 0;
        for (Journey journey : ctModel.getJourneys()){
            if (dateFormat.format(date).equals(dateFormat.format(journey.getDate()))){
                journeyEmissionsAtDate += journey.calculateCarbonFootPrint();
            }
        }
        return journeyEmissionsAtDate;
    }

    private boolean checkIfEnteredUtilityLastXDays(int days) {
        Date date;
        for (int i = 0; i < days; i ++){
            date = getLastXDate(i);
            if(checkIfDateHasUtility(date)){
                return true;
            }
        }
        return false;
    }

    private Date getLastXDate(int daysBack) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysBack);
        return cal.getTime();
    }

    private boolean checkIfDateHasUtility(Date date) {
        CarbonTrackerModel ctModel = CarbonTrackerModel.getInstance();
        for (Utilities utility : ctModel.getAllUtilities()){
            if (dateFormat.format(date).equals(dateFormat.format(utility.getStartDate()))){
                return true;
            }
        }
        return false;
    }

    private int countJourneys(Date date) {
        CarbonTrackerModel ctModel = CarbonTrackerModel.getInstance();
        int count = 0;
        for(Journey journey: ctModel.getJourneys()){
            if(dateFormat.format(date).equals(dateFormat.format(journey.getDate()))){
                count++;
            }
        }
        return count;
    }
}
