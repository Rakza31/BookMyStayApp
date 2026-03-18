import java.util.HashMap;
import java.util.Map;

//Introduce centralized inventory management by replacing scattered availability variables with a single, consistent data structure, demonstrating how HashMap solves real-world state management problems.
//@version 3.0
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
public class BookMyStayApp{
    public static void main(String[] args){
        SingleRoom r1=new SingleRoom();
        DoubleRoom r2=new DoubleRoom();
        SuiteRoom r3=new SuiteRoom();
        System.out.println("Hotel Room Inventory Status\n");
        RoomInventory inventory=new RoomInventory();
        Map<String, Integer> currentStock=inventory.getRoomAvailability();
        System.out.println("Single Room: ");
        r1.displayRoomDetails();
        System.out.println("Available: "+currentStock.get("Single"));
        System.out.println("\nDouble Room:");
        r2.displayRoomDetails();
        System.out.println("Available: "+currentStock.get("Double"));
        System.out.println("\nSuite Room:");
        r3.displayRoomDetails();
        System.out.println("Available: "+currentStock.get("Suite"));
    }
}
