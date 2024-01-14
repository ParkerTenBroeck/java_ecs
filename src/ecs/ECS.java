package ecs;

import java.util.ArrayList;

public final class ECS {

    public final ResourceManager resources = new ResourceManager();
    ArrayList<System> systems = new ArrayList<>();

    private final Time time = new Time();
    private long sleep = 33;
    private boolean stop;

    public ECS(){
        resources.addResource(time);
    }

    public void addSystem(System system){
        this.systems.add(system);
    }

    public void run(){
        time.update();
        try{Thread.sleep(this.sleep);} catch (Exception e){}
        while(!stop){
            time.update();
            for(System system : systems){
                system.runner.accept(this);
            }
            time.updateProcess();
            long sleep = this.sleep * 1000000 - time.process();
            sleep = sleep<0?0:sleep;
            try{Thread.sleep(sleep / 1000000, (int)(sleep % 1000000));} catch (Exception e){}
        }
    }


}
