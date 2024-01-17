package ecs;

import ecs.util.tupple.DynTuple;
import ecs.util.tupple.Tuple2;
import ecs.util.tupple.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Query<Tuple extends DynTuple> implements Cloneable{

    private Class<?>[] components;
    private HashMap<Long, Object>[] componentMaps;
    private Object[] componentCache;

    long excludeArchetype = 0;
    long tupleArchetype;
    long optionalArchetype = 0;
    long includeArchetype = 0;

    private Tuple t1;
    private Tuple t2;
    private Entity e1 = new Entity();
    private Entity e2 = new Entity();
    private ECS ecs;
    private ArrayList<Iterator<Long>> iter = new ArrayList<>();
    private Tuple2<Entity, Entity> pair = new Tuple2<>(this.e1, this.e2);
    private int iterLen;
    private long[] entityCache;

    private int i;
    private int j;

    protected Query(Tuple t1, Class<?>[] components){
        assert t1.get().length == components.length;
        this.components = components;
        this.componentMaps = new HashMap[components.length];
        this.componentCache = new Object[components.length];

        this.t1 = t1;
        this.t2 = (Tuple) t1.copy();
        this.e1.tuple = this.t1;
        this.e2.tuple = this.t2;
    }

    protected void setup(ECS ecs){
        this.ecs = ecs;
        this.tupleArchetype = ecs.getArchitype(this.components);
        for(int i = 0; i < this.componentMaps.length; i ++){
            this.componentMaps[i] = ecs.componentMap.get(this.components[i].toString());
        }
    }

    public void reset(){
        iter.clear();

        this.i = 0;
        this.j = 0;
        this.iterLen = 0;

        for(var v : this.ecs.entityArchetypes.entrySet()){
            long needed = (this.tupleArchetype | this.includeArchetype) & ~this.optionalArchetype;
            if ((needed & v.getKey()) == needed && (this.excludeArchetype & v.getKey()) == 0){
                iter.add(v.getValue().iterator());
                this.iterLen += v.getValue().size();
            }
        }
    }

    public void exclude(Class<?>... components){
        this.excludeArchetype = ecs.getArchitype(components);
    }

    public void include(Class<?>... components){
        this.includeArchetype = ecs.getArchitype(components);
    }

    public void optional(Class<?>... components){
        this.optionalArchetype = ecs.getArchitype(components);
    }

    private long nextId(){
        var top = iter.get(iter.size() - 1);
        var entity = top.next();
        while(!top.hasNext()){
            iter.remove(iter.size() - 1);
            if (iter.size() == 0){
                break;
            }
            top = iter.get(iter.size() - 1);
        }
        return entity;
    }

    public Entity next(){
        if (iter.size() == 0){
            return null;
        }else{
            this.e1.query(nextId());
            return this.e1;
        }
    }

    public Tuple2<Entity, Entity> nextPair(){
        if(i == 0 && j == 0){
            if(this.entityCache == null || this.entityCache.length <= this.iterLen){
                this.entityCache = new long[this.iterLen + 20];
            }
            if(this.iterLen > 1){
                this.entityCache[0] = this.nextId();
                j++;
            }else{
                return null;
            }
        }

        if(j >= this.iterLen){
            if(i >= this.iterLen - 1){
                return null;
            }
            i ++;
            j = i+1;
        }
        if(i == 0){
            this.entityCache[j] = this.nextId();
        }
        this.e1.query(this.entityCache[i]);
        this.e2.query(this.entityCache[j]);
        j++;
        return this.pair;
    }

    public Entity entity(){
        return new Entity((Tuple) this.t1.copy());
    }

    public class Entity {
        public long entity;
        public Tuple tuple;

        private Entity(){}
        private Entity(Tuple t){
            this.tuple = t;
        }

        public void query(long entity){
            this.entity = entity;
            if(tuple instanceof Tuple3 tuple){
                tuple.t1 = componentMaps[0].get(entity);
                tuple.t2 = componentMaps[1].get(entity);
                tuple.t3 = componentMaps[2].get(entity);
            }else{
                for(int i = 0; i < componentCache.length; i ++){
                    componentCache[i] = componentMaps[i].get(entity);
                }
                this.tuple.set(componentCache);
            }
        }

        public void remove() {
            ecs.remove(this.entity);
        }
    }

    @Override
    public Query<Tuple> clone(){
        Query<Tuple> clone = null;
        try{
            clone = (Query<Tuple>) super.clone();
        }catch (Exception ignore){}

        clone.e1 = new Entity();
        clone.e1.tuple = (Tuple) this.e1.tuple.copy();
        clone.componentCache = new Object[clone.componentCache.length];
        clone.iter = new ArrayList<>();
        clone.reset();
        return clone;
    }
}


