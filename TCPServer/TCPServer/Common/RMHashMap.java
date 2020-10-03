// -------------------------------
// Kevin T. Manley
// CSE 593
// -------------------------------

package TCPServer.Common;

import java.util.*;

// A specialization of HashMap with some extra diagnostics
public class RMHashMap extends HashMap<String, TCPServer.Common.RMItem>
{
	public RMHashMap() {
		super();
	}

	public String toString()
	{
		String s = "--- BEGIN RMHashMap ---\n";
		for (String key : keySet())
		{
			String value = get(key).toString();
			s = s + "[KEY='" + key + "']" + value + "\n";
		}
		s = s + "--- END RMHashMap ---";
		return s;
	}

	public void dump()
	{
		System.out.println(toString());
	}

	public Object clone()
	{
		TCPServer.Common.RMHashMap obj = new TCPServer.Common.RMHashMap();
		for (String key : keySet())
		{
			obj.put(key, (RMItem)get(key).clone());
		}
		return obj;
	}
}

