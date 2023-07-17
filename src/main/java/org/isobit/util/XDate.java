package org.isobit.util;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.swing.table.TableModel;
import org.isobit.app.X;

public class XDate extends Date {

    public static Date getDateTime(Date d, Date t) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int y = c.get(Calendar.YEAR);
        int mi = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        c.setTime(t);
        c.set(Calendar.YEAR, y);
        c.set(Calendar.MONTH, mi);
        c.set(Calendar.DAY_OF_MONTH, dd);
        return c.getTime();
    }

    private static Calendar CALENDAR = Calendar.getInstance();
    static int ALL_WEEK = 1;
    static int MONDAY_TO_FRIDAY = 2;
    static int MONDAY_TO_SATURDAY = 3;
    static int laborDay = 1;
    private static long lastWait = 0;
    private static String defaultFormat = "dd/MM/yyyy";
    private static String timeFormat = "HH:mm:ss";
    private static String datetimeFormat = "dd/MM/yyyy HH:mm:ss";

    public static Date endOfDay(Date date) {
        Calendar c = CALENDAR;
        synchronized (c) {
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MILLISECOND, 999);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MINUTE, 59);
            return c.getTime();
        }
    }

    public static List getMonthList() {
        return getMonthList(Locale.getDefault());
    }

    public static List getWeekdays() {
        Locale l = Locale.getDefault();
        List d = new ArrayList();
        for (Object v : new DateFormatSymbols(l).getWeekdays()) {
            String s = v.toString().toUpperCase();
            if (s.length() > 0) {
                d.add(new Object[]{d.size(), s});
            }
        }
        return d;
    }

    public static List getMonthList(Locale l) {
        List d = new ArrayList();
        for (Object v : Arrays.asList(new DateFormatSymbols(l).getMonths()).subList(0, 12)) {
            d.add(new Object[]{d.size(), v.toString().toUpperCase()});
        }
        return d;
    }

    /**
     * Returns a new Date with the hours, milliseconds, seconds and minutes set
     * to 0.
     *
     * @param date Date used in calculating start of day
     * @return Start of <code>date</code>
     */
    public static Date startOfDay(Date date) {
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            return calendar.getTime();
        }
    }

    /**
     * Returns day in millis with the hours, milliseconds, seconds and minutes
     * set to 0.
     *
     * @param date long used in calculating start of day
     * @return Start of <code>date</code>
     */
    public static long startOfDayInMillis(long date) {
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * Returns the last millisecond of the specified date.
     *
     * @param date long to calculate end of day from
     * @return Last millisecond of <code>date</code>
     */
    public static long endOfDayInMillis(long date) {
        Calendar c = CALENDAR;
        synchronized (c) {
            c.setTimeInMillis(date);
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MILLISECOND, 999);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MINUTE, 59);
            return c.getTimeInMillis();
        }
    }

    /**
     * Returns the day after <code>date</code>.
     *
     * @param date Date used in calculating next day
     * @return Day after <code>date</code>.
     */
    public static Date nextDay(Date date) {
        return new Date(addDays(date.getTime(), 1));
    }

    /**
     * Adds <code>amount</code> days to <code>time</code> and returns the
     * resulting time.
     *
     * @param time Base time
     * @param amount Amount of increment.
     *
     * @return the <var>time</var> + <var>amount</var> days
     */
    public static long addDays(long time, int amount) {
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.DAY_OF_MONTH, amount);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * Returns the day after <code>date</code>.
     *
     * @param date Date used in calculating next day
     * @return Day after <code>date</code>.
     */
    public static long nextDay(long date) {
        return addDays(date, 1);
    }

    /**
     * Returns the week after <code>date</code>.
     *
     * @param date Date used in calculating next week
     * @return week after <code>date</code>.
     */
    public static long nextWeek(long date) {
        return addDays(date, 7);
    }

    /**
     * Returns the number of days difference between <code>t1</code> and
     * <code>t2</code>.
     *
     * @param t1 Time 1
     * @param t2 Time 2
     * @param checkOverflow indicates whether to check for overflow
     * @return Number of days between <code>start</code> and <code>end</code>
     */
    public static int getDaysDiff(long t1, long t2, boolean checkOverflow) {
//        if (t1 > t2) {
//            long tmp = t1;
//            t1 = t2;
//            t2 = tmp;
//        }
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(t1);
            int delta = 0;
            while (calendar.getTimeInMillis() < t2) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                delta++;
            }
            if (checkOverflow && (calendar.getTimeInMillis() > t2)) {
                delta--;
            }
            return delta;
        }
    }

    /**
     * Returns the number of days difference between <code>t1</code> and
     * <code>t2</code>.
     *
     * @param t1 Time 1
     * @param t2 Time 2
     * @return Number of days between <code>start</code> and <code>end</code>
     */
    public static int getDaysDiff(long t1, long t2) {
        return getDaysDiff(t1, t2, true);
    }

    public static boolean isValidDate(String inDate) {
        if (inDate == null) {
            return false;
        }
        System.out.print("validar " + inDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        System.out.print("parcear " + inDate);
        try {
            dateFormat.parse(inDate.trim());
        } catch (Exception pe) {
            return false;
        }
        return true;
    }

    /**
     * Check, whether the date passed in is the first day of the year.
     *
     * @param date date to check in millis
     * @return <code>true</code> if <var>date</var> corresponds to the first day
     * of a year
     * @see Date#getTime()
     */
    public static boolean isFirstOfYear(long date) {
        boolean ret = false;
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            int currentYear = calendar.get(Calendar.YEAR);
            // Check yesterday
            calendar.add(Calendar.DATE, -1);
            int yesterdayYear = calendar.get(Calendar.YEAR);
            ret = (currentYear != yesterdayYear);
        }
        return ret;
    }

    /**
     * Check, whether the date passed in is the first day of the month.
     *
     * @param date date to check in millis
     * @return <code>true</code> if <var>date</var> corresponds to the first day
     * of a month
     * @see Date#getTime()
     */
    public static boolean isFirstOfMonth(long date) {
        boolean ret = false;
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            int currentMonth = calendar.get(Calendar.MONTH);
            // Check yesterday
            calendar.add(Calendar.DATE, -1);
            int yesterdayMonth = calendar.get(Calendar.MONTH);
            ret = (currentMonth != yesterdayMonth);
        }
        return ret;
    }

    /**
     * Returns the day before <code>date</code>.
     *
     * @param date Date used in calculating previous day
     * @return Day before <code>date</code>.
     */
    public static long previousDay(long date) {
        return addDays(date, -1);
    }

    /**
     * Returns the week before <code>date</code>.
     *
     * @param date Date used in calculating previous week
     * @return week before <code>date</code>.
     */
    public static long previousWeek(long date) {
        return addDays(date, -7);
    }

    /**
     * Returns the first day before <code>date</code> that has the day of week
     * matching <code>startOfWeek</code>. For example, if you want to find the
     * previous monday relative to <code>date</code> you would call
     * <code>getPreviousDay(date, Calendar.MONDAY)</code>.
     *
     * @param date Base date
     * @param startOfWeek Calendar constant correspoding to start of week.
     * @return start of week, return value will have 0 hours, 0 minutes, 0
     * seconds and 0 ms.
     *
     */
    public static long getPreviousDay(long date, int startOfWeek) {
        return getDay(date, startOfWeek, -1);
    }

    /**
     * Returns the first day after <code>date</code> that has the day of week
     * matching <code>startOfWeek</code>. For example, if you want to find the
     * next monday relative to <code>date</code> you would call
     * <code>getPreviousDay(date, Calendar.MONDAY)</code>.
     *
     * @param date Base date
     * @param startOfWeek Calendar constant correspoding to start of week.
     * @return start of week, return value will have 0 hours, 0 minutes, 0
     * seconds and 0 ms.
     *
     */
    public static long getNextDay(long date, int startOfWeek) {
        return getDay(date, startOfWeek, 1);
    }

    private static long getDay(long date, int startOfWeek, int increment) {
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            // Normalize the view starting date to a week starting day
            while (day != startOfWeek) {
                calendar.add(Calendar.DATE, increment);
                day = calendar.get(Calendar.DAY_OF_WEEK);
            }
            return startOfDayInMillis(calendar.getTimeInMillis());
        }
    }

    /**
     * Returns the previous month.
     *
     * @param date Base date
     * @return previous month
     */
    public static long getPreviousMonth(long date) {
        return incrementMonth(date, -1);
    }

    /**
     * Returns the next month.
     *
     * @param date Base date
     * @return next month
     */
    public static long getNextMonth(long date) {
        return incrementMonth(date, 1);
    }

    private static long incrementMonth(long date, int increment) {
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            calendar.add(Calendar.MONTH, increment);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * Returns the date corresponding to the start of the month.
     *
     * @param date Base date
     * @return Start of month.
     */
    public static long getStartOfMonth(long date) {
        return getMonth(date, -1);
    }

    /**
     * Returns the date corresponding to the end of the month.
     *
     * @param date Base date
     * @return End of month.
     */
    public static long getEndOfMonth(long date) {
        return getMonth(date, 1);
    }

    private static long getMonth(long date, int increment) {
        long result;
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            if (increment == -1) {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                result = startOfDayInMillis(calendar.getTimeInMillis());
            } else {
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.add(Calendar.MILLISECOND, -1);
                result = calendar.getTimeInMillis();
            }
        }
        return result;
    }

    /**
     * Returns the day of the week.
     *
     * @param date date
     * @return day of week.
     */
    public static int getDayOfWeek(long date) {
        Calendar calendar = CALENDAR;
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            return (calendar.get(Calendar.DAY_OF_WEEK));
        }
    }

    public static String getDateFormat() {
        return XDate.defaultFormat;
    }

    public static String getTimeFormat() {
        return XDate.timeFormat;
    }

    public static String getDateTimeFormat() {
        return XDate.datetimeFormat;
    }

    public static Date getTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    public static int getDaysOfMonth(int anoEje, int mes) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, anoEje);
        c.set(Calendar.MONTH, mes);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private static SimpleDateFormat ds = new SimpleDateFormat("dd/MM/yyyy");

    public static Date[] getPeriod(String s) {
        Object d[] = (" " + s + " ").split("[|]");
        Date[] period = null;
        if (d.length > 1) {
            period = new Date[2];
            try {
                period[0] = new Date(Long.parseLong(d[0].toString().trim()));
                
            } catch (NumberFormatException e) {
                try {
                    period[0] = ds.parse(d[0].toString().trim());
                } catch (ParseException e2) {
                }
            }
            try {
                period[1] = new Date(Long.parseLong(d[1].toString().trim()));
                
            } catch (NumberFormatException e) {
                try {
                    period[1] = ds.parse(d[1].toString().trim());
                } catch (ParseException e2) {
                }
            }
            if (period[0] != null || period[1] != null) {
                return period;
            }
        }

        return period;
    }

    public XDate() {
        super();
    }

    public XDate(Date d) {
        super(d != null ? d.getTime() : System.currentTimeMillis());
    }

    /**
     * Returns the time waited in milliseconds from the previous call to this
     * method. A
     *
     * @return the number of milliseconds from previous call to this method
     */
    public static long getLastWait() {
        long n = Calendar.getInstance().getTimeInMillis();
        long nv = n - lastWait;
        lastWait = n;
        return nv;
    }

    public static Object getTime(TableModel model, int coIni, int coFin, Date xIni, Date xFin) {
        return getTime(model, coIni, coFin, 0, model.getRowCount(), xIni, xFin);
    }

    private static Calendar cIni = new GregorianCalendar();
    private static Calendar cFin = new GregorianCalendar();

    public static void main(String[] args) {
        /*System.out.println(X.gson.toJson(getDateDifferenceInDDMMYYYY(
                XDate.getDate(2015, 3, 1),
                XDate.getDate(2015, 11, 31)
        )));*/
    }

    public static int[] getDateDifferenceInDDMMYYYY(Date dIni, Date dFin) {
        cIni.setTime(dIni);
        cFin.setTime(dFin);
        int nYears = cFin.get(Calendar.YEAR) - cIni.get(Calendar.YEAR);
        int nMeses = cFin.get(Calendar.MONTH) - cIni.get(Calendar.MONTH) - 1;

        if (nMeses < 0) {
            nYears--;
            nMeses = 12 + nMeses;
        }

        int mdi = cIni.getActualMaximum(Calendar.DAY_OF_MONTH);
        int mdf = cFin.getActualMaximum(Calendar.DAY_OF_MONTH);

        int ndi = mdi - cIni.get(Calendar.DAY_OF_MONTH) + 1;
        int ndf = cFin.get(Calendar.DAY_OF_MONTH);
        int nd = ndi + ndf;
        int tDias;
//        System.out.println("mdi=" + XDate.toString(dIni) + ";mdf=" + XDate.toString(dFin));
//        System.out.println("mdi=" + mdi + ";mdf=" + mdf);
//        System.out.println("ndi=" + ndi + ";ndf=" + ndf + ";nMeses=" + nMeses);
        if (nd >= mdi) {
            nd = nd - mdi;//nd % mdi; 
            nMeses += 1;
//            System.out.println("nd=" + nd + ";nMeses=" + nMeses);
//            System.out.println("nd=" + nd);
            if (nd >= mdf) {
                nMeses += (int) Math.floor(nd / mdf);
                nd = nd % mdf;
            }
//            System.out.println("nd=" + nd + ";nMeses=" + nMeses);

        } else if (mdf - ndf < mdi - ndi) {
            if (nd >= mdf) {
                nMeses += (int) Math.floor(nd / mdf);
            }
        } else if (nd >= mdi) {
            nMeses += (int) Math.floor(nd / mdi);
        }
        nYears += (int) Math.floor(nMeses / 12.0);
        nMeses = nMeses % 12;
        return new int[]{nYears, nMeses, nd};
    }

    public static Object getTime(TableModel model, int coIni, int coFin, int rini, int rfin, Date xIni, Date xFin) {
        //Las fechas deben estar ordenadas
        Calendar cIni = new GregorianCalendar();
        Calendar cFin = new GregorianCalendar();
        int tYears = 0;
        int tMeses = 0;
        int tDias = 0;

        ArrayList<Date[]> fechas = new ArrayList();
//        System.out.println("limitado de " + getSQLDate(xIni) + " a " + getSQLDate(xFin));

        for (int i = rini; i < rfin; i++) {
            Date ini = (Date) model.getValueAt(i, coIni);
            Date fin = (Date) XUtil.isEmpty(model.getValueAt(i, coFin), xFin);

            //para asegurarse q la fecha fin no es nula o no supera la fecha actual
            if (xFin != null) {
                if (XDate.compareDate(ini, xFin) > 0) {
                    continue;
                } else if (fin == null || fin.after(xFin)) {
                    fin = xFin;
                }
            } else if (fin == null) {
                continue;
            }

            if (xIni != null) {
                if (XDate.compareDate(fin, xIni) < 0) {
                    continue;
                } else if (ini.before(xIni)) {
                    ini = xIni;
                }
            }

            int p = -1;
            int ii = 0;
            int jj = fechas.size();
            for (ii = 0; ii < jj; ii++) {
                if (!fechas.get(ii)[0].before(ini)) {
                    p = ii;
                    break;
                }
            }
            if (p == -1) {
                cIni.setTime(ini);
                cIni.add(Calendar.DAY_OF_MONTH, -1);
                if (fechas.size() > 0 && !fechas.get(ii - 1)[1].before(cIni.getTime())) {
                    if (fin.after(fechas.get(ii - 1)[1])) {
                        fechas.get(ii - 1)[1] = fin;
                    }
                } else {
                    fechas.add(new Date[]{ini, fin});
                }
            } else {
                fechas.add(p, new Date[]{ini, fin});
                cFin.setTime(fechas.get(p++)[1]);
                cFin.add(Calendar.DAY_OF_MONTH, 1);

                for (ii = p; ii < fechas.size(); ii++) {
                    if (!cFin.getTime().before(fechas.get(ii)[0])) {
                        if (fechas.get(ii)[1].after(fechas.get(p)[1])) {
                            fechas.get(p)[1] = fechas.get(ii)[1];
                        }
                        fechas.remove(ii--);
                    } else {
                        break;
                    }
                }
            }
        }

        for (Date[] d : fechas) {
            cIni.setTime(d[0]);
            cFin.setTime(d[1]);
            int nYears = cFin.get(Calendar.YEAR) - cIni.get(Calendar.YEAR);
            int nMeses = cFin.get(Calendar.MONTH) - cIni.get(Calendar.MONTH) - 1;
            if (nMeses < 0) {
                nYears--;
                nMeses = 12 + nMeses;
            }
            int mdi = cIni.getActualMaximum(Calendar.DAY_OF_MONTH);
            int mdf = cFin.getActualMaximum(Calendar.DAY_OF_MONTH);
            int ndi = mdi - cIni.get(Calendar.DAY_OF_MONTH) + 1;
            int ndf = cFin.get(Calendar.DAY_OF_MONTH);
            int nd = ndi + ndf + tDias;
//            System.out.println("nd=" + nd + ";mdi=" + mdi + ";ndi=" + ndi);
            if (nd >= mdi) {
                nMeses += (int) Math.floor(nd / mdi);
                tDias = nd % mdi;
//                tDias += ndf;
                nd = tDias;
//                System.out.println("nMeses=" + nMeses + ";nDias=" + nd + ";tDias=" + tDias + ";mdi=" + mdi);
                if (nd >= mdf) {
                    nMeses += (int) Math.floor(nd / mdf);
                    tDias = nd % mdf;
                    nd = tDias;
                }
            } else //                nd += ndf;
            if (mdf - ndf < mdi - ndi) {
                if (nd >= mdf) {
                    nMeses += (int) Math.floor(nd / mdf);
                    tDias = nd % mdf;
                } else {
                    tDias = nd % mdf;
                }
            } else if (nd >= mdi) {
                nMeses += (int) Math.floor(nd / mdi);
                tDias = nd % mdi;
            } else {
                tDias = nd % mdi;
            }
            nYears += (int) Math.floor(nMeses / 12.0);
            nMeses = nMeses % 12;
            tYears += nYears;
            tMeses += nMeses;
            tYears += (int) Math.floor(tMeses / 12.0);
            tMeses = tMeses % 12;
//            System.out.println(getSQLDate(d[0]) + " a " + d[1]);
//            System.out.println();
//            System.out.println("mdi=" + mdi + ";mdf=" + mdf + ";nYears=" + nYears + ";nMeses=" + nMeses + ";nDias=" + nd);
//            System.out.println("tYears=" + tYears + " tMeses=" + tMeses + " tDias=" + tDias);
        }
        return new Integer[]{tYears, tMeses, tDias};
    }

    public static void setDefaultFormat(String defaultFormat) {
        sdf = new SimpleDateFormat(XDate.defaultFormat = defaultFormat);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static String format(Date d) {
        return d != null ? sdf.format(d) : null;
    }

    public static String toString(Object date) {
        return date != null ? sdf.format(date) : null;
    }

    public static XDate parseDateDB(String sfecha) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = formatoFecha.parse(sfecha);
            return new XDate(fecha);
        } catch (Exception e) {
        }
        return null;
    }

    public static java.sql.Date getSQLDate(Date d) {
        return d != null ? new java.sql.Date(d.getTime()) : null;
    }

    public static Date getDate(int year, int month, int day) {
        CALENDAR.set(year, month, day);
        return CALENDAR.getTime();
    }

    public static Date getDate(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int getMonth(Date date) {
        CALENDAR.setTime(date);
        return CALENDAR.get(Calendar.MONTH);
    }

    public static int getDay(Date date) {
        CALENDAR.setTime(date);
        return CALENDAR.get(Calendar.DAY_OF_MONTH);
    }

    public static int compareDate(java.util.Date dateA, java.util.Date dateB) {

        if (dateA.getYear() > dateB.getYear()) {
            return 1;
        } else if (dateA.getYear() < dateB.getYear()) {
            return -1;
        } else if (dateA.getMonth() > dateB.getMonth()) {
            return 1;
        } else if (dateA.getMonth() < dateB.getMonth()) {
            return -1;
        } else if (dateA.getDate() > dateB.getDate()) {
            return 1;
        } else if (dateA.getDate() < dateB.getDate()) {
            return -1;
        } else {
            return 0;
        }
    }

    static public void setLaborDay(int laborDay) {
        XDate.laborDay = laborDay;
    }

    public XDate(String s) {
        super(s);
    }

    public static java.util.Date parseDate(String sDate) {
        return parse(sDate, defaultFormat);
    }

    public static java.util.Date parse(String sDate, String entryFormat) {
        try {
            return new SimpleDateFormat(entryFormat).parse(sDate);
        } catch (Exception e) {
            return null;
        }
    }

    public int getDia() {
        return super.getDate();
    }

    public Date setDay(int dia) {
        super.setDate(dia);
        return this;
    }

    public int getMes() {
        return Integer.parseInt((new SimpleDateFormat("MM")).format(this));
    }

    public void setMes(int mes) {
        this.setMonth(mes - 1);
    }

    public static int getYear(Date date) {
        CALENDAR.setTime(date);
        return CALENDAR.get(Calendar.YEAR);
    }

    public int getRelativeDate(XDate date) {
        int d = 0;
        d = getDia() - date.getDia();
        return d;
    }

    public int getRelativeDate2(XDate date) {
        int c = 0, p = 1, dia = 0;
        XDate date1;
        XDate date2;
        if (this.after(date)) {
            p = -1;
            date2 = (XDate) this.clone();
            date1 = date;
        } else {
            date2 = date;
            date1 = (XDate) this.clone();
        }
        while (date1.compareTo((Date) date2) != 0) {
            dia = date1.getDay();
            c++;
            if ((XDate.laborDay == XDate.MONDAY_TO_FRIDAY) && (dia == 0 || dia == 6)) {
                c--;
            } else if ((XDate.laborDay == XDate.MONDAY_TO_SATURDAY) && (dia == 0)) {
                c--;
            }
            /*else if(date1.isHollyDay(date1))
             c--;*/
            date1.setDate(date1.getDia() + 1);
        }
        return c * p;
    }

    public static String toString(Object date, String sFormat) {
        return date != null ? (new SimpleDateFormat(sFormat)).format(date) : null;
    }

}
