import ecs.ECS;
import ecs.Time;
import ecs.util.tupple.Tuple2;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {

        var ecs_ = new ECS();
        ecs_.addSystem(ecs.System.makeSystem((Time time) -> {
            System.out.println("process: " + time.process() + "ns");
            System.out.println("delta: " + time.deltaS() + "S");
        }));

        ecs_.run();
    }
}