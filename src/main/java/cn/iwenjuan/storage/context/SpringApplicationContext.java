package cn.iwenjuan.storage.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @author li1244
 * @date 2023/3/24 13:45
 */
public class SpringApplicationContext implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringApplicationContext.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        if (ObjectUtils.isEmpty(applicationContext)) {
            throw new RuntimeException("applicationContext is null");
        }
        return applicationContext;
    }

    public static ListableBeanFactory getBeanFactory() {
        if (!ObjectUtils.isEmpty(beanFactory)) {
            return beanFactory;
        }
        return getApplicationContext();
    }

    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        ConfigurableListableBeanFactory factory;
        if (!ObjectUtils.isEmpty(beanFactory)) {
            return beanFactory;
        }
        return ((ConfigurableApplicationContext) getApplicationContext()).getBeanFactory();
    }

    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getBeanFactory().getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    public static String[] getBeanNamesForType(Class<?> type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    public static String getProperty(String key) {
        return getApplicationContext().getEnvironment().getProperty(key);
    }

    public static String getApplicationName() {
        return getProperty("spring.application.name");
    }

    public static String getContextPath() {
        return getProperty("server.servlet.context-path");
    }

    public static String[] getActiveProfiles() {
        return getApplicationContext().getEnvironment().getActiveProfiles();
    }

    public static String getActiveProfile() {
        String[] activeProfiles = getActiveProfiles();
        return ObjectUtils.isEmpty(activeProfiles) ? null : activeProfiles[0];
    }

    public static <T> void registerBean(String beanName, T bean) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
    }

    public static void unregisterBean(String beanName) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) factory;
        registry.destroySingleton(beanName);
    }

    public static void publishEvent(ApplicationEvent event) {
        getApplicationContext().publishEvent(event);
    }

    public static void publishEvent(Object event) {
        getApplicationContext().publishEvent(event);
    }
}
