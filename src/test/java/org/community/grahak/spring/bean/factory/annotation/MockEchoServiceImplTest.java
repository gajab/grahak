package org.community.grahak.spring.bean.factory.annotation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
  "classpath*:applicationContext-test.xml"
   })

public class MockEchoServiceImplTest {

  @Autowired
  private EchoDAO echoDAO;

  @Test
  public void testMockEcho() {
    //calling the first mock method
    String serverEcho = echoDAO.echo();
    System.out.println("first serverEcho = "+serverEcho);

    //calling the second mock method
    serverEcho = echoDAO.anotherEcho();
    System.out.println("second serverEcho = "+serverEcho);
  }

}
