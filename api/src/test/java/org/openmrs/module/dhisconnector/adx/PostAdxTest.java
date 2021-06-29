package org.openmrs.module.dhisconnector.adx;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openmrs.module.dhisconnector.DHISPeriodTypeUnit;

import junit.framework.Assert;

public class PostAdxTest {
	
	
	@Test
	public void testOutSideObjectCreator() throws DatatypeConfigurationException {
		AdxDataValue dv1 = new AdxDataValue("D1", new BigDecimal(32.0));
		AdxDataValue dv2 = new AdxDataValue("D2", new BigDecimal(120.0));
		AdxDataValueGroupPeriod period = new AdxDataValueGroupPeriod("20160614");
		XMLGregorianCalendar exported = DatatypeFactory.newInstance()
		        .newXMLGregorianCalendar(new GregorianCalendar(2016, 06, 14));
		AdxObjectFactory factory = new AdxObjectFactory();
		String str = null;
		
		AdxDataValueGroup g = new AdxDataValueGroup(dv1, period, "LOC1", "DS1");
		AdxDataValueGroup g2 = new AdxDataValueGroup(dv2, period, "LOC2", "DS2");
		AdxDataValueSet adx = new AdxDataValueSet(g, exported);
		
		str = factory.translateAdxDataValueSetIntoString(adx);
		QName q = new QName("GENDER");
		dv1.getOtherAttributes().put(q, "Male");
		System.out.println("otherAttrsize::::: " + dv1.getOtherAttributes().size());
		g2.getDataValues().add(dv1);
		adx.getGroups().add(g2);
		
		str = factory.translateAdxDataValueSetIntoString(adx);
		System.out.println("\nnSample ADX:::::::\n\n" + str + "\nXML:::::::\n");
		
		try {
			String text = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("adx-data-sample.xml"), "UTF-8");
			AdxDataValueSet tAdx = factory.translateStringToAdxDataValueSet(text);
			
			Assert.assertEquals(2, tAdx.getGroups().size());
			Assert.assertEquals(2, tAdx.getGroups().get(0).getOtherAttributes().size());
			Assert.assertNotNull(tAdx.getGroups().get(1).getDataValues().get(2).getAnnotation());
			Assert.assertEquals(0, tAdx.getGroups().get(1).getDataValues().get(2).getOtherAttributes().size());
			//Assert.assertEquals("Some qualifying text here on the datavalue", annotation);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testPeriodTypes() {
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011"), "Yearly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("201101"), "Monthly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011W1"), "Weekly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011W32"), "Weekly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011BiW1"), "BiWeekly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011BiW32"), "BiWeekly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("20110101"), "Daily");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011Q3"), "Quarterly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("201101B"), "BiMonthly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011S1"), "SixMonthly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011AprilS1"), "SixMonthlyApril");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011April"), "FinancialApril");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011July"), "FinancialJuly");
		Assert.assertEquals(DHISPeriodTypeUnit.getPeriodTypeFromIsoString("2011Oct"), "FinancialOct");
		
		AdxDataValueGroupPeriod py = new AdxDataValueGroupPeriod("2017");
		AdxDataValueGroupPeriod pd = new AdxDataValueGroupPeriod("20110101");
		AdxDataValueGroupPeriod pm = new AdxDataValueGroupPeriod("201101");
		AdxDataValueGroupPeriod pyh = new AdxDataValueGroupPeriod("2011S2");
		AdxDataValueGroupPeriod pyq = new AdxDataValueGroupPeriod("2014Q3");
		AdxDataValueGroupPeriod pw = new AdxDataValueGroupPeriod("2017W32");
		
		Assert.assertEquals("2011-01-01", pd.getDHISAdxStartDate());
		Assert.assertEquals("2017-01-01", py.getDHISAdxStartDate());
		Assert.assertEquals("2011-01-01", pm.getDHISAdxStartDate());
		Assert.assertEquals("2011-07-01", pyh.getDHISAdxStartDate());
		Assert.assertEquals("2014-07-01", pyq.getDHISAdxStartDate());
		Assert.assertEquals("2017-08-06", pw.getDHISAdxStartDate());
		
		Assert.assertEquals("2011-01-01", pd.getdHISAdxEndDate());
		Assert.assertEquals("2017-12-31", py.getdHISAdxEndDate());
		Assert.assertEquals("2011-01-31", pm.getdHISAdxEndDate());
		Assert.assertEquals("2011-12-31", pyh.getdHISAdxEndDate());
		Assert.assertEquals("2014-09-30", pyq.getdHISAdxEndDate());
		Assert.assertEquals("2017-08-12", pw.getdHISAdxEndDate());
	}
}
