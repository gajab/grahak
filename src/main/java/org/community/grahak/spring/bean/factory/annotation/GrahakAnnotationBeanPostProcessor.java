package org.community.grahak.spring.bean.factory.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.community.grahak.annotation.Grahak;
import org.community.grahak.cxf.ClientProxyFactoryBeanFactory;
import org.community.grahak.cxf.jaxws.GrahakJaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;




/**
 * BeanPostProcessor class for {@link Grahak} annotation. This class overrides 
 * postProcessAfterInstantiation which will be called by spring after instantiating 
 * the bean and before doing field wiring. The overridden postProcessAfterInstantiation
 * method would first try to autowire the field based on type and if unsuccessful then
 * will create a Apache CXF {@link GrahakJaxWsProxyFactoryBean} proxy and inject to the field
 * annotated with {@link Grahak} annotation
 * 
 * @author rkarwa
 *
 */
public class GrahakAnnotationBeanPostProcessor extends 
		InstantiationAwareBeanPostProcessorAdapter implements  ApplicationContextAware {
	
	private static Logger log = LoggerFactory.getLogger(GrahakAnnotationBeanPostProcessor.class);
	
	private ApplicationContext applicationContext;
	private ClientProxyFactoryBeanFactory proxyFactory;
	
	public void setProxyFactory(ClientProxyFactoryBeanFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}
	
	private void injectServiceProxy(Object bean, Field field) throws IllegalAccessException
	{
		if (field.isAnnotationPresent(Grahak.class) && field.get(bean) == null) {
			
			Class<?> fieldType = field.getType();
			Object autowiredObject = null;
			//first try to autowire based on type implemented
			//TODO : temp comment while doing zmq
//			try
//			{
//				if(log.isDebugEnabled())
//					log.debug("try to autowire the field " + field.getName());
//
//				autowiredObject = applicationContext.getBean(fieldType);
//				field.set(bean, autowiredObject);
//			}
//			catch(RuntimeException ex)
//			{
//				//ignore exception
//				//if(log.isDebugEnabled())
//				//	log.debug("Could not autowired type " + fieldType + " try with CXF proxy ... ");
//			}
			
			//autowire was not successful, inject the CXF proxy
			if(field.get(bean) == null)
			{
				log.debug("Creating cxf proxy for {} ", field.getName());
				
				Grahak serviceProxy = field.getAnnotation(Grahak.class);
				Object proxy = null;
				String channelName = serviceProxy.channel();
				
				if(channelName != null && ! channelName.isEmpty())
				{
                    try
                    {
                     log.debug("[ServiceName = {} ]",serviceProxy.serviceName());
					 proxy = proxyFactory.create(channelName, ClientProxyFactoryBeanFactory.ENDPOINT_TYPE.CHANNEL, serviceProxy.serviceName(), fieldType);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
				}
				else
				{
					 String endpoint = serviceProxy.endpoint();
					 if(!StringUtils.hasText(endpoint))
					 {
						 log.error("invalid endpoint " + field.getName() + " Could not inject proxy " );
						 
					 }

					 proxy = proxyFactory.create(endpoint, ClientProxyFactoryBeanFactory.ENDPOINT_TYPE.URL,
							 serviceProxy.serviceName(), fieldType);

				}
				field.set(bean, proxy);
				
				if(log.isDebugEnabled())
					log.debug("cxf proxy " + field.getName() + " injected successfully ");
			}
		}
	}
	


	@Override
	public boolean postProcessAfterInstantiation(final Object bean, String beanName) throws BeansException
	{
		
		ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback()
		{
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				field.setAccessible(true);
				
				injectServiceProxy(bean, field);
		
			}
		}
		);

		return true;
	}
	
	/*@Override
	public int getOrder() {
		//same as autowired
		return LOWEST_PRECEDENCE-2;
	}*/

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}


}
