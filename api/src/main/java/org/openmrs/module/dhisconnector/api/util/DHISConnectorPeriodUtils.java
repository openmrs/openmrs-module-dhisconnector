package org.openmrs.module.dhisconnector.api.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class DHISConnectorPeriodUtils {

	public enum PeriodTypes {

		DAILY("Daily"),

		WEEKLY("Weekly"),

		MONTHLY("Monthly"),

		YEARLY("Yearly"),

		SIXMONTHLY("SixMonthly"),

		SIXMONTHLYAPRIL("SixMonthlyApril"),

		QUARTERLY("Quarterly");

		private final String name;

		private PeriodTypes(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public static boolean isMonthlyPeriod(String periodType) {
		return PeriodTypes.MONTHLY.getName().equals(periodType);
	}

	public static boolean isCurrentMonthOpen(String periodType, String periodValue) {

		if (isCurrentMonth(periodType, periodValue)) {

			int year = Integer.valueOf(StringUtils.substring(periodValue, 0, 4));
			int month = Integer.valueOf(StringUtils.substring(periodValue, 4));

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.DAY_OF_MONTH, 21);

			Date minOpenDate = cal.getTime();
			Date currentDate = Calendar.getInstance().getTime();

			if (currentDate.compareTo(minOpenDate) >= 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCurrentMonth(String periodType, String periodValue) {

		if (isMonthlyPeriod(periodType)) {
			int month = Integer.valueOf(StringUtils.substring(periodValue, 4));
			int currentMonth = DateTime.now().getMonthOfYear();

			return month == currentMonth ? true : false;
		}
		return false;
	}

	public static boolean isPreviousMonth(String periodType, String periodValue) {

		if (isMonthlyPeriod(periodType)) {
			int month = Integer.valueOf(StringUtils.substring(periodValue, 4));
			int currentMonth = DateTime.now().getMonthOfYear();

			return month + 1 == currentMonth ? true : false;
		}
		return false;
	}

	public static boolean isMonthOpenByExpiryDays(Integer expiryDays, String periodValue) {

		if (Objects.isNull(expiryDays)) {
			return false;
		}

		if (expiryDays == 0) {
			return true;
		}

		Date now = Calendar.getInstance().getTime();
		Date previousMonthPlusExpireDays = adjustExpireDaysInPreviousMonth(periodValue, expiryDays);
		if (now.compareTo(previousMonthPlusExpireDays) <= 0) {
			return true;
		}
		return false;
	}

	private static Date adjustExpireDaysInPreviousMonth(String periodValue, Integer expiryDays) {

		int year = Integer.valueOf(StringUtils.substring(periodValue, 0, 4));
		int month = Integer.valueOf(StringUtils.substring(periodValue, 4));

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

		return DateUtils.addDays(cal.getTime(), expiryDays);
	}

}
