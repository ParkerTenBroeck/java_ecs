package ecs;

public class Time {
    private long last = 0;
    private long current = 0;
    private long delta = 0;
    private long process = 0;
    private double lastS = 0;
    private double currentS = 0;
    private double deltaS = 0;
    private double processS = 0;
    private long tick = 0;
    private double usedTimePercent = 0.0;

    public long last(){
        return this.last;
    }

    public long current(){
        return this.current;
    }

    public long delta(){
        return this.delta;
    }

    public long process(){
        return this.process;
    }

    public double lastS(){
        return this.lastS;
    }

    public double currentS(){
        return this.currentS;
    }

    public double deltaS(){
        return this.deltaS;
    }

    public double processS(){
        return this.processS;
    }


    public long tick(){
        return this.tick;
    }
    public double usedTimePercent(){
        return usedTimePercent;
    }

    protected void update(){
        this.tick ++;
        this.last = this.current;
        this.current = java.lang.System.nanoTime();
        this.delta = current - last;

        this.lastS = this.last * 1e-9;
        this.currentS = this.current * 1e-9;
        this.deltaS = this.delta * 1e-9;
    }

    public void updateProcess(long target) {
        this.process = java.lang.System.nanoTime() - this.current;
        this.processS = this.processS * 1e-9;
        this.usedTimePercent = ((double)process)/((double)target);
    }
}
