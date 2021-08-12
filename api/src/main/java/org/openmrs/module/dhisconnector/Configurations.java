package org.openmrs.module.dhisconnector;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;

public class Configurations {

	private static String STORE_PERIOD = "dhisconnector.config.dataAndLogsStoragePeriod";

	private static String DXF_TO_ADX_SWITCH = "dhisconnector.config.dxfToAdxSwitch";
	
	private static String TOGGLE_AUTOMATION = "dhisconnector.config.enableAutoRun";
	
	//TODO check dhis version to be above 2.20 when adx was introduced
	public boolean useAdxInsteadOfDxf() {
		return "true".equals(Context.getAdministrationService().getGlobalProperty(DXF_TO_ADX_SWITCH));
	}

	/**
	 * @return number of months for dataAndLogsStoragePeriod
	 */
	public Integer getDataAndLogsStoragePeriod() {
		String v = Context.getAdministrationService().getGlobalProperty(STORE_PERIOD);

		if(StringUtils.isNotBlank(v))
			return Integer.parseInt(v);
		return 1;
	}
	
	public boolean automationEnabled() {
		return "true".equals(Context.getAdministrationService().getGlobalProperty(TOGGLE_AUTOMATION));
	}
	
	public void toggleAutomation(boolean onOrOff) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(TOGGLE_AUTOMATION);
		
		gp.setPropertyValue(Boolean.toString(onOrOff));
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
}
