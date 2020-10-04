package ClientTest;
import Client.*;
import org.junit.*;
import java.io.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Vector;

public class RMIClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private RMIClient client;

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

    @Before
    public void before() {
        // Get a reference to the RMIRegister
        client = new RMIClient();
        client.connectServer();

    }

    @Test
    public void helpTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        temp.add("help");
        client.execute(Command.Help, temp);
        assertThat(outContent.toString(), containsString("Commands supported by the client:"));
    }

    @Test
    public void addFlgihtTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addFlight");
        String xid ="1";
        String flgihtNum ="1";
        String seats ="10";
        String price ="100";
        temp.add(xid);
        temp.add(flgihtNum);
        temp.add(seats);
        temp.add(price);
        client.execute(Command.AddFlight, temp);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_flightNum = pasrser(result[1],"Number: ");
        String return_seats = pasrser(result[2],"Seats: ");
        String return_price = pasrser(result[3],"Price: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(flgihtNum,return_flightNum);
        assertEquals(seats,return_seats);
        assertEquals(price,return_price);
    }

    @Test
    public void addRoomTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addRooms");
        String xid ="1";
        String location ="catzone";
        String rooms ="10";
        String price ="100";
        temp.add(xid);
        temp.add(location);
        temp.add(rooms);
        temp.add(price);
        client.execute(Command.AddRooms, temp);
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
    public void addCarsTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addCars");
        String xid ="1";
        String location ="catzone";
        String cars ="10";
        String price ="100";
        temp.add(xid);
        temp.add(location);
        temp.add(cars);
        temp.add(price);
        client.execute(Command.AddCars, temp);
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

    private String pasrser(String target, String splitter){
        return target.split(splitter)[1];
    }

//    @Test
//    public void helpTest() throws Exception {
//        Vector<String> temp = new Vector<String>();
//        temp.add("help");
//        client.execute(Command.Help, temp);
//        assertThat(outContent.toString(), containsString("Commands supported by the client:"));
//    }

//    @Test
//    public void helpTest() throws Exception {
//        Vector<String> temp = new Vector<String>();
//        temp.add("help");
//        client.execute(Command.Help, temp);
//        assertThat(outContent.toString(), containsString("Commands supported by the client:"));
//    }



}
