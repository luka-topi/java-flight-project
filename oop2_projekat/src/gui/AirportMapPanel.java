package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import manage.AirportManager;
import model.Airport;
import model.Flight;
import timerManager.Timer;

public class AirportMapPanel extends Canvas {

    private Frame parent;
    private int rows = 60;
    private int step;
    private List<Airport> airports = new ArrayList<>();
    private List<Airport> selectedAirports = new ArrayList<>();
    private List<Flight> activeFlights = new ArrayList<>();
    private boolean blinkState = false;
    private Timer blinkTimer;
    private int currentSimulatedMinutes = 0;

    public AirportMapPanel(Frame parent) {
        this.parent = parent;
    }

    public void setAirports(List<Airport> airports) {
        this.airports = airports;
        repaint();
    }

    public void setSelectedAirports(List<Airport> selectedAirports) {
        this.selectedAirports = selectedAirports;
        setSelectedAirportsBlinking(true);
    }

    public void addDepartingFlight(Flight flight) {
        activeFlights.add(flight);
    }
    
    public void clearDepartingFlights() {
        activeFlights.clear();
        repaint();
    }

    public void setCurrentSimulatedMinutes(int minutes) {
        this.currentSimulatedMinutes = minutes;
        repaint();
    }

    public int getRows() {
        return rows;
    }

    public int getStep() {
        return step;
    }

    // Calculates pixel size of one grid cell
    public void updateStep() {
        int dim = getDim();
        step = dim / rows;
    }

    @Override
    public void paint(Graphics g) {
        updateStep();

        drawLines(g); // draw base map grid

        for (Airport a : airports) {
            drawAirport(g, a);
        }

        for (Airport a : selectedAirports) {
            selectAirport(g, a);
        }

        // Draw moving planes on the map
        for (Flight f : activeFlights) {
            drawPlane(g, f);
        }
    }

    private void drawLines(Graphics g) {
        int dim = getDim();
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, 0, dim);
        g.drawLine(0, 0, dim, 0);
        g.drawLine(0, dim / 2, dim, dim / 2);
        g.drawLine(dim / 2, 0, dim / 2, dim);
        g.drawLine(dim, dim, dim, 0);
        g.drawLine(dim, dim, 0, dim);
    }

    // Draws one airport square and its label
    public void drawAirport(Graphics g, Airport a) {
        int dim = getDim();
        int step = dim / rows;

        g.setColor(Color.GRAY);

        int x = (a.getX() + 90);
        int y = (90 - a.getY());
        int px = x * dim / 180;
        int py = y * dim / 180;

        g.fillRect(px, py, step, step);

        g.setColor(Color.BLACK);
        g.drawString(a.getId(), px + step + 2, py + step / 2);
    }

    // Returns the square map dimension
    int getDim() {
        int width = getWidth();
        int height = getHeight();
        int min = Math.min(width, height);
        return (min / rows) * rows;
    }

    // Highlights selected airport (blinking effect)
    private void selectAirport(Graphics g, Airport a) {
        int dim = getDim();
        int x = (a.getX() + 90);
        int y = (90 - a.getY());
        int px = x * dim / 180;
        int py = y * dim / 180;

        g.setColor(blinkState ? Color.RED : Color.GRAY);
        g.fillRect(px, py, step, step);
    }

    // Starts or stops blinking animation for selected airports
    public void setSelectedAirportsBlinking(boolean enable) {
        if (enable) {
            if (blinkTimer == null) {
                blinkTimer = new Timer(500);
                blinkTimer.setListener(secondsPassed -> {
                    blinkState = !blinkState;
                    repaint();
                });
                blinkTimer.start();
                blinkTimer.go();
            }
        } else {
            if (blinkTimer != null) {
                blinkTimer.interrupt();
                blinkTimer = null;
                blinkState = false;
                repaint();
            }
        }
    }

    // Draws the plane at its current position between airports
    private void drawPlane(Graphics g, Flight f) {
        int dim = getDim();
        int x1
        
        
        
        
        
        
        
        
        
        
        
        
        = (f.getDepartureAirport().getX() + 90) * dim / 180;
        int y1 = (90 - f.getDepartureAirport().getY()) * dim / 180;
        int x2 = (f.getArrivalAirport().getX() + 90) * dim / 180;
        int y2 = (90 - f.getArrivalAirport().getY()) * dim / 180;

        int start = f.getDepartureTime().getHour() * 60 + f.getDepartureTime().getMinute();
        int end = start + f.getDuration();

        // Skip drawing if flight hasn't started or already ended
        if (currentSimulatedMinutes < start || currentSimulatedMinutes > end) return;

        // Skip if airports are missing on map
        if (AirportManager.findAirport(airports, f.getDepartureAirport().getId()) == null ||
            AirportManager.findAirport(airports, f.getArrivalAirport().getId()) == null)
            return;

        // Calculate plane position between departure and arrival
        double lambda = (double)(currentSimulatedMinutes - start) / f.getDuration();
        lambda = Math.max(0, Math.min(1, lambda));

        int px = (int)(x1 + lambda * (x2 - x1));
        int py = (int)(y1 + lambda * (y2 - y1));

        g.setColor(Color.BLUE);
        g.fillOval(px, py, step, step);
    }
}
