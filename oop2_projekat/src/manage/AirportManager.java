package manage;

import java.util.List;

import gui.DialogMessages;
import model.Airport;

public class AirportManager {

    private AirportManager() { }

    public static void addAirport(List<Airport> airportList, Airport a) 
    {
        airportList.add(a);
    }
    
    public static Airport findAirport(List<Airport> airportList, String id)
    {
    	for(Airport b: airportList)
    	{
    		if (b.getId() != null && b.getId().equals(id))
    			return b;
    	}
    	return null;
    }
    
    public static boolean checkAirportId(List<Airport> airportList, Airport a)
    {
    	for(Airport b: airportList)
    	{
    		if (b.getId().equals(a.getId()))
    			return false;
    	}
    	return true;
    }
 
    public static boolean checkAirportIdLetters(Airport a) {
        String id = a.getId();
        
        
        for (int i = 0; i < 3; i++) {
            if (!Character.isUpperCase(id.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
 
    
    public static boolean checkAirportCoordinates(List<Airport> airportList, Airport a)
    {
    	for(Airport b: airportList)
    	{
    		if (b.getX()==a.getX() && b.getY()==a.getY())
    			return false;
    	}
    	return true;
    }
 }

