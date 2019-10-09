package com.badboy.gpstracker.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bad Boy on 1/31/2017.
 */

public class Utils {

    public static String getFormattedDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if(!date.isEmpty()) {
            Date startDate = (Date) formatter.parse(date);
            return sdf.format(startDate);
        }else{
            return "";
        }
    }

    public static String decimalTruncate(float data) {
        String formattedString = String.format("%.02f", data);
        return formattedString;
    }
}
