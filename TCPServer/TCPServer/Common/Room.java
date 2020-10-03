// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package TCPServer.Common;

public class Room extends ReservableItem
{
	public Room(String location, int count, int price)
	{
		super(location, count, price);
	}

	public String getKey()
	{
		return getKey(getLocation());
	}

	public static String getKey(String location)
	{
		String s = "room-" + location;
		return s.toLowerCase();
	}
}

