package org.openmrs.module.dhisconnector;

/**
 * DHIS dataset period types include: Monthly | Daily | Weekly | Quarterly | SixMontly | Yearly
 * <br />
 * 
 * @see <a href=
 *      "https://docs.dhis2.org/2.22/en/developer/html/dhis2_developer_manual_full.html">DHIS
 *      2.22 Dev Manual</a> <br />
 *      DHIS period types and examples
 *      <table class="table" summary="Period format" border="1">
 *      <colgroup><col width="60pt" class="c1"><col width="80pt" class="c2"><col width="80pt"
 *      class="c3"><col width="200pt" class="c4"></colgroup><thead>
 *      <tr>
 *      <th>Interval</th>
 *      <th>Format</th>
 *      <th>Example</th>
 *      <th>Description</th>
 *      </tr>
 *      </thead><tbody>
 *      <tr>
 *      <td>Day</td>
 *      <td><span class="emphasis"><em>yyyyMMdd</em></span></td>
 *      <td>20040315</td>
 *      <td>March 15 2004</td>
 *      </tr>
 *      <tr>
 *      <td>Week</td>
 *      <td><span class="emphasis"><em>yyyy</em></span>W<span class="emphasis">
 *      <em>n</em></span></td>
 *      <td>2004W10</td>
 *      <td>Week 10 2004</td>
 *      </tr>
 *      <tr>
 *      <td>Month</td>
 *      <td><span class="emphasis"><em>yyyyMM</em></span></td>
 *      <td>200403</td>
 *      <td>March 2004</td>
 *      </tr>
 *      <tr>
 *      <td>Quarter</td>
 *      <td><span class="emphasis"><em>yyyy</em></span>Q<span class="emphasis">
 *      <em>n</em></span></td>
 *      <td>2004Q1</td>
 *      <td>January-March 2004</td>
 *      </tr>
 *      <tr>
 *      <td>Six-month</td>
 *      <td><span class="emphasis"><em>yyyy</em></span>S<span class="emphasis">
 *      <em>n</em></span></td>
 *      <td>2004S1</td>
 *      <td>January-June 2004</td>
 *      </tr>
 *      <tr>
 *      <td>Six-month April</td>
 *      <td><span class="emphasis"><em>yyyy</em></span>AprilSn</td>
 *      <td>2004AprilS1</td>
 *      <td>April-September 2004</td>
 *      </tr>
 *      <tr>
 *      <td>Year</td>
 *      <td>yyyy</td>
 *      <td>2004</td>
 *      <td>2004</td>
 *      </tr>
 *      <tr>
 *      <td>Financial Year April</td>
 *      <td>yyyyApril</td>
 *      <td>2004April</td>
 *      <td>Apr 2004-Mar 2005</td>
 *      </tr>
 *      <tr>
 *      <td>Financial Year July</td>
 *      <td>yyyyJuly</td>
 *      <td>2004July</td>
 *      <td>July 2004-June 2005</td>
 *      </tr>
 *      <tr>
 *      <td>Financial Year Oct</td>
 *      <td>yyyyOct</td>
 *      <td>2004Oct</td>
 *      <td>Oct 2004-Sep 2005</td>
 *      </tr>
 *      </tbody>
 *      </table>
 */
public enum DHISPeriodTypeUnit {
	DAILY("Daily", "\\b(\\d{4})(\\d{2})(\\d{2})\\b"),
    WEEKLY("Weekly", "\\b(\\d{4})W(\\d[\\d]?)\\b"),
    WEEKLY_SUNDAY("WeeklySunday", "\\b(\\d{4})SunW(\\d[\\d]?)\\b"),
    WEEKLY_WEDNESDAY("WeeklyWednesday", "\\b(\\d{4})WedW(\\d[\\d]?)\\b"),
    BI_WEEKLY("BiWeekly", "\\b(\\d{4})BiW(\\d[\\d]?)\\b"),
    MONTHLY("Monthly", "\\b(\\d{4})[-]?(\\d{2})\\b"),
    BI_MONTHLY("BiMonthly", "\\b(\\d{4})(\\d{2})B\\b"),
    QUARTERLY("Quarterly", "\\b(\\d{4})Q(\\d)\\b"),
    SIX_MONTHLY("SixMonthly", "\\b(\\d{4})S(\\d)\\b"),
    SIX_MONTHLY_APRIL("SixMonthlyApril", "\\b(\\d{4})AprilS(\\d)\\b"),
    YEARLY("Yearly", "\\b(\\d{4})\\b"),
    FINANCIAL_APRIL("FinancialApril", "\\b(\\d{4})April\\b"),
    FINANCIAL_JULY("FinancialJuly", "\\b(\\d{4})July\\b"),
    FINANCIAL_OCTOBER("FinancialOct", "\\b(\\d{4})Oct\\b");
	
	private final String type;
	
	private final String format;
	
	public String getType() {
		return type;
	}
	
	public String getFormat() {
		return format;
	}
	
	DHISPeriodTypeUnit(String type, String format) {
		this.type = type;
		this.format = format;
	}
	
	public static DHISPeriodTypeUnit find(String format) {
		for (DHISPeriodTypeUnit type : DHISPeriodTypeUnit.values()) {
			if (format.matches(type.format)) {
				return type;
			}
		}
		
		return null;
	}
	
	public static String getPeriodTypeFromIsoString(String isoPeriod) {
		DHISPeriodTypeUnit dateUnitType = find(isoPeriod);
		return dateUnitType != null ? dateUnitType.getType() : null;
	}
}
