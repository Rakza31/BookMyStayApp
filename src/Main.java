//Introduce object modeling through inheritance and abstraction before introducing data structures, allowing students to focus on domain design rather than optimization.
//@version 2.0
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
        super(1,250,1500.0);
    }
}
class DoubleRoom extends Room{
    public DoubleRoom(){
        super(2,400,2500.0);
    }
}
class SuiteRoom extends Room{
    public SuiteRoom(){
        super(3,750,5000.0);
    }
}
public class BookMyStayApp{
    public static void main(String[] args){
        SingleRoom r1=new SingleRoom();
        DoubleRoom r2=new DoubleRoom();
        SuiteRoom r3=new SuiteRoom();
        int singleAvailability=5;
        int doubleAvailability=3;
        int suiteAvailability=2;
        System.out.println("Hotel Room Initialization\n");
        System.out.println("Single Room: ");
        r1.displayRoomDetails();
        System.out.println("Available: "+singleAvailability);
        System.out.println("\nDouble Room: ");
        r2.displayRoomDetails();
        System.out.println("Available: "+doubleAvailability);
        System.out.println("\nSuite Room: ");
        r3.displayRoomDetails();
        System.out.println("Available: "+suiteAvailability);
    }
}
