// IBookManager.aidl
package com.bytesing.andart.aaipc.aidl;
import com.bytesing.andart.aaipc.aidl.Book;
import com.bytesing.andart.aaipc.aidl.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void register(in IOnNewBookArrivedListener listener);
    void unregister(in IOnNewBookArrivedListener listener);
}
