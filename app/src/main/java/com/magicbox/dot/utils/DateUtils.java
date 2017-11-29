package com.magicbox.dot.utils;

import android.text.Editable;

import com.magicbox.dot.model.DiaSemana;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Criado por eduardo em 11/09/17.
 */

public class DateUtils {

    public static String dataParaHoraString(Date data){
        if(data == null)
            return "";
        return android.text.format.DateFormat.format("HH:mm", data).toString();
    }

    public static String dataParaString(Date data){
        if(data == null)
            return "";
        return android.text.format.DateFormat.format("yyyy-MM-dd", data).toString();
    }

    public static Date horaStringParaData(String horario) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        try {
            return dateFormat.parse(horario);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date horaStringParaData(Editable text) {
        return horaStringParaData(String.valueOf(text));
    }

    public static Date stringParaData(String data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return dateFormat.parse(data);
        } catch (ParseException e) {
            return null;
        }
    }

    public static DiaSemana dataParaDiaSemana(Date data){

        Calendar c = Calendar.getInstance();
        c.setTime(data);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        return DiaSemana.valueOf(dayOfWeek);
    }

    public static long diferencaEmMinutos(Date inicio, Date termino) {
        final int MILLI_TO_MINUTE = 1000 * 60;
        return (horaCalendar(inicio).getTimeInMillis() - horaCalendar(termino).getTimeInMillis()) / MILLI_TO_MINUTE;
    }

    public static Calendar horaCalendar(Date data){
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return cal;
    }
}
