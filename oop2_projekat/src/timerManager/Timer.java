package timerManager;

public class Timer extends Thread {

    private double s; // seconds
    private int m;    // minutes
    private int step; // sleep interval in milliseconds
    private boolean work; // flag to control if timer is running
    private TimerListener listener; // listener to notify each step

    public Timer(int step) {
        this.step = step;
    }

    //Set the listener that will be called on each timer step
    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {

                // wait until timer is started
                synchronized (this) {
                    while (!work) {
                        wait();
                    }
                }

                sleep(step);
                s += (double) step / 1000; // increment seconds

                // if 60 seconds passed, increment minutes
                if (s % 60 == 0) {
                    m++;
                    s = 0;
                }

                // notify listener about step
                if (listener != null) {
                    listener.onStep(s);
                }
            }
        } catch (InterruptedException e) {}
    }

    // Start/resume the timer
    public synchronized void go() {
        work = true;
        notify();
    }

    // Pause the timer 
    public synchronized void pause() {
        work = false;
    }

    // Reset timer to zero
    public synchronized void reset() {
        s = 0;
        m = 0;
    }

    // Get current seconds
    public double getS() {
        return s;
    }

    //Get current minutes
    public int getM() {
        return m;
    }
}