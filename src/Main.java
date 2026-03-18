import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

//Handle multiple booking requests fairly by introducing a request intake mechanism that preserves arrival order, reflecting real-world booking behavior during peak demand.
//@version 5.0
abstract class Room{
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;
    public Room(int numberOfBeds, int squareFeet, double pricePerNight){
        this.numberOfBeds=numberOfBeds;
        this.squareFeet=squareFeet;
        this.pricePerNight=pricePerNight;
    }
    public void displayRoomDetails(){
        System.out.println("Beds: "+numberOfBeds);
        System.out.println("Size: "+squareFeet);
        System.out.println("Price per night: "+pricePerNight);
    }
}
class SingleRoom extends Room{
    public SingleRoom(){
        super(1, 250, 1500.0);
    }
}
class DoubleRoom extends Room{
    public DoubleRoom(){
        super(2, 400, 2500.0);
    }
}
class SuiteRoom extends Room{
    public SuiteRoom(){
        super(3, 750, 5000.0);
    }
}
class RoomInventory{
    private Map<String,Integer> roomAvailability;
    public RoomInventory(){
        this.roomAvailability=new HashMap<>();
        this.initializeInventory();
    }
    private void initializeInventory(){
        roomAvailability.put("Single",5);
        roomAvailability.put("Double",3);
        roomAvailability.put("Suite",2);
    }
    public Map<String,Integer> getRoomAvailability(){
        return roomAvailability;
    }
    public void updateAvailability(String roomType, int count){
        roomAvailability.put(roomType,count);
    }
}
class RoomSearchService{
    public void searchAvailableRooms(RoomInventory inventory, Room singleRoom, Room doubleRoom, Room suiteRoom){
        Map<String,Integer> availability=inventory.getRoomAvailability();
        if(availability.get("Single")>0){
            System.out.println("Single Room:");
            singleRoom.displayRoomDetails();
            System.out.println("Available: "+availability.get("Single"));
        }
        if(availability.get("Double")>0){
            System.out.println("\nDouble Room:");
            doubleRoom.displayRoomDetails();
            System.out.println("Available: "+availability.get("Double"));
        }
        if(availability.get("Suite")>0){
            System.out.println("\nSuite Room:");
            suiteRoom.displayRoomDetails();
            System.out.println("Available: "+availability.get("Suite"));
        }
    }
}
class Reservation{
    private String guestName;
    private String roomType;
    public Reservation(String guestName, String roomType){
        this.guestName=guestName;
        this.roomType=roomType;
    }
    public String getGuestName(){
        return guestName;
    }
    public String getRoomType(){
        return roomType;
    }
}
class BookingRequestQueue{
    private Queue<Reservation> requestQueue;
    public BookingRequestQueue(){
        requestQueue=new LinkedList<>();
    }
    public void addRequest(Reservation reservation){
        requestQueue.offer(reservation);
    }
    public Reservation getNextRequest(){
        return requestQueue.poll();
    }
    public boolean hasPendingRequests(){
        return !requestQueue.isEmpty();
    }
}
public class BookMyStayApp{
    public static void main(String[] args){
        System.out.println("Booking Request Queue");
        BookingRequestQueue bookingQueue=new BookingRequestQueue();
        Reservation r1=new Reservation("Abhi","Single");
        Reservation r2=new Reservation("Subha","Double");
        Reservation r3=new Reservation("Vanmathi","Suite");
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);
        while(bookingQueue.hasPendingRequests()){
            Reservation currentRequest=bookingQueue.getNextRequest();
            System.out.println("Processing booking for Guest: "+currentRequest.getGuestName()+", Room Type: "+currentRequest.getRoomType());
        }
    }
}
