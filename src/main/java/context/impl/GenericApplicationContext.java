package context.impl;

import context.ApplicationContext;
import entity.Bean;
import entity.BeanDefinition;
import reader.impl.XMLBeanDefinitionReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GenericApplicationContext implements ApplicationContext {
    private String path;
    private Map<String, BeanDefinition> beanDefinitionList;
    private Map<String, Bean> beanList;

    public GenericApplicationContext(String path) {
        this.path = path;
        beanList = new HashMap<>();
        beanDefinitionList = new XMLBeanDefinitionReader(path).getBeanDefinition();
        createBeans(beanDefinitionList, beanList);
    }

    @Override
    public Object getBean(String beanId) {
        return beanList.get(beanId).getValue();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        for (Map.Entry<String, Bean> entry : beanList.entrySet()) {
            Class currentBeanClazz = entry.getValue().getValue().getClass();
            if (currentBeanClazz.equals(clazz)) {
                return (T) entry.getValue().getValue();
            }
        }
        throw new RuntimeException("Bean doesn't exists");
    }

    @Override
    public <T> T getBean(String beanId, Class<T> clazz) {
        for (Map.Entry<String, Bean> entry : beanList.entrySet()) {
            Class currentBeanClazz = entry.getValue().getValue().getClass();
            if (currentBeanClazz.equals(clazz) && entry.getKey().equals(beanId)) {
                return (T) entry.getValue().getValue();
            }
        }
        throw new RuntimeException("Bean doesn't exists");
    }

    private void createBeans(Map<String, BeanDefinition> beanDefinitionList, Map<String, Bean> beanList) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionList.entrySet()) {
            try {
                BeanDefinition beanDefinition = entry.getValue();
                Class<?> clazz = Class.forName(beanDefinition.getClassName());
                Constructor<?> constructor = clazz.getConstructor();
                Object object = constructor.newInstance();
                injectValueDependencies(beanDefinition, object);
                injectRefDependencies(beanDefinition, object);

                Bean bean = new Bean();
                bean.setId(beanDefinition.getId());
                bean.setValue(object);
                beanList.put(beanDefinition.getId(), bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void injectValueDependencies(BeanDefinition beanDefinition, Object object) {
        for (Map.Entry<String, String> entry : beanDefinition.getValueDependencies().entrySet()) {
            String setterName = getSetterName(entry.getKey());
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    invokeSetMethod(method, object, entry.getValue());
                    break;
                }
            }
        }
    }

    private void injectRefDependencies(BeanDefinition beanDefinition, Object object) {
        for (Map.Entry<String, String> entry : beanDefinition.getRefDependencies().entrySet()) {
            String setterName = getSetterName(entry.getKey());
            Object linkedBean = getBean(entry.getValue());
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    try {
                        method.invoke(object, linkedBean);
                        break;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void invokeSetMethod(Method method, Object object, String value) {
        Class<?>[] parametersTypes = method.getParameterTypes();
        if (parametersTypes.length == 1) {
            try {
                if (parametersTypes[0].equals(int.class)) {
                    method.invoke(object, Integer.valueOf(value));
                } else if (parametersTypes[0].equals(char.class)) {
                    method.invoke(object, value.charAt(0));
                } else {
                    method.invoke(object, parametersTypes[0].cast(value));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

}