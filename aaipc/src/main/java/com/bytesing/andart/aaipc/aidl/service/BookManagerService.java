package com.bytesing.andart.aaipc.aidl.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bytesing.andart.aaipc.aidl.Book;
import com.bytesing.andart.aaipc.aidl.IBookManager;
import com.bytesing.andart.aaipc.aidl.IBookManager.Stub;
import com.bytesing.andart.aaipc.aidl.IOnNewBookArrivedListener;
import com.bytesing.andart.aaipc.aidl.PermissionConstant;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/8.
 */
public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";
    private AtomicBoolean mIsDestroyed = new AtomicBoolean(false);
    private CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mArrivedListeners = new RemoteCallbackList<>();
    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean hasPermission = hasPermission(getCallingUid());
            return hasPermission && super.onTransact(code, data, reply, flags);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.e(TAG, "getBookList: thread="+Thread.currentThread().getName());
            return mBooks;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.e(TAG, "addBook: thread="+Thread.currentThread().getName());
            mBooks.add(book);

        }

        @Override
        public void register(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.e(TAG, "register: thread="+Thread.currentThread().getName());
            mArrivedListeners.register(listener);
            int size = mArrivedListeners.beginBroadcast();
            mArrivedListeners.finishBroadcast();
            Log.e(TAG, "register: size = "+size+",thread="+Thread.currentThread().getName());
        }

        @Override
        public void unregister(IOnNewBookArrivedListener listener) throws RemoteException {
            mArrivedListeners.unregister(listener);
            int size = mArrivedListeners.beginBroadcast();
            mArrivedListeners.finishBroadcast();
            Log.e(TAG, "unregister: size = "+size+",thread="+Thread.currentThread().getName());
        }
    };

    private void onNewBookArrived(Book newBook){
        mBooks.add(newBook);
        try {
            final int size = mArrivedListeners.beginBroadcast();
            Log.i(TAG, "onNewBookArrived: notify size = " + size);
            for (int i = 0; i < size; i++) {
                try {
                    IOnNewBookArrivedListener listener = mArrivedListeners.getBroadcastItem(i);
                    if (listener != null) {
                        Log.i(TAG, "onNewBookArrived: notify Listener = " + listener);
                        listener.onNewBookArrived(newBook);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mArrivedListeners.finishBroadcast();
        }catch (Throwable ignore){
            ignore.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBooks.add(new Book(1,"Android"));
        mBooks.add(new Book(2,"Linux"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mIsDestroyed.get()){
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int bookIndex = mBooks.size() + 1;
                    Book newBook = new Book(bookIndex,"new book No."+bookIndex);
                    onNewBookArrived(newBook);
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(!hasPermission(0)){
            return null;
        }
        return mBinder;
    }

    private boolean hasPermission(int uid){
        int check = checkCallingOrSelfPermission(PermissionConstant.ACCESS_BOOK_SERVICE);
        if(check == PackageManager.PERMISSION_DENIED){
            Log.i(TAG, "hasPermission: PERMISSION_DENIED");
            return false;
        }
        if(uid <=0){
            return false;
        }
        String pkn = "";
        String[] packages = getPackageManager().getPackagesForUid(uid);
        if (packages != null && packages.length > 0) {
            pkn = packages[0];
        }
        Log.i(TAG, "hasPermission: pkn="+pkn);
        return pkn.startsWith(PermissionConstant.CALLING_PKN_PRE);
    }
}
