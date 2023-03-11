package cnam.smb116.tp4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
//http://jfod.cnam.fr/SMB116/tp4_enonce/
public class MainActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();
    private final boolean I = true;
    private Ticker ticker;
    private static long count;
    private BroadcastReceiver r1,r2,r3;
    private TextView tv1, tv2, tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);

        if(count <= 10)
            tv1.setText("r1: count:" + count);
        else
            tv1.setText("r1: count:" + 10L);

        tv2.setText("r2: count:" + count);
        tv3.setText("r3: count:" + count);

        r1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                count = intent.getLongExtra(Ticker.COUNT_EXTRA, 0);
                tv1.clearComposingText();
                tv1.setText("r1: count:" + count);
            }
        };

        r2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                count = intent.getLongExtra(Ticker.COUNT_EXTRA, 0);
                boolean abort = intent.getBooleanExtra("abortBroadcast", false);
                tv2.clearComposingText();
                tv2.setText("r2: count:" + count);
                if(abort)
                    abortBroadcast();
            }
        };

        r3 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                count = intent.getLongExtra(Ticker.COUNT_EXTRA, 0);
                tv3.clearComposingText();
                tv3.setText("r3: count:" + count);
            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();
        if(I) Log.i(TAG,"onResume");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Ticker.TIME_ACTION_TIC);
        filter.setPriority(100);
        registerReceiver(r1, filter);
        filter.setPriority(200);
        registerReceiver(r2, filter);
        filter.setPriority(300);
        registerReceiver(r3, filter);
    }

    public void startTicker(View view) {
        ticker = new Ticker(this);
        ticker.startTicker();
    }

    public void stopTicker(View view) {
        ticker.stopTicker();
    }

    public void finishActivity(View view) {
        finish();
    }

    public void onSaveInstanceState(Bundle outState){
        if(I)Log.i(TAG,"onSaveInstanceState");
        outState.putSerializable("ticker", ticker); // à commenter pour q3
        // outState.putParcelable("ticker",ticker); // demandé en q3
    }

    public void onRestoreInstanceState(Bundle outState){
        if(I)Log.i(TAG,"onRestoreInstanceState");
        ticker = (Ticker)outState.getSerializable("ticker"); // à commenter pour q3
        // ticker = (Ticker)outState.getParcelable("ticker"); // demandé en q3
        // suivie d'une mise à jour de l'IHM
    }

    public class Ticker extends Thread implements Serializable {
        // private class Ticker extends Thread implements Parcelable // ligne à décommenter en q3
        public static final String TIME_ACTION_TIC = "time_action_tic";
        public static final String COUNT_EXTRA = "count";
        private Context context;


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
                //context.sendBroadcast(intent);
                if(count <= 10)                                  // à décommenter pour q2
                    context.sendBroadcast(intent);
                else {// à décommenter pour q2
                    intent.putExtra("abortBroadcast", true);
                    context.sendOrderedBroadcast(intent, null); // à décommenter pour q2
                }
            }
        }
    }
}