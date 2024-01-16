package ecs.util.tupple;

public class Tuple4<T1, T2, T3, T4> implements DynTuple{
    public T1 t1;
    public T2 t2;
    public T3 t3;
    public T4 t4;

    public Tuple4(){}

    public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4){
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    @Override
    public void set(Object... objects) {
        this.t1 = (T1)objects[0];
        this.t2 = (T2)objects[1];
        this.t3 = (T3)objects[2];
        this.t4 = (T4)objects[3];
    }

    @Override
    public Object[] get() {
        return new Object[]{this.t1, this.t2, this.t3, this.t4};
    }

    @Override
    public DynTuple copy() {
        return new Tuple4<>();
    }
}
