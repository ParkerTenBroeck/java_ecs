package ecs;

import ecs.util.tupple.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Query<Tuple> {

    private Class<?>[] components;
    private HashMap<Long, Object>[] componentMaps;
    long architype;
    private Tuple t1;
    private Entity<Tuple> entity = new Entity<>();
    private ECS ecs;
    private ArrayList<Iterator<Long>> iter = new ArrayList<>();

    protected Query(Tuple t1, Class<?>[] components){
        this.components = components;
        this.componentMaps = new HashMap[components.length];
        this.t1 = t1;
    }

    protected void setup(ECS ecs){
        this.ecs = ecs;
        this.entity.tuple = this.t1;
        this.architype = ecs.getArchitype(this.components);
        this.componentMaps = new HashMap[Long.bitCount(this.architype)];
        reset();
    }

    public void reset(){
        iter.clear();
        for(var v : this.ecs.entityArchetypes.entrySet()){
            if ((v.getKey() & this.architype) == this.architype){
                iter.add(v.getValue().iterator());
            }
        }
        for(int i = 0; i < this.componentMaps.length; i ++){
            this.componentMaps[i] = ecs.componentMap.get(this.components[i].toString());
        }
    }

    public Entity<Tuple> next(){
        if (iter.size() == 0){
            return null;
        }else{
            var top = iter.get(iter.size() - 1);
            while(!top.hasNext()){
                iter.remove(iter.size() - 1);
                if (iter.size() == 0){
                    return null;
                }
                top = iter.get(iter.size() - 1);
            }
            var entity = top.next();
            if (this.entity.tuple instanceof Tuple2<?,?> tuple){
                test(tuple, entity);
            }
            this.entity.entity = entity;
            return this.entity;
        }
    }

    private <T1, T2> void test(Tuple2<T1, T2> tuple, long entity){
        tuple.t1 = (T1)this.componentMaps[0].get(entity);
        tuple.t2 = (T2)this.componentMaps[1].get(entity);
    }

    public static class Entity<Tuple> {
        public long entity;
        public Tuple tuple;
    }
}


