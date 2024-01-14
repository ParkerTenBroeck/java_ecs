package ecs;

import java.util.HashMap;

public final class ResourceManager {
    HashMap<String, Object> resources = new HashMap<>();

    public ResourceManager(){}

    public <T> void addResource(T resource){
        var clazz = resource.getClass();
        var clazz_name = clazz.toString();
        if (resources.containsKey(clazz_name)){
            throw new RuntimeException("Cannot insert already existing resource");
        }else{
            resources.put(clazz_name, resource);
        }
    }

    public <T> T getResource(Class<T> resource){
        return (T)resources.get(resource.toString());
    }
}
