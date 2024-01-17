package ecs;

import ecs.util.f1.*;
import ecs.util.tupple.*;


public final class System {

    protected final Consume1<ECS> runner;

    public System(Consume1<ECS> runner){
        this.runner = runner;
    }

    private static java.lang.reflect.Type[] resolveGenerics(Class<?> clazz){
        return ecs.util.TypeReflection.resolveGenericInterfaceArguments(clazz, clazz.getGenericInterfaces()[0]);
    }

    public static <R1> System makeSystem(final Consume1<R1> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        return new System((ecs) -> {
            var r1 = ecs.resources.getResource(r1_type);
            func.accept(r1);
        });
    }


    public static <R1, R2> System makeSystem(final Consume2<R1, R2> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        return new System((ecs) -> {
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            func.accept(r1, r2);
        });
    }

    public static <R1, R2, R3> System makeSystem(final Consume3<R1, R2, R3> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        final var r3_type = (Class<R3>)types[2];
        return new System((ecs) -> {
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            var r3 = ecs.resources.getResource(r3_type);
            func.accept(r1, r2, r3);
        });
    }

    public static <R1, R2, R3, R4> System makeSystem(final Consume4<R1, R2, R3, R4> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        final var r3_type = (Class<R3>)types[2];
        final var r4_type = (Class<R4>)types[3];
        return new System((ecs) -> {
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            var r3 = ecs.resources.getResource(r3_type);
            var r4 = ecs.resources.getResource(r4_type);
            func.accept(r1, r2, r3, r4);
        });
    }

    public static <R1, C1> System makeSystem(final Consume1<C1> ts, final Consume2<R1, Query<Tuple1<C1>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var components = resolveGenerics(ts.getClass());
        return new System((ecs) -> {
            var r1 = ecs.resources.getResource(r1_type);
            func.accept(r1, null);
        });
    }

    public static <R1, C1, C2> System makeSystem(final Consume2<C1, C2> ts, final Consume2<R1, Query<Tuple2<C1, C2>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var components = (Class<?>[])resolveGenerics(ts.getClass());
        final var tuple = new Tuple2<C1, C2>();
        final var iter = new Query<>(tuple, components);
        return new System((ecs) -> {
            iter.setup(ecs);
            var r1 = ecs.resources.getResource(r1_type);
            func.accept(r1, iter);
        });
    }

    public static <R1, C1, C2, C3> System makeSystem(final Consume3<C1, C2, C3> ts, final Consume2<R1, Query<Tuple3<C1, C2, C3>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var components = (Class<?>[])resolveGenerics(ts.getClass());
        final var tuple = new Tuple3<C1, C2, C3>();
        final var iter = new Query<>(tuple, components);
        return new System((ecs) -> {
            iter.setup(ecs);
            var r1 = ecs.resources.getResource(r1_type);
            func.accept(r1, iter);
        });
    }

    public static <R1, R2, C1> System makeSystem(final Consume1<C1> ts, final Consume3<R1, R2, Query<Tuple1<C1>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        final var components = (Class<?>[])resolveGenerics(ts.getClass());
        final var tuple = new Tuple1<C1>();
        final var iter = new Query<>(tuple, components);
        return new System((ecs) -> {
            iter.setup(ecs);
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            func.accept(r1, r2, iter);
        });
    }

    public static <R1, R2, R3, C1> System makeSystem(final Consume1<C1> ts, final Consume4<R1, R2, R3, Query<Tuple1<C1>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        final var r3_type = (Class<R3>)types[2];
        final var components = (Class<?>[])resolveGenerics(ts.getClass());
        final var tuple = new Tuple1<C1>();
        final var iter = new Query<>(tuple, components);
        return new System((ecs) -> {
            iter.setup(ecs);
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            var r3 = ecs.resources.getResource(r3_type);
            func.accept(r1, r2, r3, iter);
        });
    }

    public static <R1, R2, C1, C2> System makeSystem(final Consume2<C1, C2> ts, final Consume3<R1, R2, Query<Tuple2<C1, C2>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        final var components = (Class<?>[])resolveGenerics(ts.getClass());
        final var tuple = new Tuple2<C1, C2>();
        final var iter = new Query<>(tuple, components);
        return new System((ecs) -> {
            iter.setup(ecs);
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            func.accept(r1, r2, iter);
        });
    }

    public static <R1, R2, C1, C2, C3> System makeSystem(final Consume3<C1, C2, C3> ts, final Consume3<R1, R2, Query<Tuple3<C1, C2, C3>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        final var components = (Class<?>[])resolveGenerics(ts.getClass());
        final var tuple = new Tuple3<C1, C2, C3>();
        final var iter = new Query<>(tuple, components);
        return new System((ecs) -> {
            iter.setup(ecs);
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            func.accept(r1, r2, iter);
        });
    }

    public static <R1, R2, R3, C1, C2> System makeSystem(final Consume2<C1, C2> ts, final Consume4<R1, R2, R3, Query<Tuple2<C1, C2>>> func){
        final var types = resolveGenerics(func.getClass());
        final var r1_type = (Class<R1>)types[0];
        final var r2_type = (Class<R2>)types[1];
        final var r3_type = (Class<R3>)types[2];
        final var components = (Class<?>[])resolveGenerics(ts.getClass());
        final var tuple = new Tuple2<C1, C2>();
        final var iter = new Query<>(tuple, components);
        return new System((ecs) -> {
            iter.setup(ecs);
            var r1 = ecs.resources.getResource(r1_type);
            var r2 = ecs.resources.getResource(r2_type);
            var r3 = ecs.resources.getResource(r3_type);
            func.accept(r1, r2, r3, iter);
        });
    }
}
