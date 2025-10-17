package DAM;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;

public class Assignment 
{
    private Client client;      
    private DigitalAsset asset;
    
    private Employee assigner;
    private Date assignedDate;
    
    private Date dateReturned;
    private Employee receiver;
    
    private boolean penaltyPaid;
       
    public Assignment(Client cli, DigitalAsset a, Employee assigner, Employee receiver, Date assignedDate, Date returnedDate, boolean penaltyPaid)  // Para cons.
    {
        client = cli;
        asset = a;
        this.assigner = assigner;
        this.receiver = receiver;
        this.assignedDate = assignedDate;
        dateReturned = returnedDate;
        
        this.penaltyPaid = penaltyPaid;
    }
    
    /*----- Getter FUNCs.------------*/
    
    public DigitalAsset getAsset()       //Returns the asset
    {
        return asset;
    }
    
    public Employee getIssuer()     //Returns the Employee who assigned the asset
    {
        return assigner;
    }
    
    public Employee getReceiver()  //Returns the Employee to whom asset is returned
    {
        return receiver;
    }
    
    public Date getIssuedDate()     //Returns the date on which this particular asset was assigned
    {
        return assignedDate;
    } 

    public Date getReturnDate()     //Returns the date on which this particular asset was returned
    {
        return dateReturned;
    }
    
    public Client getBorrower()   //Returns the Client to whom the asset was assigned
    {
        return client;
    }
    
    public boolean getFineStatus()  // Returns status of penalty
    {
        return penaltyPaid;
    }
    /*---------------------------------------------*/
    
    
    /*----------Setter FUNCs.---------------------*/
    public void setReturnedDate(Date dReturned)
    {
        dateReturned = dReturned;
    }
    
    public void setFineStatus(boolean fStatus)
    {
        penaltyPaid = fStatus;
    }    
    
    public void setReceiver(Employee r)
    {
        receiver = r;
    }
    /*-------------------------------------------*/
    


    //Computes penalty for a particular assignment only
    public double computeFine1()
    {

        //-----------Computing Penalty-----------        
        double totalPenalty = 0;
        
        if (!penaltyPaid)
        {    
            Date iDate = assignedDate;
            Date rDate = new Date();                

            long days =  ChronoUnit.DAYS.between(rDate.toInstant(), iDate.toInstant());        
            days=0-days;

            days = days - AssetRepository.getInstance().return_deadline;

            if(days>0)
                totalPenalty = days * AssetRepository.getInstance().per_day_penalty;
            else
                totalPenalty=0;
        }
        return totalPenalty;
    }
    
    
    public void payPenalty()
    {
        //-----------Computing Penalty-----------//
        
        double totalPenalty = computeFine1();
                
        if (totalPenalty > 0)
        {
            System.out.println("\nTotal Penalty generated: Rs " + totalPenalty);

            System.out.println("Do you want to pay? (y/n)");
            
            Scanner input = new Scanner(System.in); 
            
            String choice = input.next();
            
            if(choice.equals("y") || choice.equals("Y"))
                penaltyPaid = true; 
            
            if(choice.equals("n") || choice.equals("N"))
                penaltyPaid = false; 
        }
        else
        {
            System.out.println("\nNo penalty is generated.");
            penaltyPaid = true;
        }        
    }


    // Extending assigned Date 
    public void extendAssignment(Date iDate)
    {        
        assignedDate = iDate;
        
        System.out.println("\nThe deadline of the asset " + getAsset().getAssetName() + " has been extended.");
        System.out.println("Assigned Asset is successfully extended!\n");
    }

}   // Assignment class Closed
