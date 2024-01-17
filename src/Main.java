import ecs.ECS;
import ecs.Query;
import ecs.Time;
import ecs.util.TypeReflection;
import ecs.util.tupple.Tuple1;
import ecs.util.tupple.Tuple2;
import ecs.util.tupple.Tuple3;
import ecs.util.tupple.Tuple4;
import io.Display;
import io.Input;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        var ecs_ = new ECS();
        ecs_.addResource(new Debug());

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

            query.reset();
            Query<Tuple3<Player, Position, Velocity>>.Entity cube;
            while((cube = query.next()) != null){
                cube.tuple.t2.x += time.deltaS() * speedX;
                cube.tuple.t2.y += time.deltaS() * speedY;

                cube.tuple.t3.x = speedX;
                cube.tuple.t3.y = speedY;
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2) -> {}, (Time time, Query<Tuple2<Player, Drawable>> query) -> {
            Query<Tuple2<Player, Drawable>>.Entity player;
            query.reset();
            while((player = query.next()) != null){
                player.tuple.t2.color = Color.getHSBColor((float)(time.currentS() % 360) ,1.0f, 1.0f);
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2, _3) -> {}, (Display display, Time time, Query<Tuple3<Position, Velocity, HitBox>> query) -> {
            Query<Tuple3<Position, Velocity, HitBox>>.Entity physics;

            int steps = 1;
            double bounce = 0.714;
            double delta = time.deltaS() / steps;

            double pm = 0.7;
            double pv = 0.90;

            for(int i = 0; i < steps; i ++){


                query.reset();
                var pair = query.nextPair();
                // yes this is sorta cursed but whatever
                // the tuples keep the same reference so we don't need to go through so many different steps
                // to get to our data
                var obj1 = pair==null?null:pair.t1.tuple;
                var obj2 = pair==null?null:pair.t2.tuple;
                while(pair != null) {

                    double difX = obj1.t1.x - obj2.t1.x;
                    double difY = obj1.t1.y - obj2.t1.y;
                    double dist = Math.sqrt(difX * difX + difY * difY);
                    double min = (obj1.t3.width + obj2.t3.width) / 2;
                    if (dist < min && dist != 0) {
                        var nx = difX / dist;
                        var ny = difY / dist;
                        var dx = min - dist;
                        var dy = min - dist;


                        var mx = 0.5 * dx * nx;
                        var my = 0.5 * dy * ny;
                        obj1.t1.x += mx * pm;
                        obj1.t1.y += my * pm;
                        obj2.t1.x -= mx * pm;
                        obj2.t1.y -= my * pm;

                        obj1.t2.x += mx / delta * pv;
                        obj1.t2.y += my / delta * pv;
                        obj2.t2.x -= mx / delta * pv;
                        obj2.t2.y -= my / delta * pv;
                    }
                    pair = query.nextPair();
                }


                query.reset();
                double width = display.getWidth();
                double height = display.getHeight() - 35;
                while((physics = query.next()) != null){

                    // apply velocity
                    physics.tuple.t1.x += physics.tuple.t2.x * delta;
                    physics.tuple.t1.y += physics.tuple.t2.y * delta;

                    // bounds checking
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

                    // acceleration
                    physics.tuple.t2.y += 100 * 9.81 * delta;
                }
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2, _3) -> {}, (Display display, Query<Tuple3<Position, Drawable, HitBox>> query) -> {
            var g = display.getGraphics();
            query.reset();
            var cube = query.next();
            while(cube != null){
                g.setColor(cube.tuple.t2.color);
                switch (cube.tuple.t3.shape){
                    case 1:
                        g.fillRect((int)(cube.tuple.t1.x - cube.tuple.t3.width/2) , (int)(cube.tuple.t1.y - cube.tuple.t3.height/2), (int)cube.tuple.t3.width, (int)cube.tuple.t3.height);
                        break;
                    case 2:
                        g.fillOval((int)(cube.tuple.t1.x - cube.tuple.t3.width/2) , (int)(cube.tuple.t1.y - cube.tuple.t3.height/2), (int)cube.tuple.t3.width, (int)cube.tuple.t3.height);
                        break;
                }
                cube = query.next();
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1, _2) -> {}, (ECS ecs__, Input input, Time time, Query<Tuple2<Position, HitBox>> query) -> {
            if (input.mouseHeld(Input.MouseKey.Left) && (time.tick() & 2) == 0){
                var drawable = new Drawable(Color.getHSBColor((float)(time.currentS() % 360) ,1.0f, 1.0f));
                var random = Math.random() * 25 + 45;
                ecs__.addEntity(new Position(input.getMouseX(), input.getMouseY()), drawable, new HitBox(random, random, 2), new Velocity());
            }
            if(!input.mouseHeld(Input.MouseKey.Right)){
                return;
            }
            query.exclude(Player.class);
            query.reset();
            var thing = query.next();
            while(thing != null){
                var dx = thing.tuple.t1.x - input.getMouseX();
                var dy = thing.tuple.t1.y - input.getMouseY();
                var ds = dx * dx + dy * dy;
                var rs = thing.tuple.t2.width * thing.tuple.t2.width / 4;
                if(ds <= rs){
                    thing.remove();
                }

                thing = query.next();
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((Input input, Debug debug) -> {
            if(input.keyPressed(' '))
                debug.enabled ^= true;
        }));

        ecs_.addSystem(ecs.System.makeSystem((_1) -> {}, (Display display, Debug debug, Query<Tuple1<Position>> query) -> {
            if(!debug.enabled)
                return;

            var g = display.getGraphics();
            g.setColor(Color.WHITE);

            query.reset();
            query.include(Drawable.class);
            var de = query.next();
            while(de != null){
                g.drawString(""+de.entity, (int)de.tuple.t1.x-10, (int)de.tuple.t1.y+5);
                de = query.next();
            }
        }));

        ecs_.addSystem(ecs.System.makeSystem((ECS ecs__, Display display, Input input, Time time, Debug debug) -> {
            if(!debug.enabled)
                return;
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

class Debug{
    boolean enabled = true;
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