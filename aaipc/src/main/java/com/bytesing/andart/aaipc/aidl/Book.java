package com.bytesing.andart.aaipc.aidl;

import android.os.Parcel;
import android.os.Parcelable.Creator;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/6.
 */
public class Book implements android.os.Parcelable {
    public int mBookId;
    public String mBookName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mBookId);
        dest.writeString(this.mBookName);
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.mBookId = in.readInt();
        this.mBookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
