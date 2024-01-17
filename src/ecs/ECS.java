package ecs;

import io.Display;
import io.Input;

import java.util.ArrayList;
import java.util.HashMap;

public final class ECS {

    private long entityId = 1;
    protected final HashMap<Long, ArrayList<Long>> entityArchetypes = new HashMap<>();
    protected final HashMap<String, HashMap<Long, Object>> componentMap = new HashMap<>();
    private final ArrayList<String> activeComponents = new ArrayList<>();
    private final ArrayList<Long> toRemove = new ArrayList<>();

    public final ResourceManager resources = new ResourceManager();
    ArrayList<System> systems = new ArrayList<>();

    private final Time time = new Time();
    private final Input input = new Input();
    private final Display display = new Display(this.input);
    private long targetFrameTime = 16666666;
    private boolean stop;

    public ECS(){
        resources.addResource(time);
        resources.addResource(input);
        resources.addResource(display);
        resources.addResource(this);
    }

    public <T> void addResource(T resource){
        this.resources.addResource(resource);
    }

    public void addSystem(System system){
        this.systems.add(system);
    }

    protected long getArchitype(Class<?>... components){
        long archetype = 0;
        for(int i = 0; i < components.length; i ++) {
            var name = components[i].toString();
            var index = activeComponents.indexOf(name);
            if (index >= 0) {
                archetype |= 1l << index;
            } else {
                archetype |= 1l << activeComponents.size();
                activeComponents.add(name);
            }
        }
        return archetype;
    }

    public long addEntity(Object... components){
        long archetype = 0;
        long entityId = this.entityId++;
        for(int i = 0; i < components.length; i ++){
            var name = components[i].getClass().toString();
            var index = activeComponents.indexOf(name);
            if (index >= 0){
                archetype |= 1l << index;
            }else{
                archetype |= 1l << activeComponents.size();
                activeComponents.add(name);
            }
            var componentEntityMap = componentMap.get(name);
            if(componentEntityMap == null){
                componentEntityMap = new HashMap<>();
                componentMap.put(name, componentEntityMap);
            }
            componentEntityMap.put(entityId, components[i]);
        }
        var entitys = entityArchetypes.get(archetype);
        if(entitys == null){
            entitys = new ArrayList<>();
            entityArchetypes.put(archetype, entitys);
        }
        entitys.add(entityId);
        return entityId;
    }

    public void run(){
        time.update();
        try{Thread.sleep(this.targetFrameTime / 1000000);} catch (Exception e){}
        while(!stop){
            time.update();
            for(System system : systems){
                system.runner.accept(this);
            }

            for(int i = 0; i < toRemove.size(); i ++){
                removeFully(toRemove.get(i));
            }
            toRemove.clear();

            display.update();
            input.update();

            time.updateProcess(targetFrameTime);
            long sleep = this.targetFrameTime - time.process();
            sleep = sleep<10000?10000:sleep;
            try{Thread.sleep(sleep / 1000000, (int)(sleep % 1000000));} catch (Exception e){}
        }
    }


    public int countEntities() {
        int count = 0;
        for(var v : this.entityArchetypes.values()){
            count += v.size();
        }
        return count;
    }

    public void remove(long entity){
        this.toRemove.add(entity);
    }

    private void removeFully(long entity) {
        long archetype = 0;
        for(var entry : this.componentMap.entrySet()){
            var removed = entry.getValue().remove(entity);
            if(removed != null){
                archetype |= 1 << activeComponents.indexOf(entry.getKey());
            }
        }

        this.entityArchetypes.get(archetype).remove(entity);
    }
}
