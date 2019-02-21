package reader.impl;

import entity.BeanDefinition;
import reader.BeanDefinitionReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Map;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private String path;

    public XMLBeanDefinitionReader(String path) {
        this.path = path;
    }

    public Map<String, BeanDefinition> getBeanDefinition() {

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path))) {

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            BeanContextHandler handler = new BeanContextHandler();
            saxParser.parse(bufferedInputStream, handler);

            Map<String, BeanDefinition> beanDefinitionList = handler.getBeanDefinitionList();
            return beanDefinitionList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
