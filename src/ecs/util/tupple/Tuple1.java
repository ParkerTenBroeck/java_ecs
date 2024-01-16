package ecs.util.tupple;

public class Tuple1<T1> implements DynTuple {
    public T1 t1;

    public Tuple1(){}

    public Tuple1(T1 t1){
        this.t1 = t1;
    }


    @Override
    public void set(Object... objects) {
        this.t1 = (T1)objects[0];
    }

    @Override
    public Object[] get() {
        return new Object[]{this.t1};
    }

    @Override
    public DynTuple copy() {
        return new Tuple1<>();
    }

}
