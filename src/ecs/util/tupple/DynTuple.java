package ecs.util.tupple;

public interface DynTuple{
    void set(Object... objects);
    Object[] get();

    DynTuple copy();
}
