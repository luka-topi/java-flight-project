package gui;

import java.awt.*;

public class WindowClosingTimerDialog extends Thread {

    private int s, m; // seconds and minutes counters
    private Frame parent; 
    private boolean warningShown = false; // flag to show inactivity warning once
    private boolean paused = false; // flag to pause the timer

    public WindowClosingTimerDialog(Frame parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {

                synchronized (this) {
                    while (paused) {
                        wait(); // waits until resumeTimer() is called
                    }
                }

                sleep(1000); 
                s++;

                // Show inactivity warning at 55 seconds
                if (s == 55 && !warningShown) {
                    warningShown = true;

                    Dialog dialog = new Dialog(parent, "Inactivity warning", true);
                    dialog.setLayout(new BorderLayout());
                    dialog.setSize(300, 150);

                    Label message = new Label("", Label.CENTER);
                    dialog.add(message, BorderLayout.CENTER);

                    Panel buttonPanel = new Panel(new FlowLayout());
                    Button yesButton = new Button("Continue");
                    Button noButton = new Button("Exit");

                    buttonPanel.add(yesButton);
                    buttonPanel.add(noButton);
                    dialog.add(buttonPanel, BorderLayout.SOUTH);

                    //Continue button resets the timer
                    yesButton.addActionListener(e -> {
                        resetTimer();
                        warningShown = false;
                        dialog.dispose();
                    });

                    // Exit button closes the program
                    noButton.addActionListener(e -> System.exit(0));

                    //Countdown thread before auto exit
                    new Thread(() -> {
                        for (int i = 5; i > 0; i--) {
                            final int secondsLeft = i;
                            message.setText("Program will close in " + secondsLeft + " seconds.");
                            try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                            if (!dialog.isVisible()) return;
                        }
                        System.exit(0);
                    }).start();

                    dialog.setVisible(true);
                }

                if (s > 59) {
                    System.exit(0);
                }

                if (s % 60 == 0) {
                    m++;
                    s = 0;
                    warningShown = false;
                }
            }
        } catch (InterruptedException e) {}
    }

    public synchronized void resetTimer() {
        m = s = 0;
        warningShown = false;
    }

    public synchronized void pauseTimer() {
        paused = true;
    }

    public synchronized void resumeTimer() {
        paused = false;
        notify();
    }
}

