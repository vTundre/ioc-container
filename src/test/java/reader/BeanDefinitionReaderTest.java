package reader;

import entity.BeanDefinition;
import org.junit.Assert;
import org.junit.Test;
import reader.impl.XMLBeanDefinitionReader;

import java.util.Map;

public class BeanDefinitionReaderTest {

    @Test
    public void testGetBeanDefinition() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml");
        Map<String, BeanDefinition> list = beanDefinitionReader.getBeanDefinition();

        Assert.assertEquals("entity.MailService", list.get("mailServiceInstance").getClassName());
        Assert.assertEquals("entity.DefaultUserService", list.get("instanceUserService").getClassName());
        Assert.assertEquals("entity.Empty", list.get("empty").getClassName());

        Assert.assertEquals("mailServiceInstance", list.get("mailServiceInstance").getId());
        Assert.assertEquals("instanceUserService", list.get("instanceUserService").getId());
        Assert.assertEquals("empty", list.get("empty").getId());

        Assert.assertEquals("3000", list.get("mailServiceInstance").getValueDependencies().get("port"));
        Assert.assertEquals("POP3", list.get("mailServiceInstance").getValueDependencies().get("protocol"));

        Assert.assertEquals("mailServiceInstance", list.get("instanceUserService").getRefDependencies().get("mailService"));
    }
}
