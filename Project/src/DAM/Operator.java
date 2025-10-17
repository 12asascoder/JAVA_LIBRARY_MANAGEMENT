package DAM;

public class Operator extends Employee {
  
    int workstationNo;     //Workstation Number of the Operator
    public static int currentWorkstationNumber = 0;
  
    public Operator(int id, String n, String a,int ph, double s,int ws) // para cons.
    {
        super(id,n,a,ph,s);
        
        if(ws == -1)
        {
            workstationNo = currentWorkstationNumber;
        }
        else
        {
            workstationNo=ws;
        }
        
        currentWorkstationNumber++;
    }
    
    // Printing Operator's Info
    @Override
    public void displayInfo()
    {
        super.displayInfo();
        System.out.println("Workstation Number: " + workstationNo);
    }
    
}   // Operator's Class Closed
