package cofh.util;

import java.util.Calendar;

/**
 * The class contains helper functions related to Holidays!
 * 
 * Yes, they are US-centric. Feel free to suggest others!
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

	public static boolean isNewYear() {

		setDate(holidayStart, Calendar.DECEMBER, 31);
		setDate(holidayEnd, Calendar.JANUARY, 2);
		holidayEnd.set(Calendar.YEAR, Calendar.YEAR + 1);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isValentinesDay() {

		setDate(holidayStart, Calendar.FEBRUARY, 13);
		setDate(holidayEnd, Calendar.FEBRUARY, 15);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isStPatricksDay() {

		setDate(holidayStart, Calendar.MARCH, 16);
		setDate(holidayEnd, Calendar.MARCH, 18);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isAprilFools() {

		setDate(holidayStart, Calendar.MARCH, 31);
		setDate(holidayEnd, Calendar.APRIL, 2);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isEarthDay() {

		setDate(holidayStart, Calendar.APRIL, 21);
		setDate(holidayEnd, Calendar.APRIL, 23);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isEaster() {

		return false;
	}

	public static boolean isUSIndependenceDay() {

		setDate(holidayStart, Calendar.JULY, 3);
		setDate(holidayEnd, Calendar.JULY, 5);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isHalloween() {

		setDate(holidayStart, Calendar.OCTOBER, 30);
		setDate(holidayEnd, Calendar.NOVEMBER, 1);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
	}

	public static boolean isThanksgiving() {

		return false;
	}

	public static boolean isHanukkah() {

		return false;
	}

	public static boolean isChristmas() {

		setDate(holidayStart, Calendar.DECEMBER, 24);
		setDate(holidayEnd, Calendar.DECEMBER, 26);

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
