// IOnNewBookArrivedListener.aidl
package com.bytesing.andart.aaipc.aidl;
import com.bytesing.andart.aaipc.aidl.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}
