package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.Flight;

public class TableViewPanel {

    private Panel mainPanel;   // Main panel holding header and rows
    private Panel headerPanel; // Panel for column headers
    private Panel rowsPanel;   // Panel containing flight rows
    private List<Flight> flights; // List of flights displayed

    public TableViewPanel() {
        flights = new ArrayList<>();

        mainPanel = new Panel(new BorderLayout());

        //Header panel with column titles
        headerPanel = new Panel(new GridLayout(1, 4));
        headerPanel.add(new Label("Departure airport"));
        headerPanel.add(new Label("Destination airport"));
        headerPanel.add(new Label("Departure time"));
        headerPanel.add(new Label("Duration"));

        // Panel that holds each flight row
        rowsPanel = new Panel();
        rowsPanel.setLayout(new GridLayout(0, 1));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(rowsPanel, BorderLayout.CENTER);
    }

    // --- Add a flight to the table ---
    public void addFlight(Flight f) {
        flights.add(f);

        // Create a new row for this flight
        Panel row = new Panel(new GridLayout(1, 4));
        row.add(new Label(f.getDepartureAirport().getId()));
        row.add(new Label(f.getArrivalAirport().getId()));
        row.add(new Label(f.getDepartureTime().toString()));
        row.add(new Label(String.valueOf(f.getDuration())));

        rowsPanel.add(row);

        // Refresh the panel to display the new row
        rowsPanel.revalidate();  
        rowsPanel.repaint();
        mainPanel.revalidate(); 
        mainPanel.repaint();
    }

    //Clear all flights from the table
    public void clearFlights() {
        flights.clear();
        rowsPanel.removeAll();
        rowsPanel.validate();
        rowsPanel.repaint();
    }

    //Return main panel for adding to a parent container
    public Panel getPanel() {
        return mainPanel;
    }
}
