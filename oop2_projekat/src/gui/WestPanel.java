package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import fileHandler.FileHandler;
import model.Airport;
import model.Flight;
import simulation.Scheduler;
import manage.AirportManager;

public class WestPanel extends Panel {

    private MainWindow mainWindow;
    private WindowClosingTimerDialog dt;
    private Scheduler scheduler;
    
    private TextField airportNameField;
    private TextField airportIdField;
    private TextField airportXField;
    private TextField airportYField;
    private Button addAirportButton;

    private Choice departureAirportChoice;
    private Choice arrivalAirportChoice;
    private TextField timeField;
    private TextField durationField;
    private Button addFlightButton;

    private Button loadCsv;
    private Button saveCsv;

    public WestPanel(MainWindow mainWindow, Scheduler scheduler, WindowClosingTimerDialog dt) {
        this.mainWindow = mainWindow;
        this.dt = dt;
        this.scheduler = scheduler;
        setLayout(new GridLayout(3, 1));

        // Create subpanels for airports, flights, and CSV actions
        add(createAirportPanel());
        add(createFlightPanel());
        add(createCsvPanel());
    }

    private Panel createAirportPanel() {
        Panel airportPanel = new Panel(new GridLayout(5, 2, 5, 5));

        airportNameField = new TextField(10);
        airportIdField = new TextField(3);
        airportXField = new TextField(5);
        airportYField = new TextField(5);
        addAirportButton = new Button("Add Airport");

        airportPanel.add(new Label("Airport name:"));
        airportPanel.add(airportNameField);
        airportPanel.add(new Label("Airport ID:"));
        airportPanel.add(airportIdField);
        airportPanel.add(new Label("X:"));
        airportPanel.add(airportXField);
        airportPanel.add(new Label("Y:"));
        airportPanel.add(airportYField);
        airportPanel.add(new Label(""));
        airportPanel.add(addAirportButton);

        addAirportButton.addActionListener(e -> addAirport());

        return airportPanel;
    }

    private Panel createFlightPanel() {
        Panel flightPanel = new Panel(new GridLayout(5, 2, 5, 5));

        departureAirportChoice = new Choice();
        arrivalAirportChoice = new Choice();
        timeField = new TextField(5);
        durationField = new TextField(5);
        addFlightButton = new Button("Add Flight");

        flightPanel.add(new Label("Departure airport:"));
        flightPanel.add(departureAirportChoice);
        flightPanel.add(new Label("Arrival airport:"));
        flightPanel.add(arrivalAirportChoice);
        flightPanel.add(new Label("Time:"));
        flightPanel.add(timeField);
        flightPanel.add(new Label("Duration:"));
        flightPanel.add(durationField);
        flightPanel.add(new Label(""));
        flightPanel.add(addFlightButton);

        addFlightButton.addActionListener(e -> addFlight());

        return flightPanel;
    }

    private Panel createCsvPanel() {
        Panel csvPanel = new Panel(new GridLayout(2, 2, 5, 5));
        loadCsv = new Button("Load CSV");
        saveCsv = new Button("Save CSV");

        csvPanel.add(new Label("Load CSV:"));
        csvPanel.add(loadCsv);
        csvPanel.add(new Label("Save CSV:"));
        csvPanel.add(saveCsv);

        loadCsv.addActionListener(e -> loadCsv());
        saveCsv.addActionListener(e -> saveCsv());

        return csvPanel;
    }

