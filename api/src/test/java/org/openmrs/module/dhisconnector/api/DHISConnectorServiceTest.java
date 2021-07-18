package org.openmrs.module.dhisconnector.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

/**
 * Created by k-joseph on 04/07/2017.
 */
public class DHISConnectorServiceTest extends BaseModuleContextSensitiveTest {
	@Mock
	private DHISConnectorService dhisConnectorService;
	
	@Test
	public void testServiceInit() {
		Assert.assertNotNull(Context.getService(DHISConnectorService.class));
	}
	
	@Test
	public void testReportToDataSetMappingService() {
		Assert.assertEquals(0, Context.getService(DHISConnectorService.class).getAllReportToDataSetMappings().size());
	}
	
	@Test
	public void transformToDHISPeriod() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//month is 0based index
		Calendar startDate = new GregorianCalendar(2017, 11, 01);
		Calendar endDate = new GregorianCalendar(2017, 11, 01);
		String period = "";
		
		//test daily, date now at 2017/dec/01
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "Daily", null);
		Assert.assertEquals("20171130", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171130", sdf.format(endDate.getTime()));
		Assert.assertEquals(period, "20171130");
		
		//test weekly, date now at 2017/nov/30
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "Weekly", null);
		Assert.assertEquals("20171120", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171126", sdf.format(endDate.getTime()));
		Assert.assertEquals(period, "2017W48");

		//test weekly wednesday, date now at 2017/nov/20
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "WeeklyWednesday", null);
		Assert.assertEquals("20171108", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171114", sdf.format(endDate.getTime()));
		Assert.assertEquals(period, "2017WedW46");

		//test weekly thursday, date now at 2017/nov/14
		startDate = (Calendar) endDate.clone();
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "WeeklyThursday", null);
		Assert.assertEquals("20171102", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171108", sdf.format(endDate.getTime()));
		Assert.assertEquals(period, "2017ThuW45");

		//test weekly saturday, date now at 2017/nov/08
		startDate = (Calendar) endDate.clone();
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "WeeklySaturday", null);
		Assert.assertEquals("20171028", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171103", sdf.format(endDate.getTime()));
		Assert.assertEquals(period, "2017SatW44");
		
		//test monthly, date now at 2017/nov/03
		startDate = (Calendar) endDate.clone();
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "Monthly", null);
		Assert.assertEquals("20171001", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171031", sdf.format(endDate.getTime()));
		Assert.assertEquals(period, "201710");
		
		//test daily with lastRun, date now at 2017/oct/31
		startDate = (Calendar) endDate.clone();
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "Daily",
		    new GregorianCalendar(2017, 9, 30).getTime());
		Assert.assertEquals("20171030", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171030", sdf.format(endDate.getTime()));
		Assert.assertNull(period);
		
		//test weekly with lastRun, date now at 2017/oct/30
		startDate = (Calendar) endDate.clone();
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "Weekly",
		    new GregorianCalendar(2017, 9, 25).getTime());
		Assert.assertEquals("20171023", sdf.format(startDate.getTime()));
		Assert.assertEquals("20171029", sdf.format(endDate.getTime()));
		Assert.assertNull(period);
		
		//test monthly with lastRun, date now at 2017/oct/07
		startDate = (Calendar) endDate.clone();
		period = Context.getService(DHISConnectorService.class).transformToDHISPeriod(startDate, endDate, "Monthly",
		    new GregorianCalendar(2017, 8, 28).getTime());
		Assert.assertEquals("20170901", sdf.format(startDate.getTime()));
		Assert.assertEquals("20170930", sdf.format(endDate.getTime()));
		Assert.assertNull(period);
	}

	@Test
	public void testGetLocationToOrgUnitMappingByUuid() {
		LocationToOrgUnitMapping locationToOrgUnitMapping = new LocationToOrgUnitMapping();
		doReturn(locationToOrgUnitMapping)
				.when(dhisConnectorService)
				.getLocationToOrgUnitMappingByUuid(anyString());

		Assert.assertEquals(locationToOrgUnitMapping,
				dhisConnectorService.getLocationToOrgUnitMappingByUuid("abc"));
	}

	@Test
	public void testGetLocationToOrgUnitMappingByLocationByOrgUnitUid() {
		LocationToOrgUnitMapping locationToOrgUnitMapping = new LocationToOrgUnitMapping();
		doReturn(locationToOrgUnitMapping)
				.when(dhisConnectorService)
				.getLocationToOrgUnitMappingByOrgUnitUid(anyString());

		Assert.assertEquals(locationToOrgUnitMapping,
				dhisConnectorService.getLocationToOrgUnitMappingByOrgUnitUid("org"));
	}

	@Test
	public void testSaveLocationToOrgUnitMapping() {
		LocationToOrgUnitMapping locationToOrgUnitMapping = new LocationToOrgUnitMapping();
		locationToOrgUnitMapping.setOrgUnitUid("abc");
		locationToOrgUnitMapping.setLocation(Context.getLocationService().getDefaultLocation());

		Context.getService(DHISConnectorService.class).saveLocationToOrgUnitMapping(locationToOrgUnitMapping);
		Assert.assertEquals(1, Context.getService(DHISConnectorService.class).getAllLocationToOrgUnitMappings().size());
	}

	@Test
	public void testDeleteLocationToOrgUnitMappingsByLocation() {
		Context.getService(DHISConnectorService.class).deleteLocationToOrgUnitMappingsByLocation(
				Context.getLocationService().getDefaultLocation());
		Assert.assertEquals(0, Context.getService(DHISConnectorService.class).getAllLocationToOrgUnitMappings().size());
	}
}
