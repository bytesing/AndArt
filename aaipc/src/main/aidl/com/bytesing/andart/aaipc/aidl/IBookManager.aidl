// IBookManager.aidl
package com.bytesing.andart.aaipc.aidl;
import com.bytesing.andart.aaipc.aidl.Book;

// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}
