package com.bytesing.andart.andutil.security;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringBufferInputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/6.
 */
public class MDUtil {

    public static String md5(byte[] data){
        String md5Str ="";
        if(data == null){
            return md5Str;
        }
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger bi = new BigInteger(1,md.digest(data));
            md5Str = String.format("%32x",bi);
            md.reset();
        }catch(Throwable e){
            e.printStackTrace();

        }
        return md5Str;
    }

    public static String md5(String target){
        String md5Str ="";
        try{
            md5Str =  md5(target.getBytes());
        }catch(Throwable e){
            e.printStackTrace();
        }finally{

        }
        return md5Str;

    }

    public static String md5(File file){
        String md5Str ="";
        if(file == null || !file.exists()){
            return md5Str;
        }
        RandomAccessFile raf = null;
        final int _len = 2048;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            raf = new RandomAccessFile(file,"r");
            byte[] buf = new byte[_len];
            int readLen = 0;
            while ((readLen= raf.read(buf,0,_len)) != -1){
                md.update(buf,0,readLen);
            }
            raf.close();
            BigInteger bi = new BigInteger(1,md.digest());
            md5Str = String.format("%32x",bi);
            md.reset();

        }catch(Throwable e){
            e.printStackTrace();
        }finally{
            if (raf != null) {
                try {
                    raf.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        }
        return md5Str;
    }
}
