package ClientTest;

import Client.Command;
import Client.RMIClient;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.Vector;
import static org.junit.matchers.JUnitMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RMIClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private static RMIClient client;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @BeforeAll
    public static void setup() {
        // Get a reference to the RMIRegister
        client = new RMIClient();
        client.connectServer();
    }

    @Test
    @Order(1)
    public void helpTest() throws Exception {
        Vector<String> temp = new Vector<String>();
        temp.add("help");
        client.execute(Command.Help, temp);
        assertEquals(outContent.toString(), containsString("Commands supported by the client:"));
    }

    @Test
    @Order(2)
    public void addFlightTest() throws Exception {
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
    @Order(3)
    public void addCarsTest() throws Exception {
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
    @Order(4)
    public void addRoomTest() throws Exception {
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
    @Order(5)
    public void queryFlightTest() throws Exception {
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
    @Order(6)
    public void queryCarsTest() throws Exception {
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
    @Order(7)
    public void queryRoomsTest() throws Exception {
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
    @Order(8)
    public void addTwiceFlightAndQueryTest() throws Exception{
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
    @Order(9)
    public void queryFlightPriceTest() throws Exception {
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
        String return_price = pasrser(result[2],"seat: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(price,return_price);
    }

    @Test
    @Order(10)
    public void queryCarsPriceTest() throws Exception {
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
        String return_location = pasrser(result[1],"Location: ");
        String return_price = pasrser(result[2],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(location,return_location);
        assertEquals(price,return_price);
    }

    @Test
    @Order(11)
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
        String return_price = pasrser(result[2],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(price,return_price);
    }

    @Test
    @Order(12)
    public void addCustomerTest() throws Exception {
        String xid ="1";
        addCustomer(xid);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        assertEquals(xid+"]",return_xid);
        assertThat(result[1], containsString("Add customer ID"));
    }

    @Test
    @Order(13)
    public void addCustomerIDTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        addCustomerID(xid,customerID);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
    }

    @Test
    @Order(14)
    public void reserveFlightTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String flightNum = "1";
        String flightseats = "19";
        reserveFlight(xid,customerID,flightNum);
        queryFlight(xid,flightNum);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Number: ");
        String return_seats = pasrser(result[6],"seats available: ");

        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(flightNum, return_fNum);
        assertThat(result[3], containsString("Flight Reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(15)
    public void reserveCarTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String location = "catzone";
        String flightseats = "9";
        reserveCar(xid,customerID,location);
        queryCars(xid,location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Location: ");
        String return_seats = pasrser(result[6],"location: ");

        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(location, return_fNum);
        assertThat(result[3], containsString("Car Reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(16)
    public void reserveRoomTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String location = "catzone";
        String flightseats = "9";
        reserveRoom(xid,customerID,location);
        queryRooms(xid,location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Location: ");
        String return_seats = pasrser(result[6],"location: ");

        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(location, return_fNum);
        assertThat(result[3], containsString("Room Reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(17)
    public void reserveFlightCustomerFailTest() throws Exception {
        String xid ="1";
        String customerID = "10";
        String flightNum = "1";
        String flightseats = "19";
        reserveFlight(xid,customerID,flightNum);
        queryFlight(xid,flightNum);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Number: ");
        String return_seats = pasrser(result[6],"seats available: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(flightNum, return_fNum);
        assertThat(result[3], containsString("Flight could not be reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(18)
    public void reserveFlightItemFailTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String flightNum = "1";
        String flightNum_f = "10";
        String flightseats = "19";
        reserveFlight(xid,customerID,flightNum_f);
        queryFlight(xid,flightNum);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Number: ");
        String return_seats = pasrser(result[6],"seats available: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(flightNum_f, return_fNum);
        assertThat(result[3], containsString("Flight could not be reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(19)
    public void reserveCarCustomerFailTest() throws Exception {
        String xid ="1";
        String customerID = "10";
        String location = "catzone";
        String flightseats = "9";
        reserveCar(xid,customerID,location);
        queryCars(xid,location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Location: ");
        String return_seats = pasrser(result[6],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(location, return_fNum);
        assertThat(result[3], containsString("Car could not be reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(20)
    public void reserveCarItemFailTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String location_f = "fakecatzone";
        String location = "catzone";
        String flightseats = "9";
        reserveCar(xid,customerID,location_f);
        queryCars(xid,location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Location: ");
        String return_seats = pasrser(result[6],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(location_f, return_fNum);
        assertThat(result[3], containsString("Car could not be reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(21)
    public void reserveRoomCustomerFailTest() throws Exception {
        String xid ="1";
        String customerID = "10";
        String location = "catzone";
        String flightseats = "9";
        reserveRoom(xid,customerID,location);
        queryRooms(xid,location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Location: ");
        String return_seats = pasrser(result[6],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(location, return_fNum);
        assertThat(result[3], containsString("Room could not be reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(22)
    public void reserveRoomItemFailTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String location_f = "fakecatzone";
        String location = "catzone";
        String flightseats = "9";
        reserveRoom(xid,customerID,location_f);
        queryRooms(xid,location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fNum = pasrser(result[2],"Location: ");
        String return_seats = pasrser(result[6],"location: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(location_f, return_fNum);
        assertThat(result[3], containsString("Room could not be reserved"));
        assertEquals(flightseats,return_seats);
    }

    @Test
    @Order(23)
    public void bundleAllTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String flightNum = "1";
        String location = "catzone";
        String isCar = "True";
        String isRoom = "True";

        String expect_seats = "18";
        String expect_rn = "8";
        bundle(xid,customerID,flightNum,location,isCar,isRoom);
        queryFlight(xid,flightNum);
        queryCars(xid,location);
        queryRooms(xid,location);
        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        String return_fn = pasrser(result[2],"Number: ");
        String return_location = pasrser(result[3],"Car/Room: ");
        String return_isCar = pasrser(result[4],"Book Car: ");
        String return_isRoom = pasrser(result[5],"Book Room: ");

        String return_seats = pasrser(result[9],"seats available: ");
        String return_car_loc = pasrser(result[12],"location: ");
        String return_room_loc = pasrser(result[15],"location: ");

        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertEquals(flightNum, return_fn);
        assertEquals(location, return_location);
        assertEquals(isCar, return_isCar);
        assertEquals(isCar, return_isRoom);
        assertThat(result[6], containsString("Bundle Reserved"));

        assertEquals(expect_seats,return_seats);
        assertEquals(expect_rn,return_car_loc);
        assertEquals(expect_rn,return_room_loc);
    }

    @Test
    @Order(24)
    public void bundleOnlyFlightTest() throws Exception {
    }

    @Test
    @Order(25)
    public void bundleFailureTest() throws Exception {
    }

    @Test
    @Order(26)
    public void deleteCustomerTest() throws Exception {
        String xid ="1";
        String customerID = "1";
        String flightNum = "1";
        String expect_fn = "20";
        String expect_rn = "10";
        String location = "catzone";
        deleteCustomer(xid,customerID);
        queryFlight(xid,flightNum);
        queryCars(xid,location);
        queryRooms(xid,location);

        String[] result = outContent.toString().split("\n");
        String return_xid = pasrser(result[0],"xid=");
        String return_cid = pasrser(result[1],"ID: ");
        assertEquals(xid+"]",return_xid);
        assertEquals(customerID, return_cid);
        assertThat(result[2], containsString("Customer Deleted"));


        String return_seats = pasrser(result[5],"seats available: ");
        String return_car_loc = pasrser(result[8],"location: ");
        String return_room_loc = pasrser(result[11],"location: ");
        assertEquals(return_seats,expect_fn);
        assertEquals(expect_rn,return_car_loc);
        assertEquals(expect_rn,return_room_loc);

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

    private void addCustomer(String xid) throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addCustomer");
        temp.add(xid);
        client.execute(Command.AddCustomer, temp);
    }

    private void addCustomerID(String xid, String customerID) throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("addCustomerID");
        temp.add(xid);
        temp.add(customerID);
        client.execute(Command.AddCustomerID, temp);
    }

    private void deleteCustomer(String xid, String customerID) throws Exception {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("deleteCustomer");
        temp.add(xid);
        temp.add(customerID);
        client.execute(Command.DeleteCustomer, temp);
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

    private void reserveFlight(String xid, String customerID, String flightNum) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("reserveFlight");
        temp.add(xid);
        temp.add(customerID);
        temp.add(flightNum);
        client.execute(Command.ReserveFlight, temp);
    }

    private void reserveCar(String xid, String customerID, String location) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("reserveCar");
        temp.add(xid);
        temp.add(customerID);
        temp.add(location);
        client.execute(Command.ReserveCar, temp);
    }

    private void reserveRoom(String xid, String customerID, String location) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("reserveRoom");
        temp.add(xid);
        temp.add(customerID);
        temp.add(location);
        client.execute(Command.ReserveRoom, temp);
    }

    private void bundle(String xid, String customerID, String flightNum, String location, String isCar, String isRoom) throws RemoteException {
        Vector<String> temp = new Vector<String>();
        //command
        temp.add("bundle");
        temp.add(xid);
        temp.add(customerID);
        temp.add(flightNum);
        temp.add(location);
        temp.add(isCar);
        temp.add(isRoom);
        client.execute(Command.Bundle, temp);
    }

    private String pasrser(String target, String splitter){
        return target.split(splitter)[1];
    }

}
