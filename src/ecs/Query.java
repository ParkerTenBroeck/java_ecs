package ecs;

import ecs.util.tupple.DynTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Query<Tuple extends DynTuple> implements Cloneable{

    private Class<?>[] components;
    private HashMap<Long, Object>[] componentMaps;
    private Object[] cache;
    long architype;
    private Tuple t1;
    private Entity<Tuple> entity = new Entity<>();
    private ECS ecs;
    private ArrayList<Iterator<Long>> iter = new ArrayList<>();

    protected Query(Tuple t1, Class<?>[] components){
        this.components = components;
        this.componentMaps = new HashMap[components.length];
        this.cache = new Object[components.length];
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
            for(int i = 0; i < cache.length; i ++){
                this.cache[i] = this.componentMaps[i].get(entity);
            }
            this.entity.tuple.set(this.cache);
            this.entity.entity = entity;
            return this.entity;
        }
    }

    public static class Entity<Tuple> {
        public long entity;
        public Tuple tuple;
    }

    @Override
    public Query<Tuple> clone(){
        Query<Tuple> clone = null;
        try{
            clone = (Query<Tuple>) super.clone();
        }catch (Exception ignore){}

        clone.entity = new Entity<>();
        clone.entity.tuple = (Tuple) this.entity.tuple.copy();
        clone.cache = new Object[clone.cache.length];
        clone.iter = new ArrayList<>();
        clone.reset();
        return clone;
    }
}


