package ecs.util.tupple;

public class Tuple3<T1, T2, T3> implements DynTuple{
    public T1 t1;
    public T2 t2;
    public T3 t3;

    public Tuple3(){}

    public Tuple3(T1 t1, T2 t2, T3 t3){
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    @Override
    public void set(Object... objects) {
        this.t1 = (T1)objects[0];
        this.t2 = (T2)objects[1];
        this.t3 = (T3)objects[2];
    }

    @Override
    public Object[] get() {
        return new Object[]{this.t1, this.t2, this.t3};
    }

    @Override
    public DynTuple copy() {
        return new Tuple3<>();
    }
}
