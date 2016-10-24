package cofh.lib.util.helpers;

import java.lang.reflect.Field;

public class ReflectionHelper {

	public static Object getValue(Field field, Object instance) {

		try {
			return field.get(instance);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getInt(Field field, Object instance) {

		try {
			return field.getInt(instance);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setInt(Field field, Object instance, int value) {

		try {
			field.set(instance, value);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static double getDouble(Field field, Object instance) {

		try {
			return field.getDouble(instance);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean getBoolean(Field field, Object instance) {

		try {
			return field.getBoolean(instance);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setBoolean(Field field, Object instance, boolean value) {

		try {
			field.setBoolean(instance, value);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static float getFloat(Field field, Object instance) {

		try {
			return field.getFloat(instance);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFloat(Field field, Object instance, float value) {

		try {
			field.setFloat(instance, value);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
