package ClientTest;

import Client.Command;
import Client.RMIClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.Vector;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RMIClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private static RMIClient client;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @BeforeClass
    public static void setup() {
        // Get a reference to the RMIRegister
        client = new RMIClient();
        client.connectServer();
    }

    @Test
    public void Test1help() throws Exception {
        Vector<String> temp = new Vector<String>();
        temp.add("help");
        client.execute(Command.Help, temp);
        assertThat(outContent.toString(), containsString("Commands supported by the client:"));
    }

    @Test
    public void Test2addFlight() throws Exception {
        String xid ="1";
        String flightNum ="1";
        String seats ="10";
        String price ="100";
        addFlight(xid, flightNum, seats, price);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_flightNum = pasrser(result[1],"Number: ");
        String return_seats = pasrser(result[2],"Seats: ");
        String return_price = pasrser(result[3],"Price: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(flightNum,return_flightNum);
        assertEquals(seats,return_seats);
        assertEquals(price,return_price);
    }

    @Test
    public void Test3addCarsTest() throws Exception {
        String xid ="1";
        String location ="catzone";
        String cars ="10";
        String price ="100";
        addCars(xid, location, cars, price);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_flightNum = pasrser(result[1],"Location: ");
        String return_seats = pasrser(result[2],"Cars: ");
        String return_price = pasrser(result[3],"Price: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(location,return_flightNum);
        assertEquals(cars,return_seats);
        assertEquals(price,return_price);
    }

    @Test
    public void Test4addRoomTest() throws Exception {
        String xid ="1";
        String location ="catzone";
        String rooms ="10";
        String price ="100";
        addRooms(xid, location, rooms, price);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_flightNum = pasrser(result[1],"Location: ");
        String return_seats = pasrser(result[2],"Rooms: ");
        String return_price = pasrser(result[3],"Price: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(location,return_flightNum);
        assertEquals(rooms,return_seats);
        assertEquals(price,return_price);
    }

    @Test
    public void Test5queryFlightTest() throws Exception {
        String xid ="1";
        String flgihtNum ="1";
        String flightseats ="10";
        queryFlight(xid, flgihtNum);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_flightNum = pasrser(result[1],"Number: ");
        String return_seats = pasrser(result[2],"seats available: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(flgihtNum,return_flightNum);
        assertEquals(flightseats,return_seats);
    }

    @Test
    public void Test6queryCarsTest() throws Exception {
        String xid ="1";
        String location ="catzone";
        String num_car = "10";
        queryCars(xid, location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_location = pasrser(result[1],"Location: ");
        String return_num = pasrser(result[2],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(location,return_location);
        assertEquals(num_car,return_num);
    }

    @Test
    public void Test7queryRoomsTest() throws Exception {
        String xid ="1";
        String location ="catzone";
        String num_room = "10";
        queryRooms(xid, location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_flightNum = pasrser(result[1],"Location: ");
        String return_num = pasrser(result[2],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(location,return_flightNum);
        assertEquals(num_room,return_num);
    }

    @Test
    public void Test8addTwiceFlightAndQueryTest() throws Exception{
        String xid = "1";
        String flightNum = "1";
        String expected_seats = "20";
        addFlight(xid,flightNum,"10","100");
        queryFlight(xid,flightNum);
        //now query should return 20
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[5],"xid=");
        String return_flightNum = pasrser(result[6],"Number: ");
        String return_seats = pasrser(result[7],"seats available: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(flightNum,return_flightNum);
        assertEquals(expected_seats,return_seats);

    }

    @Test
    public void Test9queryFlightPriceTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("queryFlightPrice");
        String xid ="1";
        String flgihtNum ="1";
        String price ="100";
        temp.add(xid);
        temp.add(flgihtNum);
        client.execute(Command.QueryFlightPrice, temp);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_price = pasrser(result[1],"seat: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(flgihtNum,return_price);
    }

    @Test
    public void TestaqueryCarsPriceTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("queryCarsPrice");
        String xid ="1";
        String location ="catzone";
        String price = "100";
        temp.add(xid);
        temp.add(location);
        client.execute(Command.QueryCarsPrice, temp);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_price = pasrser(result[1],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(price,return_price);
    }

    @Test
    public void queryRoomsPriceTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("queryRoomsPrice");
        String xid ="1";
        String location ="catzone";
        String price = "100";
        temp.add(xid);
        temp.add(location);
        client.execute(Command.QueryRoomsPrice, temp);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_price = pasrser(result[1],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(price,return_price);
    }

    /*
        Helper method section
     */

    private void addFlight(String xid, String flightNum, String seats, String price) throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addFlight");
        temp.add(xid);
        temp.add(flightNum);
        temp.add(seats);
        temp.add(price);
        client.execute(Command.AddFlight, temp);
    }

    private void addRooms(String xid, String location, String rooms, String price) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addRooms");
        temp.add(xid);
        temp.add(location);
        temp.add(rooms);
        temp.add(price);
        client.execute(Command.AddRooms, temp);
    }

    private void addCars(String xid, String location, String cars, String price) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addCars");
        temp.add(xid);
        temp.add(location);
        temp.add(cars);
        temp.add(price);
        client.execute(Command.AddCars, temp);
    }

    private void queryFlight(String xid, String flgihtNum) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("queryFlight");
        temp.add(xid);
        temp.add(flgihtNum);
        client.execute(Command.QueryFlight, temp);
    }

    private void queryCars(String xid, String location) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("queryCars");
        temp.add(xid);
        temp.add(location);
        client.execute(Command.QueryCars, temp);
    }

    private void queryRooms(String xid, String location) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("queryRooms");
        temp.add(xid);
        temp.add(location);
        client.execute(Command.QueryRooms, temp);
    }

    private String pasrser(String target, String splitter){
        return target.split(splitter)[1];
    }




}