    private void addAirport() {
        try {
            dt.resetTimer();

            String name = airportNameField.getText();
            String id = airportIdField.getText();
            int x = Integer.parseInt(airportXField.getText());
            int y = Integer.parseInt(airportYField.getText());

            if (x > 90 || x < -90 || y > 90 || y < -90) {
                new DialogMessages(mainWindow, "Error", "Coordinates are out of range!", true);
                return;
            }

            if(id.length() != 3) {
                new DialogMessages(mainWindow, "Error", "Id must be 3 capital letters!", true);
                return;
            }

            Airport airport = new Airport(name, id, x, y);
            
            if(!AirportManager.checkAirportIdLetters(airport))
            {
            	new DialogMessages(mainWindow, "Error", "Id must be 3 capital letters!", true);
            	return;
            }

            if (!AirportManager.checkAirportId(mainWindow.airportList, airport)) {
                new DialogMessages(mainWindow, "Error", "Airport with that ID already exists!", true);
                return;
            }

            if (!AirportManager.checkAirportCoordinates(mainWindow.airportList, airport)) {
                new DialogMessages(mainWindow, "Error", "Airport with those coordinates already exists!", true);
                return;
            }

            mainWindow.airportList.add(airport);
            mainWindow.mapPanel.setAirports(mainWindow.airportList);
            mainWindow.airportListPanel.refreshList();
            refreshAirportChoices(); // Update choices for flight creation

            airportNameField.setText("");
            airportIdField.setText("");
            airportXField.setText("");
            airportYField.setText("");

        } catch (Exception ex) {
            new DialogMessages(mainWindow, "Error", "Coordinates must be numbers!", true);
        }
    }

    // Add a new flight
    private void addFlight() {
        try {
            dt.resetTimer();

            String departureAirport = departureAirportChoice.getSelectedItem().split(" - ")[0];
            String arrivalAirport = arrivalAirportChoice.getSelectedItem().split(" - ")[0];
            LocalTime time = LocalTime.parse(timeField.getText());
            int duration = Integer.parseInt(durationField.getText());

            Airport departure = AirportManager.findAirport(mainWindow.airportList, departureAirport);
            Airport arrival = AirportManager.findAirport(mainWindow.airportList, arrivalAirport);

            if (departure == null || arrival == null) {
                new DialogMessages(mainWindow, "Error", "Departure or arrival airport does not exist!", true);
                return;
            } else if (departure.getId().equals(arrival.getId())) {
                new DialogMessages(mainWindow, "Error", "Departure and arrival airport must be different!", true);
                return;
            }

            Flight flight = new Flight(departure, arrival, time, duration);
            mainWindow.flightList.add(flight);
            mainWindow.tablePanel.addFlight(flight);
            scheduler.registerFlight(flight);

            timeField.setText("");
            durationField.setText("");

        } catch (Exception ex) {
            new DialogMessages(mainWindow, "Error", "Invalid flight input!", true);
        }
    }

    //Load airports and flights from CSV files
    private void loadCsv() {
        dt.resetTimer();
        try {
            FileHandler f = new FileHandler(mainWindow);

            mainWindow.airportList.clear();
            mainWindow.flightList.clear();

            mainWindow.airportList.addAll(f.loadAirports("airports.csv"));
            mainWindow.flightList.addAll(f.loadFlights("flights.csv", mainWindow.airportList));

            mainWindow.airportListPanel.refreshList();
            mainWindow.tablePanel.clearFlights();

            for (Flight fl : mainWindow.flightList) {
                mainWindow.tablePanel.addFlight(fl);
                scheduler.registerFlight(fl);
            }

            mainWindow.mapPanel.setAirports(mainWindow.airportList);
            mainWindow.mapPanel.repaint();
            refreshAirportChoices();

        } catch (Exception ex) {
            new DialogMessages(mainWindow, "Error", "Problem while loading CSV!", true);
        }
    }

    //Save airports and flights to CSV files
    private void saveCsv() {
        dt.resetTimer();
        try {
            FileHandler f = new FileHandler(mainWindow);
            f.saveAirports(mainWindow.airportList, "airports.csv");
            f.saveFlights(mainWindow.flightList, "flights.csv");
        } catch (Exception ex) {
            new DialogMessages(mainWindow, "Error", "Problem while saving CSV!", true);
        }
    }

    //Refresh airport choices in departure/arrival selectors
    public void refreshAirportChoices() {
        departureAirportChoice.removeAll();
        arrivalAirportChoice.removeAll();

        for (Airport a : mainWindow.airportList) {
            String item = a.getId() + " - " + a.getName();
            departureAirportChoice.add(item);
            arrivalAirportChoice.add(item);
        }
    }
}
