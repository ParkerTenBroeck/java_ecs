import ecs.ECS;
import ecs.Query;
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

        ecs_.addEntity(new Position(), new Drawable(), new Player());

        ecs_.addSystem(ecs.System.makeSystem((Display display) -> {
            var g = display.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0,0,display.getWidth(), display.getHeight());
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2) -> {}, (Input input, Time time, Query<Tuple2<Player, Position>> query) -> {
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

            Query.Entity<Tuple2<Player, Position>> cube;
            while((cube = query.next()) != null){
                cube.tuple.t2.x += time.deltaS() * speedX;
                cube.tuple.t2.y += time.deltaS() * speedY;
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2) -> {}, (Display display, Query<Tuple2<Position, Drawable>> query) -> {
            var g = display.getGraphics();
            Query.Entity<Tuple2<Position, Drawable>> cube;
            while((cube = query.next()) != null){
                g.setColor(cube.tuple.t2.color);
                g.fillRect((int)cube.tuple.t1.x , (int)cube.tuple.t1.y, 10, 10);
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((ECS ecs__, Input input, Time time) -> {
            if (input.mouseHeld(Input.MouseKey.Left)){
                var drawable = new Drawable(Color.getHSBColor((float)(time.currentS() % 360) ,1.0f, 1.0f));
                ecs__.addEntity(new Position(input.getMouseX(), input.getMouseY()), drawable);
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((Display display, Input input, Time time) -> {
            var g = display.getGraphics();

            g.setColor(Color.WHITE);
            g.drawString("delta: " + time.deltaS() + "S", 0,10);
            g.drawString("updateTime: " + time.process() + "ns", 0,25);
            g.drawString("timeLeft: " + time.usedTimePercent() + "%", 0,40);
            g.drawString("tick: " + time.tick(), 0,55);
//            g.drawString("cubes: " + cubes.size(), 0,70);
            g.drawString("MouseX: " + input.getMouseX(), 0,90);
            g.drawString("MouseY: " + input.getMouseY(), 0,105);
        }));

        ecs_.run();
    }
}

class Position{
    double x = 50;
    double y = 50;

    public Position(){}

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Drawable{
    Color color = Color.green;
    public Drawable(){}
    public Drawable(Color color) {
        this.color = color;
    }
}

class Player{}