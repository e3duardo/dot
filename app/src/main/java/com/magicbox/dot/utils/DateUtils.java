package com.magicbox.dot.utils;

import java.util.Date;

/**
 * Criado por eduardo em 11/09/17.
 */

public class DateUtils {

    public static String dataParaHoraString(Date data){
        if(data == null)
            return "";
        return android.text.format.DateFormat.format("hh:mm", data).toString();
    }
}
