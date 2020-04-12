package com.bytesing.andart.aaipc.aidl.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telecom.ConnectionService;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.bytesing.andart.aaipc.R;
import com.bytesing.andart.aaipc.aidl.Book;
import com.bytesing.andart.aaipc.aidl.IBookManager;
import com.bytesing.andart.aaipc.aidl.IOnNewBookArrivedListener;
import com.bytesing.andart.aaipc.aidl.IOnNewBookArrivedListener.Stub;
import com.bytesing.andart.aaipc.aidl.service.BookManagerService;

import java.util.List;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/8.
 */
public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = "BookManagerActivity";
    public static final int MSG_NEW_BOOK_ARRIVED = 0x0;

    public static void start(Context context) {
        Intent starter = new Intent(context, BookManagerActivity.class);
        Bundle bundle = new Bundle();
        starter.putExtras(bundle);
        context.startActivity(starter);
    }

    private IBookManager mIBookManager;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: thread =  "+Thread.currentThread().getName());
            IBookManager iBookManager = IBookManager.Stub.asInterface(service);
            mIBookManager = iBookManager;
            if (iBookManager != null) {
                try {
                    iBookManager.register(mOnNewBookArrivedListener);
                    List<Book>books = iBookManager.getBookList();
                    Log.i(TAG, "got books,list type is "+books.getClass().getCanonicalName()+"\n"+
                            " book is "+books.toString());
                    iBookManager.addBook(new Book(3,"Windows"));
                    iBookManager.addBook(new Book(3,"iOS"));
                    books = iBookManager.getBookList();
                    Log.i(TAG, "got new books,list type is "+books.getClass().getCanonicalName()+"\n"+
                            " now new books are "+books.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: thread =  "+Thread.currentThread().getName());
        }
    };

    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            Log.e(TAG, "onNewBookArrived: activity isFinishing = "+isFinishing()+", thread = "+Thread.currentThread().getName());
            if(!isFinishing()) {
                mHandler.obtainMessage(MSG_NEW_BOOK_ARRIVED, book).sendToTarget();
            }

        }
    };
    private Handler mHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_NEW_BOOK_ARRIVED:
                    Object book = msg.obj;
                    if (mBookMsgTv != null && book!=null) {
                        mMsgBuilder.setLength(0);
                        mMsgBuilder.append(mBookMsgTv.getText())
                                .append("\n")
                                .append(book.toString());
                        mBookMsgTv.setText(mMsgBuilder);
                    }
                    return true;

            }
            return false;
        }
    });

    private TextView mBookMsgTv;
    private StringBuilder mMsgBuilder = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_manager_activity_layout);
        mBookMsgTv = (TextView) findViewById(R.id.book_msg_tv);
        mBookMsgTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mIBookManager != null && mIBookManager.asBinder().isBinderAlive()) {
            try {
                Log.i(TAG, "onDestroy: unregister "+mOnNewBookArrivedListener);
                mIBookManager.unregister(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mHandler.removeCallbacksAndMessages(null);
        unbindService(mConnection);
        super.onDestroy();
    }
}
