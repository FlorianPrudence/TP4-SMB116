package cnam.smb116.tp4;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.io.Serializable;

public class Ticker extends Thread implements Serializable {
    // private class Ticker extends Thread implements Parcelable // ligne à décommenter en q3
    public static final String TIME_ACTION_TIC = "time_action_tic";
    public static final String COUNT_EXTRA = "count";
    private Context context;

    private static long count;
    public Ticker(Context context){
        this.context = context;
    }
    public void startTicker(){
        this.start();
    }
    public void stopTicker(){
        this.interrupt();
    }
    public void run(){
        Intent intent = new Intent();
        intent.setAction(TIME_ACTION_TIC);
        while(!isInterrupted()){
            SystemClock.sleep(1000L);
            count++;
            intent.putExtra(Ticker.COUNT_EXTRA, count);
            if(count <= 10)
                context.sendBroadcast(intent);
            else {
                context.sendOrderedBroadcast(intent, null);
            }
        }
    }
}
