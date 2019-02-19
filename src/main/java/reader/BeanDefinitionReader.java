package reader;

import entity.BeanDefinition;

import java.util.Map;

public interface BeanDefinitionReader {

    Map<String, BeanDefinition> getBeanDefinition();
}
