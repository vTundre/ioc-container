package entity;

public class ImplPostProcessor2 implements  BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object object) {
        System.out.println("Post before initialization2");
        return object;
    }

    @Override
    public Object postProcessAfterInitialization(Object object) {
        System.out.println("Post after initialization2");
        return object;
    }
}
