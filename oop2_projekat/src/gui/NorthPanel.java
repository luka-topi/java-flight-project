package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import model.Flight;
import simulation.Scheduler;
import timerManager.Timer;

public class NorthPanel extends Panel {

    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Label timeLabel; // Label to display simulated time

    private Scheduler scheduler;
    private Timer simulationTimer;
    private AirportMapPanel mapPanel;
    private MainWindow frame;
    private WindowClosingTimerDialog dt;
    
    private List<Flight> departureFlights = new ArrayList<>();
    private int minSim = 0;
    private boolean started = false;

    public NorthPanel(Timer simulationTimer, Scheduler scheduler, AirportMapPanel mapPanel, MainWindow frame, WindowClosingTimerDialog dt) {
        this.simulationTimer = simulationTimer;
        this.scheduler = scheduler;
        this.mapPanel = mapPanel;
        this.frame = frame;
        this.dt = dt;

        // Layout: center components with horizontal and vertical spacing
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        startButton = new Button("Start");
        pauseButton = new Button("Pause");
        resetButton = new Button("Reset");
        timeLabel = new Label("Time: 00:00"); // initial displayed time

        add(startButton);
        add(pauseButton);
        add(resetButton);
        add(timeLabel);

        //Start simulation
        startButton.addActionListener(e -> {
            dt.resetTimer();
            dt.pauseTimer();

            // Start the timer thread only once
            if (!started) {
                started = true;
                simulationTimer.start();
                simulationTimer.go();
            } else {
                simulationTimer.go();
            }

            // Set listener to be triggered on every timer tick
            simulationTimer.setListener(secondsPassed -> {
                // Increase simulated time by 2 minutes every 0.2s
                minSim += 2;

                // Retrieve flights scheduled to depart at this simulated time
                List<Flight> flightsToDepart = scheduler.getFlightsToDepart(minSim);
                
                // Update current simulation time on the map
                mapPanel.setCurrentSimulatedMinutes(minSim);

                // Add new departing flights to the map visualization
                for (Flight f : flightsToDepart) {
                    mapPanel.addDepartingFlight(f);
                }

                // Convert simulated minutes to HH:MM format and update label
                int hours = minSim / 60;
                int minutes = minSim % 60;
                timeLabel.setText(String.format("Time: %02d:%02d", hours, minutes));

                // Repaint map to reflect new flight positions
                mapPanel.repaint();
            });
        });

        //Pause simulation
        pauseButton.addActionListener(e -> {
            simulationTimer.pause();
            dt.resumeTimer();
        });

        // Reset simulation
        resetButton.addActionListener(e -> {
            simulationTimer.pause();
            dt.resumeTimer();
            minSim = 0;
            mapPanel.clearDepartingFlights();
            scheduler.reset();

            // Re-register all flights to restore initial state
            for (Flight f : frame.flightList) {
                scheduler.registerFlight(f);
            }

            // Reset map and time label
            mapPanel.setCurrentSimulatedMinutes(0);
            timeLabel.setText("Time: 00:00");
        });
    }
}
