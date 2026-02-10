package manage;

import java.util.List;

import model.Flight;

public class FlightManager {

	private FlightManager() { }

    public static void addFlight(List<Flight> flightList, Flight a) 
    {
        flightList.add(a);
    }
}
