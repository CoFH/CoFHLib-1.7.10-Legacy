package cofh.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
		/*
		 * Compute the day of the year that Easter falls on. Step names E1 E2 etc.,
		 * are direct references to Knuth, Vol 1, p 155.
		 *
		 * http://en.wikipedia.org/wiki/Computus#Meeus.2FJones.2FButcher_Gregorian_algorithm
		 */

		Calendar easterSun;

		int year = Calendar.getInstance().get(Calendar.YEAR);
		if(year <= 1582) {
			return false; // The calculation is based on Gregorian calendar and it's incorrect before 1582
		}

		int golden, century, x, z, d, epact, n;

		golden = (year % 19) + 1; /*  metonic cycle */

		century = (year / 100) + 1; /* Centuries are shifted by one e.g. 1984 was in 20th C */

		x = (3 * century / 4) - 12; /* leap year correction */
		z = ((8 * century + 5) / 25) - 5; /* syncing with moon's orbit */

		d = (5 * year / 4) - x - 10;

		epact = (11 * golden + 20 + z - x) % 30; /* epact */

		if ((epact == 25 && golden > 11) || epact == 24) {
			epact++;
		}

		n = 44 - epact;
		n += 30 * (n < 21 ? 1 : 0);
		n += 7 - ((d + n) % 7);

		if (n > 31) {
			easterSun = new GregorianCalendar(year, 4 - 1, n - 31); /* if April */
		} else {
			easterSun = new GregorianCalendar(year, 3 - 1, n); /* if March */
		}

		setDate(holidayStart, easterSun.get(Calendar.MONTH), easterSun.get(Calendar.DAY_OF_MONTH));
		setDate(holidayEnd, easterSun.get(Calendar.MONTH), easterSun.get(Calendar.DAY_OF_MONTH) + 1);

		curTime = Calendar.getInstance();

		return curTime.after(holidayStart) && curTime.before(holidayEnd);
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
