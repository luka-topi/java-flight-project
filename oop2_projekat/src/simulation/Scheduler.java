package simulation;

import java.util.*;
import model.Airport;
import model.Flight;

public class Scheduler {

    private Map<Airport, Queue<Flight>> queueMap = new HashMap<>(); // stores flights for each airport in a priority queue by departure time
    private Map<Airport, Integer> lastDepartureTime = new HashMap<>(); // tracks last departure time per airport
    private static final int SLOT_MINUTES = 10; // minimum separation between departures

    // Register a flight to the scheduler
    public void registerFlight(Flight flight) {
        // if airport has no queue, create a priority queue sorted by departure time
        queueMap.putIfAbsent(flight.getDepartureAirport(), new PriorityQueue<>(Comparator.comparingInt(f -> toMinutes(f.getDepartureTime()))));
        queueMap.get(flight.getDepartureAirport()).add(flight);
    }

    // Get flights that are ready to depart at the given simulated time
    public List<Flight> getFlightsToDepart(int currentMinutes) {
        List<Flight> readyFlights = new ArrayList<>();

        for (Airport airport : queueMap.keySet()) {
            Queue<Flight> flights = queueMap.get(airport);
            if (flights == null || flights.isEmpty()) continue;

            Flight next = flights.peek();
            if (next == null) continue;

            int flightTime = toMinutes(next.getDepartureTime());
            int last = lastDepartureTime.getOrDefault(airport, -SLOT_MINUTES);
            int minutesSinceLast = currentMinutes - last;

            //  Flight has not arrived yet, skip
            if (currentMinutes < flightTime) continue;

            // Flight has arrived, check slot
            if (minutesSinceLast >= SLOT_MINUTES) {
                readyFlights.add(flights.poll());
                lastDepartureTime.put(airport, currentMinutes);
            } else {
                //if slot not free, postpone
                int newTime = last + SLOT_MINUTES;

                // normalize over 24 hours
                newTime = ((newTime % (24 * 60)) + (24 * 60)) % (24 * 60);

                next.setDepartureTime(java.time.LocalTime.of(newTime / 60, newTime % 60));
                // next time this flight will not be processed until currentMinutes >= flightTime
            }
        }

        return readyFlights;
    }

    private int toMinutes(java.time.LocalTime t) {
        return t.getHour() * 60 + t.getMinute();
    }

    public void reset() {
        queueMap.clear();
        lastDepartureTime.clear();
    }
}

