package org.community.grahak.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Fields annotated with this annotation will get injected by Apache CXF
 * proxy object
 * 
 * @author rkarwa
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
public @interface Grahak  {
	
	public String channel() default "";
	
	/**
	 * the name provided on web service interface is port name and not the service name.
	 * the service name should be provided with @WebService (serviceName=""). but it is
	 * not mandatory by JAX-WS hence we can not reply on this. 
	 * Also we do not want to add the service name in channel configuration file as
	 * one channel should be used for different service calls hosted on same host(channel)
	 * 
	 */
	public String serviceName() default "";
	public String endpoint() default "";

}
