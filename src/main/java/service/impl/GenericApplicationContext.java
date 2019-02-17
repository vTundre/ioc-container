package service.impl;

import entity.Bean;
import entity.BeanDefinition;
import service.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericApplicationContext implements ApplicationContext {
    private List<BeanDefinition> beanDefinitionList;
    private List<Bean> beanList;

    public GenericApplicationContext(List<BeanDefinition> beanDefinitionList) {
        this.beanDefinitionList = beanDefinitionList;
        beanList = new ArrayList<>();
        createBeans();
    }

    @Override
    public Object getBean(String beanId) {
        for (Bean bean : beanList) {
            if (bean.getId().equals(beanId)) {
                return bean.getValue();
            }
        }
        throw new RuntimeException("Bean doesn't exists");
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        for (Bean bean : beanList) {
            Class currentBeanClazz = bean.getValue().getClass();
            if (currentBeanClazz.equals(clazz)) {
                return (T) bean.getValue();
            }
        }
        throw new RuntimeException("Bean doesn't exists");
    }

    @Override
    public <T> T getBean(String beanId, Class<T> clazz) {
        for (Bean bean : beanList) {
            Class currentBeanClazz = bean.getValue().getClass();
            if (bean.getId().equals(beanId) && currentBeanClazz.equals(clazz)) {
                return (T) bean.getValue();
            }
        }
        throw new RuntimeException("Bean doesn't exists");
    }

    public void createBeans() {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getClassName());
                Constructor<?> constructor = clazz.getConstructor();
                Object object = constructor.newInstance();
                injectValueDependencies(beanDefinition, object);
                injectRefDependencies(beanDefinition, object);

                Bean bean = new Bean();
                bean.setId(beanDefinition.getId());
                bean.setValue(object);
                beanList.add(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void injectValueDependencies(BeanDefinition beanDefinition, Object object) {
        for (Map.Entry<String, String> entry : beanDefinition.getValueDependencies().entrySet()) {
            String setterName = getSetterName(entry.getKey());
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    Class<?>[] parametersTypes = method.getParameterTypes();
                    if (parametersTypes.length == 1) {
                        try {
                            if (parametersTypes[0].equals(int.class)) {
                                method.invoke(object, Integer.valueOf(entry.getValue()));
                            } else if (parametersTypes[0].equals(char.class)) {
                                method.invoke(object, entry.getValue().charAt(0));
                            } else {
                                method.invoke(object, parametersTypes[0].cast(entry.getValue()));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public void injectRefDependencies(BeanDefinition beanDefinition, Object object) {
        for (Map.Entry<String, String> entry : beanDefinition.getRefDependencies().entrySet()) {
            String setterName = getSetterName(entry.getKey());
            Object linkedBean = getBean(entry.getValue());
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    try {
                        method.invoke(object, linkedBean);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        }
    }

    private static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public List<Bean> getBeans() {
        return beanList;
    }
}