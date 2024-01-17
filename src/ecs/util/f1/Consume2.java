package ecs.util.f1;

public interface Consume2<T1, T2> extends Function {
    void accept(T1 t1, T2 t2);
}

