import ecs.ECS;
import ecs.Query;
import ecs.Time;
import ecs.util.tupple.Tuple2;
import ecs.util.tupple.Tuple3;
import io.Display;
import io.Input;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        var ecs_ = new ECS();

        ecs_.addEntity(new Position(), new Drawable(), new Player(), new HitBox(30, 30, 1), new Velocity());

        ecs_.addSystem(ecs.System.makeSystem((Display display) -> {
            var g = display.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0,0,display.getWidth(), display.getHeight());
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2, _3) -> {}, (Input input, Time time, Query<Tuple3<Player, Position, Velocity>> query) -> {
            double speed = 200.0;
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

            Query.Entity<Tuple3<Player, Position, Velocity>> cube;
            while((cube = query.next()) != null){
                cube.tuple.t2.x += time.deltaS() * speedX;
                cube.tuple.t2.y += time.deltaS() * speedY;

                cube.tuple.t3.x = speedX;
                cube.tuple.t3.y = speedY;
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2) -> {}, (Time time, Query<Tuple2<Player, Drawable>> query) -> {
            Query.Entity<Tuple2<Player, Drawable>> player;
            while((player = query.next()) != null){
                player.tuple.t2.color = Color.getHSBColor((float)(time.currentS() % 360) ,1.0f, 1.0f);
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2, _3) -> {}, (Display display, Time time, Query<Tuple3<Position, Velocity, HitBox>> query) -> {
            Query.Entity<Tuple3<Position, Velocity, HitBox>> physics;

            int steps = 2;
            double bounce = 0.714;
            double delta = time.deltaS() / steps;

            double pm = 0.35;
            double pv = 1-pm;

            for(int i = 0; i < steps; i ++){

                var query2 = query.clone();
                Query.Entity<Tuple3<Position, Velocity, HitBox>> physics2;
                while((physics = query.next()) != null){
                    query2.reset();
                    while((physics2 = query2.next()) != null) {
                        if (physics2.entity == physics.entity){
                            continue;
                        }

                        double difX = physics.tuple.t1.x - physics2.tuple.t1.x;
                        double difY = physics.tuple.t1.y - physics2.tuple.t1.y;
                        double dist = Math.sqrt(difX * difX + difY * difY);
                        double min = (physics.tuple.t3.width + physics2.tuple.t3.width) / 2;
                        if (dist < min && dist != 0){
                            var nx = difX / dist;
                            var ny = difY / dist;
                            var dx = min - dist;
                            var dy = min - dist;


                            var mx = 0.5 * dx * nx;
                            var my = 0.5 * dy * ny;
                            physics.tuple.t1.x += mx * pm;
                            physics.tuple.t1.y += my * pm;
                            physics2.tuple.t1.x -= mx * pm;
                            physics2.tuple.t1.y -= my * pm;

                            physics.tuple.t2.x += mx / delta * pv;
                            physics.tuple.t2.y += my / delta * pv;
                            physics2.tuple.t2.x -= mx / delta * pv;
                            physics2.tuple.t2.y -= my / delta * pv;
                        }
                    }
                }

                query.reset();
                while((physics = query.next()) != null){
                    physics.tuple.t1.x += physics.tuple.t2.x * delta;
                    physics.tuple.t1.y += physics.tuple.t2.y * delta;
                }



                query.reset();
                double width = display.getWidth();
                double height = display.getHeight() - 35;
                while((physics = query.next()) != null){
                    var rad = physics.tuple.t3.width/2;
                    var lx = physics.tuple.t1.x - rad;
                    var rx = physics.tuple.t1.x + rad;

                    var ly = physics.tuple.t1.y - rad;
                    var ry = physics.tuple.t1.y + rad;
                    if (lx < 0){
                        physics.tuple.t1.x = rad;

                        physics.tuple.t2.x *= -bounce;
                    }else if(rx > width){
                        physics.tuple.t1.x = width - rad;

                        physics.tuple.t2.x *= -bounce;
                    }
                    if (ly < 0){
                        physics.tuple.t1.y = rad;

                        physics.tuple.t2.y *= -bounce;
                    }else if(ry > height){
                        physics.tuple.t1.y = height - rad;

                        physics.tuple.t2.y *= -bounce;
                    }
                }

                query.reset();
                while((physics = query.next()) != null){
                    physics.tuple.t2.y += 100 * 9.81 * delta;
                }
                query.reset();
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2, _3) -> {}, (Display display, Query<Tuple3<Position, Drawable, HitBox>> query) -> {
            var g = display.getGraphics();
            Query.Entity<Tuple3<Position, Drawable, HitBox>> cube;
            while((cube = query.next()) != null){
                g.setColor(cube.tuple.t2.color);
                switch (cube.tuple.t3.shape){
                    case 1:
                        g.fillRect((int)(cube.tuple.t1.x - cube.tuple.t3.width/2) , (int)(cube.tuple.t1.y - cube.tuple.t3.height/2), (int)cube.tuple.t3.width, (int)cube.tuple.t3.height);
                        break;
                    case 2:
                        g.fillOval((int)(cube.tuple.t1.x - cube.tuple.t3.width/2) , (int)(cube.tuple.t1.y - cube.tuple.t3.height/2), (int)cube.tuple.t3.width, (int)cube.tuple.t3.height);
                        break;
                }
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((ECS ecs__, Input input, Time time) -> {
            if (input.mouseHeld(Input.MouseKey.Left) && (time.tick() & 2) == 0){
                var drawable = new Drawable(Color.getHSBColor((float)(time.currentS() % 360) ,1.0f, 1.0f));
                var random = Math.random() * 20 + 25;
                ecs__.addEntity(new Position(input.getMouseX(), input.getMouseY()), drawable, new HitBox(random, random, 2), new Velocity());
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((ECS ecs__, Display display, Input input, Time time) -> {
            var g = display.getGraphics();

            g.setColor(Color.WHITE);
            g.drawString("delta: " + time.deltaS() + "S", 0,10);
            g.drawString("updateTime: " + time.process() + "ns", 0,25);
            g.drawString("timeLeft: " + time.usedTimePercent() + "%", 0,40);
            g.drawString("tick: " + time.tick(), 0,55);
            g.drawString("entities: " + ecs__.countEntities(), 0,70);
            g.drawString("MouseX: " + input.getMouseX(), 0,90);
            g.drawString("MouseY: " + input.getMouseY(), 0,105);
        }));

        ecs_.run();
    }
}

class Velocity{

    double x = 0;
    double y = 0;

    public Velocity(){}

    public Velocity(double x, double y) {
        this.x = x;
        this.y = y;
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

class HitBox {
    double width = 10;
    double height = 10;
    int shape = 1;

    public HitBox(){}
    public HitBox(int shape){
        this.shape = shape;
    }

    public HitBox(double x, double y, int shape){
        this.shape = shape;
        this.width = x;
        this.height = y;
    }
}

class Drawable{
    Color color = Color.green;
    int shape = 2;

    public Drawable(){}

    public Drawable(Color color) {
        this.color = color;
        shape = 2;
    }
}

class Player{}