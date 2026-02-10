package fileHandler;

import java.awt.Frame;
import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import gui.DialogMessages;
import model.Airport;
import model.Flight;

public class FileHandler {

    private Frame parent;

    public FileHandler(Frame parent) {
        this.parent = parent;
    }

    // --- Load airports from CSV file ---
    public List<Airport> loadAirports(String filename) {
        List<Airport> airports = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Skip header if present
                if (firstLine && line.toLowerCase().contains("name")) {
                    firstLine = false;
                    continue;
                }
                firstLine = false;

                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                String name = parts[0].trim();
                String id = parts[1].trim();
                int x, y;

                try {
                    x = Integer.parseInt(parts[2].trim());
                    y = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    new DialogMessages(parent, "Error", "Coordinates must be numbers!", true);
                    continue;
                }

                airports.add(new Airport(name, id, x, y));
            }

        } catch (IOException e) {
            new DialogMessages(parent, "Error", "Problem while reading airports CSV file!", true);
        }

        return airports;
    }

    // --- Load flights from CSV file ---
    public List<Flight> loadFlights(String filename, List<Airport> airports) {
        List<Flight> flights = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Skip header if present
                if (firstLine && line.toLowerCase().contains("departureid")) {
                    firstLine = false;
                    continue;
                }
                firstLine = false;

                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                String depId = parts[0].trim();
                String arrId = parts[1].trim();
                String depTimeStr = parts[2].trim();
                int duration;

                // Parse duration safely
                try {
                    duration = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    new DialogMessages(parent, "Error", "Duration must be a number!", true);
                    continue;
                }

                // Find departure and arrival airports by ID
                Airport dep = findAirportById(airports, depId);
                Airport arr = findAirportById(airports, arrId);
                if (dep == null || arr == null) {
                    new DialogMessages(parent, "Error", "Airport with given ID not found!", true);
                    continue;
                }

                // Parse time and create Flight object
                try {
                    LocalTime depTime = LocalTime.parse(depTimeStr);
                    flights.add(new Flight(dep, arr, depTime, duration));
                } catch (Exception e) {
                    new DialogMessages(parent, "Error", "Invalid time format!", true);
                }
            }

        } catch (IOException e) {
            new DialogMessages(parent, "Error", "Problem while reading flights CSV file!", true);
        }

        return flights;
    }

    // --- Save airports to CSV ---
    public void saveAirports(List<Airport> airports, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("Name,ID,X,Y");
            bw.newLine();

            // Write one airport per line
            for (Airport a : airports) {
                bw.write(a.getName() + "," + a.getId() + "," + a.getX() + "," + a.getY());
                bw.newLine();
            }

        } catch (IOException e) {
            new DialogMessages(parent, "Error", "Problem while saving airports CSV file!", true);
        }
    }

    // --- Save flights to CSV ---
    public void saveFlights(List<Flight> flights, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("DepartureID,ArrivalID,DepartureTime,Duration");
            bw.newLine();

            // Write one flight per line
            for (Flight f : flights) {
                bw.write(f.getDepartureAirport().getId() + "," +
                         f.getArrivalAirport().getId() + "," +
                         f.getDepartureTime().toString() + "," +
                         f.getDuration());
                bw.newLine();
            }

        } catch (IOException e) {
            new DialogMessages(parent, "Error", "Problem while saving flights CSV file!", true);
        }
    }

    // --- Helper method for finding airport by ID ---
    private Airport findAirportById(List<Airport> airports, String id) {
        for (Airport a : airports) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }
}


