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
                tv2.clearComposingText();
                tv2.setText("r2: count:" + count);
                if(isOrderedBroadcast() && count > 10)
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
        if(ticker == null || !ticker.isAlive()) {
            ticker = new Ticker(this);
            ticker.startTicker();
        }
    }

    public void stopTicker(View view) {
        if(ticker != null && !ticker.isInterrupted()) {
            ticker.stopTicker();
        }
    }

    public void finishActivity(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(r1);
        unregisterReceiver(r2);
        unregisterReceiver(r3);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(I)Log.i(TAG,"onSaveInstanceState");
        outState.putSerializable("ticker", ticker); // à commenter pour q3
        //outState.putParcelable("ticker",ticker); // demandé en q3
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState){
        super.onRestoreInstanceState(outState);
        if(I)Log.i(TAG,"onRestoreInstanceState");
        ticker = (Ticker)outState.getSerializable("ticker"); // à commenter pour q3
        //ticker = (Ticker)outState.getParcelable("ticker"); // demandé en q3
    }

}