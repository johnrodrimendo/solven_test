/**
 * 
 */
package com.affirm.common.util;

import org.springframework.context.MessageSource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author jrodriguez
 *
 */
public class SpringUtil {

	public static MessageSource getMEssageSource(){
		WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
		return (MessageSource)webAppContext.getBean("messageSource");
	}
	
}
