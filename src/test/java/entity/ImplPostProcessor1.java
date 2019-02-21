package entity;

public class ImplPostProcessor1 implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object object) {
        System.out.println("Post before initialization1");
        return object;
    }

    @Override
    public Object postProcessAfterInitialization(Object object) {
        System.out.println("Post after initialization1");
        return object;
    }
}
