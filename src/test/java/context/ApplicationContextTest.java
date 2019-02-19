package context;

import context.impl.GenericApplicationContext;
import entity.MailService;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationContextTest {

    @Test
    public void testGetBeanById() {
        GenericApplicationContext applicationContext = new GenericApplicationContext("src/test/resources/context.xml");

        MailService mailService = (MailService) applicationContext.getBean("mailServiceInstance");
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }

    @Test
    public void testGetBeanByClass() {
        GenericApplicationContext applicationContext = new GenericApplicationContext("src/test/resources/context.xml");

        MailService mailService = applicationContext.getBean(MailService.class);
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }


    @Test
    public void testGetBeanByIdAndClass() {
        GenericApplicationContext applicationContext = new GenericApplicationContext("src/test/resources/context.xml");

        MailService mailService = applicationContext.getBean("mailServiceInstance", MailService.class);
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }
}
