package org.openmrs.module.dhisconnector.web.bean;

import org.openmrs.api.context.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

@Configuration("dhisconnector.dynamicBeanConfiguration")
public class DynamicBeanConfiguration {

	/**
	 * The DefaultAnnotationHandlerMapping class was deprecated and eventually removed in Spring 5
	 * The recommended replacement class RequestMappingHandlerMapping was introduced in Spring 3.1.0
	 * which is not available on OpenMRS platform versions 1.9.x and 1.10.x which run Spring 3.0.5
	 * That's why we can't just statically replace this class in the webModuleApplicationContext.xml
	 * file.
	 */
	@Bean
	public AbstractHandlerMapping getHandlerMapping() throws Exception {

		Class<?> clazz;
		try {
			clazz = Context.loadClass("org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping");
		} catch (ClassNotFoundException e) {
			clazz = Context.loadClass("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");
		}

		return (AbstractHandlerMapping) clazz.newInstance();
	}

}

