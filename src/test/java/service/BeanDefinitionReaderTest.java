package service;

import entity.BeanDefinition;
import org.junit.Assert;
import org.junit.Test;
import service.impl.XMLBeanDefinitionReader;

import java.util.List;

public class BeanDefinitionReaderTest {

    @Test
    public void testGetBeanDefinition() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        List<BeanDefinition> list = beanDefinitionReader.getBeanDefinition();

        Assert.assertEquals("entity.MailService", list.get(0).getClassName());
        Assert.assertEquals("entity.DefaultUserService", list.get(1).getClassName());
        Assert.assertEquals("entity.Empty", list.get(2).getClassName());

        Assert.assertEquals("mailServiceInstance", list.get(0).getId());
        Assert.assertEquals("instanceUserService", list.get(1).getId());
        Assert.assertEquals("empty", list.get(2).getId());

        Assert.assertEquals("3000", list.get(0).getValueDependencies().get("port"));
        Assert.assertEquals("POP3", list.get(0).getValueDependencies().get("protocol"));

        Assert.assertEquals("mailServiceInstance", list.get(1).getRefDependencies().get("mailService"));
    }
}
