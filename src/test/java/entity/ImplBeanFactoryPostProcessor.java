package entity;

import java.util.Map;

public class ImplBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinition) {
        System.out.println("Factory processing");
    }
}
