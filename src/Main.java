import java.util.*;

//Demonstrate how concurrent access to shared resources can lead to inconsistent system state and show how synchronization ensures correctness under multi-user conditions.
//@version 11.0
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
    public String allocateRoom(Reservation reservation, RoomInventory inventory){
        String roomType=reservation.getRoomType();
        Map<String,Integer> availability=inventory.getRoomAvailability();
        if(!availability.containsKey(roomType)||availability.get(roomType)<=0){
            System.out.println("Booking failed for "+reservation.getGuestName());
            return null;
        }
        String roomId=generateRoomId(roomType);
        if(roomId==null){
            return null;
        }
        allocatedRoomIds.add(roomId);
        assignedRoomsByType.get(roomType).add(roomId);
        inventory.updateAvailability(roomType, availability.get(roomType)-1);
        System.out.println("Booking confirmed for Guest: "+reservation.getGuestName()+", Room ID: "+roomId);
        return roomId;
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
        System.out.println("\nBooking Report:");
        Map<String,Integer> count=new HashMap<>();
        for(Reservation r:history.getConfirmedReservations()){
            System.out.println(r.getReservationId()+" | "+r.getGuestName() + " | "+r.getRoomType());
            count.put(r.getRoomType(),count.getOrDefault(r.getRoomType(), 0)+1);
        }
        for(String type:count.keySet()){
            System.out.println(type+": "+count.get(type));
        }
    }
}
class InvalidBookingException extends Exception{
    public InvalidBookingException(String message){
        super(message);
    }
}
class ReservationValidator{
    public void validate(String guestName, String roomType, RoomInventory inventory) throws InvalidBookingException{
        if(guestName==null||guestName.trim().isEmpty()){
            throw new InvalidBookingException("Guest name cannot be empty");
        }
        if(roomType==null||roomType.trim().isEmpty()){
            throw new InvalidBookingException("Room type cannot be empty");
        }
        Map<String,Integer> availability=inventory.getRoomAvailability();
        if(!availability.containsKey(roomType)){
            throw new InvalidBookingException("Invalid room type: "+roomType);
        }
        if(availability.get(roomType)<=0){
            throw new InvalidBookingException("No "+roomType+" rooms available");
        }
    }
}
class CancellationService{
        private Stack<String> rollbackStack=new Stack<>();
        private Map<String, String> reservationToRoomId=new HashMap<>();
        private Map<String, String> reservationToRoomType=new HashMap<>();

        public void registerBooking(String resId, String roomId, String roomType){
            reservationToRoomId.put(resId,roomId);
            reservationToRoomType.put(resId,roomType);
        }
        public void cancelBooking(String resId, RoomInventory inventory){
            if(!reservationToRoomId.containsKey(resId)){
                System.out.println("Invalid reservation ID");
                return;
            }
            String roomType=reservationToRoomType.get(resId);
            Map<String,Integer> availability=inventory.getRoomAvailability();
            inventory.updateAvailability(roomType, availability.get(roomType)+1);
            rollbackStack.push(resId);
            reservationToRoomId.remove(resId);
            reservationToRoomType.remove(resId);
            System.out.println("Cancelled: "+resId);
        }
        public void showRollbackHistory(){
            System.out.println("Rollback History:");
            while(!rollbackStack.isEmpty()){
                System.out.println(rollbackStack.pop());
            }
        }
}
class ConcurrentBookingProcessor implements Runnable{
    private BookingRequestQueue queue;
    private RoomInventory inventory;
    private RoomAllocationService allocator;
    private CancellationService cancelService;
    private BookingHistory history;

    public ConcurrentBookingProcessor(BookingRequestQueue bookingQueue, RoomInventory inventory, RoomAllocationService allocationService, CancellationService cancellationService, BookingHistory history){
        this.queue=bookingQueue;
        this.inventory=inventory;
        this.allocator=allocationService;
        this.cancelService=cancellationService;
        this.history=history;
    }
    @Override
    public void run(){
        while(true){
            Reservation r;
            synchronized(queue){
                if(!queue.hasPendingRequests()) break;
                r=queue.getNextRequest();
            }
            if(r == null) continue;
            String roomId;
            synchronized(inventory){
                roomId=allocator.allocateRoom(r,inventory);
            }
            if(roomId!=null){
                cancelService.registerBooking(r.getReservationId(),roomId,r.getRoomType());
                history.addReservation(r);
            }
        }
    }
}
public class BookMyStayApp{
    public static void main(String[] args) throws Exception{
        System.out.println("Concurrent Booking Simulation");
        BookingRequestQueue queue=new BookingRequestQueue();
        RoomInventory inventory=new RoomInventory();
        RoomAllocationService allocator=new RoomAllocationService();
        CancellationService cancelService=new CancellationService();
        BookingHistory history=new BookingHistory();
        BookingReportService report=new BookingReportService();

        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Vanmathi", "Double"));
        queue.addRequest(new Reservation("Kural", "Suite"));
        queue.addRequest(new Reservation("Subha", "Single"));
        Thread t1=new Thread(new ConcurrentBookingProcessor(queue, inventory, allocator, cancelService, history));
        Thread t2=new Thread(new ConcurrentBookingProcessor(queue, inventory, allocator, cancelService, history));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        report.generateReport(history);
    }
}
