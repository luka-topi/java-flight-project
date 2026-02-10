package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import model.Airport;
import model.Flight;
import simulation.Scheduler;
import timerManager.Timer;

public class MainWindow extends Frame {

    List<Airport> airportList = new ArrayList<>();
    List<Airport> selectedAirportsList = new ArrayList<>();
    List<Flight> flightList = new ArrayList<>();

    WestPanel westPanel;
    AirportListPanel airportListPanel;
    TableViewPanel tablePanel;
    AirportMapPanel mapPanel;
    NorthPanel northPanel;
    
    
    private Scheduler scheduler;
    private Timer simulationTimer;
    private int simulatedMinutes = 0;

    public MainWindow() {
        setTitle("Air Traffic Simulator");
        setSize(1200, 800);
        setResizable(false);
        setLayout(new BorderLayout());
        
        // Timer dialog that handles inactivity closing
        WindowClosingTimerDialog dt = new WindowClosingTimerDialog(this);
        dt.start();

        simulationTimer = new Timer(200);
        Color cream = new Color(255, 253, 208);

        scheduler = new Scheduler();

        // Initialize GUI panels
        tablePanel = new TableViewPanel();
        airportListPanel = new AirportListPanel(this);
        mapPanel = new AirportMapPanel(this);
        mapPanel.setBackground(cream);
        westPanel = new WestPanel(this, scheduler, dt);

        // Add panels to the main window
        add(westPanel, BorderLayout.WEST);
        add(airportListPanel, BorderLayout.EAST);
        add(tablePanel.getPanel(), BorderLayout.SOUTH);
        add(mapPanel, BorderLayout.CENTER);
        mapPanel.updateStep();
        
        simulationTimer = new Timer(200);
        
        northPanel = new NorthPanel(simulationTimer, scheduler, mapPanel, this, dt);
        add(northPanel, BorderLayout.NORTH);
        
        // select/deselect airports
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                dt.resetTimer();

                // Iterate through all airports and check if one was clicked
                for (Airport a : airportList) {
                    int dim = mapPanel.getDim();
                    int px = (a.getX() + 90) * dim / 180;
                    int py = (90 - a.getY()) * dim / 180;
                    int step = mapPanel.getStep();

                    // Check if mouse coordinates are within airport bounds
                    if (mx >= px && mx <= px + step && my >= py && my <= py + step) {
                        // Add or remove from selected list depending on current state
                        if (!selectedAirportsList.contains(a)) {
                            dt.pauseTimer();
                            selectedAirportsList.add(a);
                        } else {
                            selectedAirportsList.remove(a);
                            if (selectedAirportsList.isEmpty())
                                dt.resumeTimer();
                        }

                        // Update blinking effect for selected airports
                        mapPanel.setSelectedAirports(selectedAirportsList);
                        break;
                    }
                }
            }
        });
        
        //Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }
    
    // Converts total simulated minutes into LocalTime format
    private LocalTime toLocalTime(int minutes) {
        return LocalTime.of(minutes / 60, minutes % 60);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}

