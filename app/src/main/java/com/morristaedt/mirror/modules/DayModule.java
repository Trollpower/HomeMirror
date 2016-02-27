package com.morristaedt.mirror.modules;

import android.text.Html;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by HannahMitt on 8/23/15.
 */
public class DayModule {

    public static Spanned getDay() {
        SimpleDateFormat formatDayOfMonth = new SimpleDateFormat("EEEE", Locale.GERMANY);
        Calendar now = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return Html.fromHtml(formatDayOfMonth.format(now.getTime()) + ", der " + df.format(now.getTime()));
    }
}
