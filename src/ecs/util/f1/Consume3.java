package ecs.util.f1;

public interface Consume3<T1, T2, T3> extends Function {
    void accept(T1 t1, T2 t2, T3 t3);
}

