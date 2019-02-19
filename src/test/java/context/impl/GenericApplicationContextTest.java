package context.impl;

import entity.DefaultUserService;
import entity.MailService;
import org.junit.Assert;
import org.junit.Test;

public class GenericApplicationContextTest {

    @Test
    public void testCreateBeans() {
        GenericApplicationContext applicationContext = new GenericApplicationContext("src/test/resources/context.xml");

        Assert.assertNotNull(applicationContext.getBean("mailServiceInstance"));
        Assert.assertNotNull(applicationContext.getBean("instanceUserService"));
        Assert.assertNotNull(applicationContext.getBean("empty"));
    }

    @Test
    public void testInjectValuesDependencies() {
        GenericApplicationContext applicationContext = new GenericApplicationContext("src/test/resources/context.xml");

        MailService mailService = applicationContext.getBean("mailServiceInstance", MailService.class);
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }

    @Test
    public void testInjectRefDependencies() {
        GenericApplicationContext applicationContext = new GenericApplicationContext("src/test/resources/context.xml");

        DefaultUserService userService = applicationContext.getBean("instanceUserService", DefaultUserService.class);
        MailService mailService = userService.getMailService();
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }

}
