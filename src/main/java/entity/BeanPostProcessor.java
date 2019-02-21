package entity;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object object);

    Object postProcessAfterInitialization(Object object);
}
