package br.com.aaascp.gerenciadordepedidos.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andre on 27/09/17.
 */

public final class DateFormatterUtils {

    private static final String FORMAT_DATE_HOUR = "dd/MM/yyyyy 'às' HH:mm";

    private final SimpleDateFormat formatter;

    private DateFormatterUtils(String format) {
        this.formatter = new SimpleDateFormat(format);
    }

    public static DateFormatterUtils getDateHourInstance() {
        return new DateFormatterUtils(FORMAT_DATE_HOUR);
    }

    public String format(Date date) {
        return formatter.format(date);
    }

    public String now() {
        return format(new Date());
    }

}
