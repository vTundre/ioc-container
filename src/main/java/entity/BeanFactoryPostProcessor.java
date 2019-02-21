package entity;

import java.util.Map;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinition);
}
