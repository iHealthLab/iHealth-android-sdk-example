package com.ihealth.demo;

import android.util.Log;

import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.utils.ByteBufferUtil;
import com.ihealth.communication.utils.MD5;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
////        assertEquals(4, 2 + 2);
//        String str="";
//
//        String dataID1 = MD5.md5String(ByteBufferUtil.getBPDataID("8CDE52C9D96A",
//                -100 + "", 1539850599));
//        String dataID2 = MD5.md5String(ByteBufferUtil.getBPDataID("8CDE52C9D96A",
//                (118 + 77 + 59) + "", 1539850599));
//                System.out.print(dataID1+"\n");
//                System.out.print(dataID2);
//        boolean test=(0%4!=0);
        System.out.print(getOurPackageName("https://api.ihealthlabs.com"));


    }
    // 时间数据处理 精确到秒
    public static long String2TS(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return 0;
        }

        long ret = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date;
        try {
            date = sdf.parse(dateStr);
            ret = date.getTime();
            ret = ret / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String getOurPackageName(String tempPackageName) {
        byte[] secrets;
        try {
            tempPackageName = tempPackageName.substring(0, tempPackageName.length() - 1) + "@#$";
            secrets = MessageDigest.getInstance("MD5").digest(tempPackageName.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e1) {
            throw new RuntimeException("Huh, getPackageName should be supported?", e1);
        } catch (UnsupportedEncodingException e2) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e2);
        }
        StringBuilder stringBuilder = new StringBuilder(secrets.length * 2);
        byte[] temps = secrets;
        int i = temps.length;
        for (int j = 0; j < i; ++j) {
            int k = temps[j];
            if ((k & 0xFF) < 16)
                stringBuilder.append("0");
            stringBuilder.append(Integer.toHexString(k & 0xFF));
        }
        stringBuilder.append(Integer.toHexString(temps[1] & 0xFF));
        return stringBuilder.toString();
    }


}