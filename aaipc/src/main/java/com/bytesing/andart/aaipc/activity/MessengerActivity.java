package com.bytesing.andart.aaipc.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.bytesing.andart.aaipc.MessengerConstant;
import com.bytesing.andart.aaipc.service.MessengerService;
import com.bytesing.andart.aaipc.service.MessengerService.MessengerHandler;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/6.
 */
public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = "MessengerActivity";
    private Messenger mService;

    private Messenger mRePly = new Messenger(new MessengerHandler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MessengerConstant.MSG_FROM_SERVICE:
                    Log.d(TAG, "received msg from service: "+msg.getData().getString("reply"));
                    return true;
            }
            return false;
        }
    }));

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: "+name);
            mService = new Messenger(service);
            sendMsgToService("hello service,we had connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();
    }

    public void bindService(){
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService: start");
    }

    public void sendMsgToService(String msgStr){
        if (mService != null && mService.getBinder().isBinderAlive()) {
            Message msg = Message.obtain(null, MessengerConstant.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg",msgStr);
            msg.setData(data);
            msg.replyTo = mRePly;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
