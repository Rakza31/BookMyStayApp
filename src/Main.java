import java.util.*;

//Introduce historical tracking of confirmed bookings to provide operational visibility, enable audits, and support reporting, reinforcing a persistence-oriented mindset without introducing external storage.
//@version 8.0
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
    private static int counter=1;
    private String reservationId;
    private String guestName;
    private String roomType;
    public Reservation(String guestName, String roomType){
        this.guestName=guestName;
        this.roomType=roomType;
        this.reservationId="RES-"+counter++;
    }
    public String getReservationId(){
        return reservationId;
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
class RoomAllocationService{
    private Set<String> allocatedRoomIds;
    private Map<String,Set<String>> assignedRoomsByType;
    public RoomAllocationService(){
        allocatedRoomIds=new HashSet<>();
        assignedRoomsByType=new HashMap<>();
        assignedRoomsByType.put("Single", new HashSet<>());
        assignedRoomsByType.put("Double", new HashSet<>());
        assignedRoomsByType.put("Suite", new HashSet<>());
    }
    public boolean allocateRoom(Reservation reservation, RoomInventory inventory){
        String roomType=reservation.getRoomType();
        Map<String,Integer> availability=inventory.getRoomAvailability();
        if(!availability.containsKey(roomType)||availability.get(roomType)<=0){
            System.out.println("Booking failed for "+reservation.getGuestName()+" (No "+roomType+" rooms available)");
            return false;
        }
        String roomId=generateRoomId(roomType);
        if(roomId==null){
            System.out.println("Booking failed for "+reservation.getGuestName()+" (No unique room available)");
            return false;
        }
        allocatedRoomIds.add(roomId);
        assignedRoomsByType.get(roomType).add(roomId);
        inventory.updateAvailability(roomType,availability.get(roomType)-1);
        System.out.println("Booking confirmed for Guest: "+reservation.getGuestName()+", Room ID: "+roomId);
        return true;
    }
    private String generateRoomId(String roomType){
        int counter=1;
        while(true){
            String roomId=roomType+"-"+counter;
            if(!allocatedRoomIds.contains(roomId)){
                return roomId;
            }
            counter++;
            if(counter>100){
                return null;
            }
        }
    }
}
class AddOnService{
    private String serviceName;
    private double cost;
    public AddOnService(String serviceName, double cost){
        this.serviceName=serviceName;
        this.cost=cost;
    }
    public String getServiceName(){
        return serviceName;
    }
    public double getCost(){
        return cost;
    }
}
class AddOnServiceManager{
    private Map<String,List<AddOnService>> servicesByReservation;
    public AddOnServiceManager(){
        servicesByReservation=new HashMap<>();
    }
    public void addService(String reservationID, AddOnService service){
        servicesByReservation.computeIfAbsent(reservationID,k->new ArrayList<>()).add(service);
        System.out.println("Added service: "+service.getServiceName()+" (₹"+service.getCost()+") to Reservation: "+reservationID);
    }
    public double calculateTotalServiceCost(String reservationID){
        List<AddOnService> services=servicesByReservation.get(reservationID);
        if(services==null||services.isEmpty()){
            return 0.0;
        }
        double total=0.0;
        for(AddOnService service:services){
            total+=service.getCost();
        }
        return total;
    }
}
class BookingHistory{
    private List<Reservation> confirmedReservations;
    public BookingHistory(){
        confirmedReservations=new ArrayList<>();
    }
    public void addReservation(Reservation reservation){
        confirmedReservations.add(reservation);
    }
    public List<Reservation> getConfirmedReservations(){
        return confirmedReservations;
    }
}
class BookingReportService{
    public void generateReport(BookingHistory history){
        List<Reservation> reservations=history.getConfirmedReservations();
        System.out.println("\nBooking History Report");
        if(reservations.isEmpty()){
            System.out.println("No bookings to report");
            return;
        }
        Map<String,Integer> roomTypeCount=new HashMap<>();
        for(Reservation r:reservations){
            System.out.println("Guest: "+r.getGuestName()+", Room Type: "+r.getRoomType());
            roomTypeCount.put(r.getRoomType(),roomTypeCount.getOrDefault(r.getRoomType(),0)+1);
        }
    }
}
public class BookMyStayApp{
    public static void main(String[] args){
        System.out.println("Booking History and Reporting");
        BookingRequestQueue bookingQueue=new BookingRequestQueue();
        RoomInventory inventory=new RoomInventory();
        RoomAllocationService allocator=new RoomAllocationService();
        AddOnServiceManager serviceManager=new AddOnServiceManager();
        BookingHistory history=new BookingHistory();
        BookingReportService reportService=new BookingReportService();
        Reservation r1=new Reservation("Abhi","Single");
        Reservation r2=new Reservation("Subha","Double");
        Reservation r3=new Reservation("Vanmathi","Suite");
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);
        while(bookingQueue.hasPendingRequests()){
            Reservation currentRequest=bookingQueue.getNextRequest();
            boolean success=allocator.allocateRoom(currentRequest,inventory);
            if(success){
                history.addReservation(currentRequest);
            }
        }
        reportService.generateReport(history);
    }
}
