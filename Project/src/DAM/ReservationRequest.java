package DAM;

import java.util.Date;

public class ReservationRequest 
{
    private Client client;      
    private DigitalAsset asset;
    private Date requestDate;
    
    public ReservationRequest(Client cli, DigitalAsset a, Date reqDate)  // Para cons.
    {
        client = cli;
        asset = a;
        requestDate = reqDate;
    }
    
    /*----- Getter FUNCs.------------*/
    
    public DigitalAsset getAsset()       //Returns the asset
    {
        return asset;
    }
    
    public Client getClient()     //Returns the Client who made the reservation request
    {
        return client;
    }
    
    public Date getRequestDate()     //Returns the date on which this particular reservation request was made
    {
        return requestDate;
    } 
    
    public Client getBorrower()   //Returns the Client to whom the asset was assigned
    {
        return client;
    }
    
    /*---------------------------------------------*/
    
    // Printing Info of a Reservation Request
    public void display()
    {
        System.out.println(asset.getAssetName() + "\t\t\t" + client.getName() + "\t\t\t" + requestDate);
    }
    
}   // ReservationRequest Class Closed
