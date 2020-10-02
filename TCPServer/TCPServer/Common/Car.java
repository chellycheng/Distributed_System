// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package TCPServer.Common;

public class Car extends ReservableItem
{
	public Car(String location, int count, int price)
	{
		super(location, count, price);
	}

	public String getKey()
	{
		return getKey(getLocation());
	}

	public static String getKey(String location)
	{
		String s = "car-" + location;
		return s.toLowerCase();
	}
}
