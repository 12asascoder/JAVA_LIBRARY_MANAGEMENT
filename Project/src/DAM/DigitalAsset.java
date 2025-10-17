package DAM;

import java.io.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DigitalAsset {

    private int assetID;           // ID given by a repository to an asset to make it distinguishable from other assets
    private String assetName;         // Name of an asset 
    private String category;       // Category to which an asset is related!
    private String creator;        // Creator of asset!
    private boolean isAssigned;        // this will be true if the asset is currently assigned to some client.
    private ReservationRequestOperations reservationOperations =new ReservationRequestOperations();
    static int currentIdNumber = 0;     //This will be unique for every asset, since it will be incremented when everytime
                                        //when an asset is created
    
  
    public DigitalAsset(int id,String name, String cat, String creator, boolean assigned)    // Parameterise cons.
    {
        currentIdNumber++;
        if(id==-1)
        {
            assetID = currentIdNumber;
        }
        else
            assetID=id;
        
        assetName = name;
        category = cat;
        this.creator = creator;
        isAssigned = assigned;

    }


    // printing all reservation req on an asset.
    public void displayReservationRequests()
    {
        if (!reservationOperations.reservationRequests.isEmpty())
        { 
            System.out.println("\nReservation Requests are: ");
            
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");            
            System.out.println("No.\t\tAsset's Name\t\t\tClient's Name\t\t\tRequest Date");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
            
            for (int i = 0; i < reservationOperations.reservationRequests.size(); i++)
            {                      
                System.out.print(i + "-" + "\t\t");
                reservationOperations.reservationRequests.get(i).display();
            }
        }
        else
            System.out.println("\nNo Reservation Requests.");                                
    }
    
    // printing asset's Info
    public void displayInfo()
    {
        System.out.println(assetName + "\t\t\t" + creator + "\t\t\t" + category);
    }
    
    // changing Info of an Asset
    public void modifyAssetInfo() throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        String input;
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("\nUpdate Creator? (y/n)");
        input = scanner.next();
        
        if(input.equals("y"))
        {
            System.out.println("\nEnter new Creator: ");
            creator = reader.readLine();
        }

        System.out.println("\nUpdate Category? (y/n)");
        input = scanner.next();
        
        if(input.equals("y"))
        {
            System.out.println("\nEnter new Category: ");
            category = reader.readLine();
        }

        System.out.println("\nUpdate Asset Name? (y/n)");
        input = scanner.next();
        
        if(input.equals("y"))
        {
            System.out.println("\nEnter new Asset Name: ");
            assetName = reader.readLine();
        }        
        
        System.out.println("\nAsset is successfully updated.");
        
    }
    
    /*------------Getter FUNCs.---------*/
    
    public String getAssetName()
    {
        return assetName;
    }

    public String getCategory()
    {
        return category;
    }

    public String getCreator()
    {
        return creator;
    }
    
    public boolean getAssignedStatus()
    {
        return isAssigned;
    }
    
    public void setAssignedStatus(boolean s)
    {
        isAssigned = s;
    }
    
     public int getID()
    {
        return assetID;
    }
     
     public ArrayList<ReservationRequest> getReservationRequests()
    {
        return reservationOperations.reservationRequests;
    }
    /*-----------------------------------*/
     
    // Setter Static Func.
    public static void setIDCount(int n)
    {
        currentIdNumber = n;
    }
    

    
    
    //-------------------------------------------------------------------//
    
    // Placing asset on Reservation
    public void placeAssetOnReservation(Client cli)
    {
        ReservationRequest rr = new ReservationRequest(cli,this, new Date());

        reservationOperations.addReservationRequest(rr);        //Add this reservation request to reservationRequests queue of this asset
        cli.addReservationRequest(rr);      //Add this reservation request to that particular client's class as well
        
        System.out.println("\nThe asset " + assetName + " has been successfully placed on reservation by client " + cli.getName() + ".\n");
    }
    
    


   // Request for Reserving an Asset
    public void createReservationRequest(Client client)
    {
        boolean makeRequest = true;

        //If that client has already been assigned that particular asset. Then he isn't allowed to make request for that asset. He will have to extend the assigned asset in order to extend the return deadline.
        for(int i=0;i<client.getAssignedAssets().size();i++)
        {
            if(client.getAssignedAssets().get(i).getAsset()==this)
            {
                System.out.println("\n" + "You have already been assigned " + assetName);
                return;                
            }
        }
        
        
        //If that client has already requested for that particular asset. Then he isn't allowed to make the same request again.
        for (int i = 0; i < reservationOperations.reservationRequests.size(); i++)
        {
            if ((reservationOperations.reservationRequests.get(i).getClient() == client))
            {
                makeRequest = false;    
                break;
            }
        }

        if (makeRequest)
        {
            placeAssetOnReservation(client);
        }
        else
            System.out.println("\nYou already have one reservation request for this asset.\n");
    }

    
    // Getting Info of a Reservation Request
    public void processReservationRequest(ReservationRequest rr)
    {
        reservationOperations.removeReservationRequest();
        rr.getClient().removeReservationRequest(rr);
    }

    
        
    // Assigning an Asset
    public void assignAsset(Client client, Employee employee)
    {        
        //First deleting the expired reservation requests
        Date today = new Date();        
        
        ArrayList<ReservationRequest> rRequests = reservationOperations.reservationRequests;
        
        for (int i = 0; i < rRequests.size(); i++)
        {
            ReservationRequest rr = rRequests.get(i);            
            
            //Remove that reservation request which has expired
            long days =  ChronoUnit.DAYS.between(today.toInstant(), rr.getRequestDate().toInstant());        
            days = 0-days;
            
            if(days>AssetRepository.getInstance().getHoldRequestExpiry())
            {
                reservationOperations.removeReservationRequest();
                rr.getClient().removeReservationRequest(rr);
            } 
        }
               
        if (isAssigned)
        {
            System.out.println("\nThe asset " + assetName + " is already assigned.");
            System.out.println("Would you like to place the asset on reservation? (y/n)");
             
            Scanner sc = new Scanner(System.in);
            String choice = sc.next();
            
            if (choice.equals("y"))
            {                
                createReservationRequest(client);
            }
        }
        
        else
        {               
            if (!reservationOperations.reservationRequests.isEmpty())
            {
                boolean hasRequest = false;
                
                for (int i = 0; i < reservationOperations.reservationRequests.size() && !hasRequest;i++)
                {
                    if (reservationOperations.reservationRequests.get(i).getClient() == client)
                        hasRequest = true;
                        
                }
                
                if (hasRequest)
                {
                    //If this particular client has the earliest request for this asset
                    if (reservationOperations.reservationRequests.get(0).getClient() == client)
                        processReservationRequest(reservationOperations.reservationRequests.get(0));

                    else
                    {
                        System.out.println("\nSorry some other users have requested for this asset earlier than you. So you have to wait until their reservation requests are processed.");
                        return;
                    }
                }
                else
                {
                    System.out.println("\nSome users have already placed this asset on request and you haven't, so the asset can't be assigned to you.");
                    
                    System.out.println("Would you like to place the asset on reservation? (y/n)");

                    Scanner sc = new Scanner(System.in);
                    String choice = sc.next();
                    
                    if (choice.equals("y"))
                    {
                        createReservationRequest(client); 
                    }                    
                    
                    return;
                }               
            }
                        
            //If there are no reservation requests for this asset, then simply assign the asset.            
            setAssignedStatus(true);
            
            Assignment assignmentHistory = new Assignment(client,this,employee,null,new Date(),null,false);
            
            AssetRepository.getInstance().addAssignment(assignmentHistory);
            client.addAssignedAsset(assignmentHistory);
                                    
            System.out.println("\nThe asset " + assetName + " is successfully assigned to " + client.getName() + ".");
            System.out.println("\nAssigned by: " + employee.getName());            
        }
    }
        
        
    // Returning an Asset
    public void returnAsset(Client client, Assignment ass, Employee employee)
    {
        ass.getAsset().setAssignedStatus(false);        
        ass.setReturnedDate(new Date());
        ass.setReceiver(employee);        
        
        client.removeAssignedAsset(ass);
        
        ass.payPenalty();
        
        System.out.println("\nThe asset " + ass.getAsset().getAssetName() + " is successfully returned by " + client.getName() + ".");
        System.out.println("\nReceived by: " + employee.getName());            
    }
    
}   // DigitalAsset Class Closed
