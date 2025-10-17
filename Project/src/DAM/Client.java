package DAM;

import java.io.*;
import java.util.*;

public class Client extends User 
{    
    private ArrayList<Assignment> assignedAssets;          //Those assets which are currently assigned to this client
    private ArrayList<ReservationRequest> reservedAssets;  //Those assets which are currently requested by this client to be on reservation

    
    
    public Client(int id,String name, String address, int phoneNum) // para. cons
    {
        super(id,name,address,phoneNum);
        
        assignedAssets = new ArrayList();
        reservedAssets = new ArrayList();        
    }

    
    // Printing Client's Info
    @Override
    public void displayInfo()
    {
        super.displayInfo();
               
        displayAssignedAssets();
        displayReservedAssets();
    }
   
    // Printing Asset's Info Assigned to Client
    public void displayAssignedAssets()
    {
        if (!assignedAssets.isEmpty())
        { 
            System.out.println("\nAssigned Assets are: ");
            
            System.out.println("------------------------------------------------------------------------------");            
            System.out.println("No.\t\tName\t\t\tCreator\t\t\tCategory");
            System.out.println("------------------------------------------------------------------------------");
            
            for (int i = 0; i < assignedAssets.size(); i++)
            {                      
                System.out.print(i + "-" + "\t\t");
                assignedAssets.get(i).getAsset().displayInfo();
                System.out.print("\n");
            }
        }
        else
            System.out.println("\nNo assigned assets.");                
    }
    
    // Printing Asset's Info kept on Reservation by Client
    public void displayReservedAssets()
    {
        if (!reservedAssets.isEmpty())
        { 
            System.out.println("\nOn Reservation Assets are: ");
            
            System.out.println("------------------------------------------------------------------------------");            
            System.out.println("No.\t\tName\t\t\tCreator\t\t\tCategory");
            System.out.println("------------------------------------------------------------------------------");
            
            for (int i = 0; i < reservedAssets.size(); i++)
            {                      
                System.out.print(i + "-" + "\t\t");
                reservedAssets.get(i).getAsset().displayInfo();
                System.out.print("\n");
            }
        }
        else
            System.out.println("\nNo On Reservation assets.");                
    }
   
    // Updating Client's Info
    public void updateClientInfo() throws IOException
    {
        String choice;
        
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        
        System.out.println("\nDo you want to update " + getName() + "'s Name ? (y/n)");  
        choice = sc.next();

        updateClientName(choice, reader);


        System.out.println("\nDo you want to update " + getName() + "'s Address ? (y/n)");  
        choice = sc.next();

        updateClientAddress(choice, reader);

        System.out.println("\nDo you want to update " + getName() + "'s Phone Number ? (y/n)");  
        choice = sc.next();

        updateClientPhoneNumber(choice, sc);

        System.out.println("\nClient is successfully updated.");
        
    }

    private void updateClientPhoneNumber(String choice, Scanner sc) {
        if(choice.equals("y"))
        {
            System.out.println("\nType New Phone Number: ");
            setPhone(sc.nextInt());
            System.out.println("\nThe phone number is successfully updated.");
        }
    }

    private void updateClientAddress(String choice, BufferedReader reader) throws IOException {
        if(choice.equals("y"))
        {
            System.out.println("\nType New Address: ");
            setAddress(reader.readLine());
            System.out.println("\nThe address is successfully updated.");
        }
    }

    private void updateClientName(String choice, BufferedReader reader) throws IOException {
        if(choice.equals("y"))
        {
            System.out.println("\nType New Name: ");
            setName(reader.readLine());
            System.out.println("\nThe name is successfully updated.");
        }
    }

    /*-- Adding and Removing from Assigned Assets---*/
    public void addAssignedAsset(Assignment assignedAsset)
    {
        assignedAssets.add(assignedAsset);
    }
    
    public void removeAssignedAsset(Assignment assignedAsset)
    {
        assignedAssets.remove(assignedAsset);
    }    
    
    /*-------------------------------------------*/
    
    /*-- Adding and Removing from On Reservation Assets---*/
    public void addReservationRequest(ReservationRequest rr)
    {
        reservedAssets.add(rr);
    }
    
    public void removeReservationRequest(ReservationRequest rr)
    {
        reservedAssets.remove(rr);
    }
    
    /*-------------------------------------------*/
    
    /*-----------Getter FUNCs. ------------------*/
    public ArrayList<Assignment> getAssignedAssets()
    {
        return assignedAssets;
    }
    
    public ArrayList<ReservationRequest> getReservedAssets()
    {
        return reservedAssets;
    }
    /*-------------------------------------------*/
}
