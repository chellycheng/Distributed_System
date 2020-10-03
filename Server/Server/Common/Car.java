// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;

import TCPServer.Common.ReservableItem;

public class Car extends ReservableItem
{
	public Car(String location, int count, int price)
	{
		super(location, count, price);
	}

	public String getKey()
	{
		return TCPServer.Common.Car.getKey(getLocation());
	}

	public static String getKey(String location)
	{
		String s = "car-" + location;
		return s.toLowerCase();
	}
}
