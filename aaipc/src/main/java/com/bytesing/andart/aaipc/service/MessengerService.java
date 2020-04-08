package com.bytesing.andart.aaipc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bytesing.andart.aaipc.MessengerConstant;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/6.
 */
public class MessengerService extends Service {
    private static final String TAG = "MessengerService";

    public static class MessengerHandler extends Handler{
        public MessengerHandler() {
        }

        public MessengerHandler(Callback callback) {
            super(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerConstant.MSG_FROM_CLIENT:
                    String msgStr = msg.getData().getString("msg");
                    Log.i(TAG, "receive msg from client:"+msgStr);
                    Messenger replyTo = msg.replyTo;
                    if (replyTo != null) {
                        Message replyMsg = Message.obtain(null,MessengerConstant.MSG_FROM_SERVICE);
                        Bundle data = new Bundle();
                        data.putString("reply","yes,your msg has handle latter! msg content is"+msgStr);
                        replyMsg.setData(data);
                        try {
                            replyTo.send(replyMsg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger  = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
