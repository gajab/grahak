package org.community.grahak.spring.bean.factory.annotation;

import static org.junit.Assert.assertTrue;

import org.community.grahak.cxf.ClientProxyFactoryBeanFactory;
import org.community.grahak.spring.bean.factory.annotation.GrahakAnnotationBeanPostProcessor;
import org.community.grahak.util.ServiceCallHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/config/grahak-context.xml"
})

public class ServiceProxyAnnotationBeanPostProcessorTest {

	private ApplicationContext applicationContext;

    @Autowired
	private GrahakAnnotationBeanPostProcessor beanProcessor;	
	private ClientProxyFactoryBeanFactory clientFactory;	
	private ServiceCallHandler serviceHandler;

	EchoDAO beanObj;

	@Before
	public void setUp() throws Exception {

		beanObj = new EchoDAO();

	}

    @Test
    public void testDummy()
    {
    }

   @SuppressWarnings("unchecked")
   @Test
   public void testBeanProcessorWithNameAndType() throws Throwable 
    {

       applicationContext = new ClassPathXmlApplicationContext(new String[] {"/applicationContext-test.xml"});

       //beanProcessor = new GrahakAnnotationBeanPostProcessor();
       beanProcessor.setApplicationContext(applicationContext);

       boolean flag = beanProcessor.postProcessAfterInstantiation(beanObj, "echoService");
       assertTrue("Autowire by name and type successful for EchoService..", flag);

       flag = beanProcessor.postProcessAfterInstantiation(beanObj, "anotherEchoService");
       assertTrue("Autowire by name and type successful for AnotherEchoService..", flag);     
   }

	
	@After 
    public void tearDown() { 
		
		applicationContext = null;
		beanProcessor = null;
		serviceHandler = null;
		clientFactory = null;
    } 
}
