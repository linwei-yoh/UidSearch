package com.example.android.uidsearch;


public class Utility {

    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append(str).append(" ");
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

}
