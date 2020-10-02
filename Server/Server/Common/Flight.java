// -------------------------------
// Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;

import TCPServer.Common.ReservableItem;

public class Flight extends ReservableItem
{
	public Flight(int flightNum, int flightSeats, int flightPrice)
	{
		super(Integer.valueOf(flightNum).toString(), flightSeats, flightPrice);
	}

	public String getKey()
	{
		return TCPServer.Common.Flight.getKey(Integer.parseInt(getLocation()));
	}

	public static String getKey(int flightNum)
	{
		String s = "flight-" + flightNum;
		return s.toLowerCase();
	}
}

