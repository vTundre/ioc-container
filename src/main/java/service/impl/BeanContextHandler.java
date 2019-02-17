package service.impl;

import entity.BeanDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BeanContextHandler extends DefaultHandler {

    private LinkedList<BeanDefinition> beanDefinitionsList = new LinkedList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("bean")) {

            String id = attributes.getValue("id");
            String className = attributes.getValue("class");
            if (id == null || className == null) {
                throw new SAXException("ID or ClassName of bean is missed");
            }

            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setId(id);
            beanDefinition.setClassName(className);
            beanDefinition.setRefDependencies(new HashMap<>());
            beanDefinition.setValueDependencies(new HashMap<>());
            beanDefinitionsList.add(beanDefinition);

        } else if (qName.equalsIgnoreCase("property")) {
            if (attributes.getLength() == 0) {
                throw new SAXException("Missed property attributes");
            }
            BeanDefinition lastBeanDefinition = beanDefinitionsList.getLast();

            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            String ref = attributes.getValue("ref");

            if (name != null && value != null && ref == null) {
                lastBeanDefinition.getValueDependencies().put(name, value);
            } else if (name != null && value == null && ref != null) {
                lastBeanDefinition.getRefDependencies().put(name, ref);
            } else {
                throw new SAXException("Bean properties are incorrect");
            }
        }
    }

    public List<BeanDefinition> getBeanDefinitionList() {
        return beanDefinitionsList;
    }
}
