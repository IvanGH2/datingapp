package ngd.utility;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DateTimeUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeUtil.class);
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	//private static final String DATE_FORMAT = "yyyy-mm-dd";
	private static final String DATE_ISO_8601 = "yyyy-MM-dd";

	private static final ThreadLocal<SimpleDateFormat> SDF_DD_MM_YYYY_SLASH = ThreadLocal
			.withInitial(() -> new SimpleDateFormat(DATE_FORMAT));

	private static final ThreadLocal<SimpleDateFormat> SDF_DATE_ISO_8601 = ThreadLocal
			.withInitial(() -> new SimpleDateFormat(DATE_ISO_8601));

	private DateTimeUtil() {
	}

	public static Date getDateStart(String date, String dateFormat) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		try {
			calendar.setTime(sdf.parse(date));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			return calendar.getTime();
		} catch (ParseException e) {
			LOGGER.info("", e);
			return null;
		}
	}

	public static Date getDateEnd(String date, String dateFormat) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		try {
			calendar.setTime(sdf.parse(date));
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return calendar.getTime();
		} catch (ParseException e) {
			LOGGER.info("", e);
			return null;
		}
	}

	public static String formatDate(Date date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
		return sdf.format(date);
	}

	public static Date parseDate(String date, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(date);
	}

	public static Date parseDate(String date) throws ParseException {
		try {
			return SDF_DD_MM_YYYY_SLASH.get().parse(date);
		} catch (ParseException e) {
			return SDF_DATE_ISO_8601.get().parse(date);
		}
	}

	private static String getDateFormat() {
		return DATE_FORMAT;
	}
}