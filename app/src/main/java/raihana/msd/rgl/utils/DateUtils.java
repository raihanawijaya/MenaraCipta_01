package raihana.msd.rgl.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final String SDF_FORMAT1 = "dd/MM/yyyy";
    public static final String SDF_FORMAT2 = "dd-MMM-yyyy";

    private Calendar mCalendar;

    public DateUtils() {
        mCalendar = Calendar.getInstance();
    }

    public int getCurrentDays(){
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getCurrentMonth(){
        return mCalendar.get(Calendar.MONTH);
    }

    public int getCurrentYear(){
        return mCalendar.get(Calendar.YEAR);
    }

    public Date getCurrentDate(){
        return mCalendar.getTime();
    }

    public String reFormatDate(String date, String styleInput, String styleOutput) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(styleInput);
        Date firstDate = sdf.parse(date);
        return new SimpleDateFormat(styleOutput).format(firstDate);
    }

    public String formatCurrentDate(String style){
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        return sdf.format(getCurrentDate());
    }
}
