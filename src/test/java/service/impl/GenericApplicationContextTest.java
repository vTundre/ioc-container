package service.impl;

import entity.Bean;
import entity.DefaultUserService;
import entity.MailService;
import org.junit.Assert;
import org.junit.Test;
import service.BeanDefinitionReader;

import java.util.List;

public class GenericApplicationContextTest {

    @Test
    public void testCreateBeans() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        GenericApplicationContext applicationContext = new GenericApplicationContext(beanDefinitionReader.getBeanDefinition());
        List<Bean> beanList = applicationContext.getBeans();

        Assert.assertEquals("mailServiceInstance", beanList.get(0).getId());
        Assert.assertEquals("instanceUserService", beanList.get(1).getId());
        Assert.assertEquals("empty", beanList.get(2).getId());

        Assert.assertEquals("entity.MailService", beanList.get(0).getValue().getClass().getName());
        Assert.assertEquals("entity.DefaultUserService", beanList.get(1).getValue().getClass().getName());
        Assert.assertEquals("entity.Empty", beanList.get(2).getValue().getClass().getName());
    }

    @Test
    public void testInjectValuesDependencies() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        GenericApplicationContext applicationContext = new GenericApplicationContext(beanDefinitionReader.getBeanDefinition());
        List<Bean> beanList = applicationContext.getBeans();

        MailService mailService = (MailService)beanList.get(0).getValue();
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }

    @Test
    public void testInjectRefDependencies() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        GenericApplicationContext applicationContext = new GenericApplicationContext(beanDefinitionReader.getBeanDefinition());
        List<Bean> beanList = applicationContext.getBeans();

        DefaultUserService userService = (DefaultUserService)beanList.get(1).getValue();
        MailService mailService = userService.getMailService();
        Assert.assertEquals(3000, mailService.getPort());
        Assert.assertEquals("POP3", mailService.getProtocol());
    }

}
