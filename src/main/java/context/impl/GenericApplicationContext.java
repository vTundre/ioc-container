package context.impl;

import context.ApplicationContext;
import entity.Bean;
import entity.BeanDefinition;
import entity.BeanFactoryPostProcessor;
import entity.BeanPostProcessor;
import reader.impl.XMLBeanDefinitionReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GenericApplicationContext implements ApplicationContext {
    private Map<String, BeanDefinition> beanDefinitionList;
    private Map<String, Bean> beanList;

    public GenericApplicationContext(String path) {
        beanDefinitionList = new HashMap<>();
        beanList = new HashMap<>();

        //read bean definitions
        beanDefinitionList = new XMLBeanDefinitionReader(path).getBeanDefinition();

        //definitions postprocessing
        postProcessBeanDefinitions(beanDefinitionList);

        //beans
        createBeans(beanDefinitionList, beanList);
        postProcessBeans(beanList, "postProcessBeforeInitialization");
        postConstruct(beanList);
        postProcessBeans(beanList, "postProcessAfterInitialization");
    }

    @Override
    public Object getBean(String beanId) {
        return beanList.get(beanId).getObject();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        for (Map.Entry<String, Bean> entry : beanList.entrySet()) {
            Class currentBeanClazz = entry.getValue().getObject().getClass();
            if (currentBeanClazz.equals(clazz)) {
                return (T) entry.getValue().getObject();
            }
        }
        return null;
    }

    @Override
    public <T> T getBean(String beanId, Class<T> clazz) {
        for (Map.Entry<String, Bean> entry : beanList.entrySet()) {
            Class currentBeanClazz = entry.getValue().getObject().getClass();
            if (currentBeanClazz.equals(clazz) && entry.getKey().equals(beanId)) {
                return (T) entry.getValue().getObject();
            }
        }
        return null;
    }

    private void postProcessBeanDefinitions(Map<String, BeanDefinition> beanDefinitionList) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionList.entrySet()) {
            String className = entry.getValue().getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                if (Arrays.asList(clazz.getInterfaces()).contains(BeanFactoryPostProcessor.class)) {
                    Method method = clazz.getMethod("postProcessBeanFactory", Map.class);
                    Constructor<?> constructor = clazz.getConstructor();
                    Object object = constructor.newInstance();
                    method.invoke(object, beanDefinitionList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void postProcessBeans(Map<String, Bean> beanList, String methodName) {
        for (Map.Entry<String, Bean> systemEntry : beanList.entrySet()) {
            if (systemEntry.getValue().isSystem()) {
                Object systemObject = systemEntry.getValue().getObject();
                try {
                    Method method = systemObject.getClass().getMethod(methodName, Object.class);

                    for (Map.Entry<String, Bean> entry : beanList.entrySet()) {
                        if (!entry.getValue().isSystem()) {
                            Object object = entry.getValue().getObject();
                            Object modifiedObject = method.invoke(systemObject, object);

                            Bean bean = entry.getValue();
                            bean.setObject(modifiedObject);
                            beanList.put(entry.getKey(), bean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void postConstruct(Map<String, Bean> beanList) {
        for (Map.Entry<String, Bean> entry : beanList.entrySet()) {
            Object object = entry.getValue().getObject();
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getParameterCount() == 0 && method.isAnnotationPresent(entity.PostConstruct.class)) {
                    try {
                        method.invoke(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
                bean.setObject(object);
                bean.setSystem(Arrays.asList(object.getClass().getInterfaces()).contains(BeanPostProcessor.class));

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