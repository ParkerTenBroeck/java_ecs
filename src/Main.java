import ecs.ECS;
import ecs.Time;
import ecs.util.tupple.Tuple2;
import io.Display;
import io.Input;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {

        var ecs_ = new ECS();
        ecs_.resources.addResource(new Cube());
        ecs_.resources.addResource(new ArrayList<Cube>());

        ecs_.addSystem(ecs.System.makeSystem((Display display, Input input, Time time, ArrayList<Cube> cubes) -> {
            var g = display.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0,0,display.getWidth(), display.getHeight());

            g.setColor(Color.WHITE);
            g.drawString("delta: " + time.deltaS() + "S", 0,10);
            g.drawString("updateTime: " + time.process() + "ns", 0,25);
            g.drawString("timeLeft: " + time.usedTimePercent() + "%", 0,40);
            g.drawString("tick: " + time.tick(), 0,55);
            g.drawString("cubes: " + cubes.size(), 0,70);
            g.drawString("MouseX: " + input.getMouseX(), 0,90);
            g.drawString("MouseY: " + input.getMouseY(), 0,105);
        }));

        ecs_.addSystem(ecs.System.makeSystem((Cube cube, ArrayList<Cube> cubes, Input input, Time time) -> {
            double speed = 100.0;
            var speedX = 0.0;
            if (input.keyHeld('a')){
                speedX -= speed;
            }else if(input.keyHeld('d')){
                speedX += speed;
            }

            var speedY = 0.0;
            if (input.keyHeld('w')){
                speedY -= speed;
            }else if(input.keyHeld('s')){
                speedY += speed;
            }

            cube.x += time.deltaS() * speedX;
            cube.y += time.deltaS() * speedY;

            for(Cube cuben : cubes){
                cuben.x += time.deltaS() * speedX;
                cuben.y += time.deltaS() * speedY;
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((Cube cube, Display display) -> {
            var g = display.getGraphics();
            g.setColor(Color.RED);
            g.fillRect((int)cube.x, (int)cube.y, 10, 10);
        }));

        ecs_.addSystem(ecs.System.makeSystem((ArrayList<Cube> cubes, Display display) -> {
            var g = display.getGraphics();
            for(Cube cube : cubes){
                g.setColor(Color.RED);
                g.fillRect((int)cube.x, (int)cube.y, 10, 10);
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((ArrayList<Cube> cubes, Display display, Input input) -> {
            if (input.mouseHeld(Input.MouseKey.Left)){
                cubes.add(new Cube(input.getMouseX(), input.getMouseY()));
            }
        }));

        ecs_.run();
    }
}

class Cube{
    double x = 50;
    double y = 50;

    public Cube(){

    }

    public Cube(double x, double y) {
        this.x = x;
        this.y = y;
    }
}