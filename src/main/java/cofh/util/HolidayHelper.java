package cofh.util;

import java.util.Calendar;

/**
 * The class contains helper functions related to Holidays!
 * 
 * @author King Lemming
 * 
 */
public class HolidayHelper {

	private HolidayHelper() {

	}

	static Calendar curTime = Calendar.getInstance();

	static Calendar holidayStart = Calendar.getInstance();
	static Calendar holidayEnd = Calendar.getInstance();

	public static boolean isHalloween() {

		setDate(holidayStart, Calendar.OCTOBER, 30);
		setDate(holidayEnd, Calendar.NOVEMBER, 2);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isChristmas() {

		setDate(holidayStart, Calendar.DECEMBER, 24);
		setDate(holidayEnd, Calendar.DECEMBER, 27);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	static void setDate(Calendar cal, int month, int date) {

		cal.clear();

		cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

}
