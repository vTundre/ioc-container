package service;

import entity.MailService;
import org.junit.Assert;
import org.junit.Test;
import service.impl.GenericApplicationContext;
import service.impl.XMLBeanDefinitionReader;

public class ApplicationContextTest {

    @Test
    public void testGetBeanById() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        ApplicationContext applicationContext = new GenericApplicationContext(beanDefinitionReader.getBeanDefinition());

        MailService mailService = (MailService) applicationContext.getBean("mailServiceInstance");
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }

    @Test
    public void testGetBeanByClass() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        ApplicationContext applicationContext = new GenericApplicationContext(beanDefinitionReader.getBeanDefinition());

        MailService mailService = applicationContext.getBean(MailService.class);
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }


    @Test
    public void testGetBeanByIdAndClass() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        ApplicationContext applicationContext = new GenericApplicationContext(beanDefinitionReader.getBeanDefinition());

        MailService mailService = applicationContext.getBean("mailServiceInstance", MailService.class);
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }
}
