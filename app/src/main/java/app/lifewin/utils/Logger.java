package app.lifewin.utils;

import android.util.Log;

/**
 * This is used for maintain the log values.
 *
 */
public class Logger {

	private static String TAG = "";
	public static boolean logstatus = true;

	private Logger() {
	}

	public static void setLogStatus(boolean isShow){
		logstatus=isShow;
	}
	public static void setTag(String tagValue){
		TAG=tagValue;
	}

	public static void w(final String TAG,final String s) {
		if (logstatus) {
			Log.w(TAG, ":->" + s);
		}
	}
	public static void w(final String s) {
		if (logstatus) {
			Log.w(TAG, ":->" + s);
		}
	}
	public static void warn(final String s) {
		if (logstatus) {
			Log.w(TAG, ":->" + s);
		}
	}


	public static void i(final String TAG,final String s) {
		if (logstatus) {
			Log.i(TAG, ":->" + s);
		}
	}
	public static void i(final String s) {
		if (logstatus) {
			Log.i(TAG, ":->" + s);
		}
	}

	public static void info(final String s) {
		if (logstatus) {
			Log.i(TAG, ":->" + s);
		}
	}

	public static void info(final String s, final Throwable throwable) {
		if (logstatus) {
			Log.i(TAG, s, throwable);
		}
	}

	public static void e(final String TAG,final String s) {
		if (logstatus) {
			Log.e(TAG, ":->" + s);
		}
	}

	public static void e(final String s) {
		if (logstatus) {
			Log.e(TAG, ":->" + s);
		}
	}

	public static void error(final String s) {
		if (logstatus) {
			Log.e(TAG, ":->" + s);
		}
	}

	public static void error(final String tag, final String s) {
		if (logstatus) {
			Log.e(tag, ":->" + s);
		}
	}


	public static void error(final Throwable throwable) {
		if (logstatus) {
			Log.e(TAG, null, throwable);
		}
	}

	public static void error(final String s, final Throwable throwable) {
		if (logstatus) {
			Log.e(TAG, s, throwable);
		}
	}


	public static void d(final String TAG,final String s) {
		if (logstatus) {
			Log.w(TAG, ":->" + s);
		}
	}
	public static void d(final String s) {
		if (logstatus) {
			Log.w(TAG, ":->" + s);
		}
	}
	public static void debug(final String s) {
		if (logstatus) {
			Log.w(TAG, ":->" + s);
		}
	}
}
