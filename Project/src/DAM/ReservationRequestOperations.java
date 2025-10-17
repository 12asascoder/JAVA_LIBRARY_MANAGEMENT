package DAM;

import java.util.ArrayList;

public class ReservationRequestOperations 
{
    public ArrayList<ReservationRequest> reservationRequests = new ArrayList();
    
    public void addReservationRequest(ReservationRequest rr)
    {
        reservationRequests.add(rr);
    }
    
    public void removeReservationRequest()
    {
        if (!reservationRequests.isEmpty())
            reservationRequests.remove(0);
    }
    
}   // ReservationRequestOperations Class Closed
