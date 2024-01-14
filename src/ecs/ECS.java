package ecs;

import io.Display;
import io.Input;

import java.util.ArrayList;

public final class ECS {

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
    }

    public void addSystem(System system){
        this.systems.add(system);
    }

    public void run(){
        time.update();
        try{Thread.sleep(this.targetFrameTime / 1000000);} catch (Exception e){}
        while(!stop){
            time.update();
            for(System system : systems){
                system.runner.accept(this);
            }

            display.update();
            input.update();

            time.updateProcess(targetFrameTime);
            long sleep = this.targetFrameTime - time.process();
            sleep = sleep<0?0:sleep;
            try{Thread.sleep(sleep / 1000000, (int)(sleep % 1000000));} catch (Exception e){}
        }
    }


}
