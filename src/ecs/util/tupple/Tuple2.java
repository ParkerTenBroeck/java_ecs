package ecs.util.tupple;

public class Tuple2<T1, T2> implements DynTuple {
    public T1 t1;
    public T2 t2;

    public Tuple2(){}

    public Tuple2(T1 t1, T2 t2){
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public void set(Object... objects) {
        this.t1 = (T1)objects[0];
        this.t2 = (T2)objects[1];
    }

    @Override
    public Object[] get() {
        return new Object[]{this.t1, this.t2};
    }

    @Override
    public DynTuple copy() {
        return new Tuple2<>();
    }
}
