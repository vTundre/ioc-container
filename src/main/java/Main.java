import service.ApplicationContext;
import service.BeanDefinitionReader;
import service.impl.GenericApplicationContext;
import service.impl.XMLBeanDefinitionReader;

public class Main {
    public static void createContext(String path){
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader(path);
        ApplicationContext applicationContext = new GenericApplicationContext(beanDefinitionReader.getBeanDefinition());
    }
}
