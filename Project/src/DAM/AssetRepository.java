package DAM;


// Including Header Files.
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AssetRepository {
    
    private String repositoryName;                                // name of repository
    public static Manager manager;                        // object of Manager (only one)
    public static ArrayList <User> users;                 // all operators and clients
    private ArrayList <DigitalAsset> assetsInRepository;            // all assets in repository are here!
    
    private ArrayList <Assignment> assignments;                     // history of all assets which have been assigned
        
    public int return_deadline;                   //return deadline after which penalty will be generated each day
    public double per_day_penalty;
    
    public int hold_request_expiry;                    //number of days after which a reservation request will expire
    //Created object of the reservation request operations
    private ReservationRequestOperations reservationRequestsOperations =new ReservationRequestOperations();

    
    /*----Following Singleton Design Pattern (Lazy Instantiation)------------*/
    private static AssetRepository obj;

    public static AssetRepository getInstance()
    {
        if(obj==null)
        {
            obj = new AssetRepository();
        }
        
        return obj;
    }
    /*---------------------------------------------------------------------*/
    
    private AssetRepository()   // default cons.
    {
        repositoryName = null;
        manager = null;
        users = new ArrayList();
    
        assetsInRepository = new ArrayList();
        assignments = new ArrayList();
    }

    
    /*------------Setter FUNCs.------------*/
    
    public void setReturnDeadline(int deadline)
    {
        return_deadline = deadline;
    }

    public void setPenalty(double perDayPenalty)
    {
        per_day_penalty = perDayPenalty;
    }

    public void setRequestExpiry(int hrExpiry)
    {
        hold_request_expiry = hrExpiry;
    }
    /*--------------------------------------*/    
    
    
    
    // Setter Func.
    public void setName(String n)   
    {
        repositoryName = n;
    }
     
    /*-----------Getter FUNCs.------------*/
    
    public int getHoldRequestExpiry()
    {
        return hold_request_expiry;
    }
    
    public ArrayList<User> getPersons()
    {
        return users;
    }
    
    public Manager getLibrarian()
    {
        return manager;
    }
      
    public String getLibraryName()
    {
        return repositoryName;
    }

    public ArrayList<DigitalAsset> getBooks()
    {
        return assetsInRepository;
    }
    
    /*---------------------------------------*/

    /*-----Adding other People in Repository----*/

    public void addClerk(Operator c)
    {
        users.add(c);
    }

    public void addBorrower(Client b)
    {
        users.add(b);
    }

    
    public void addAssignment(Assignment l)
    {
        assignments.add(l);
    }
    
    /*----------------------------------------------*/
      
    /*-----------Finding People in Repository--------------*/
    public Client findBorrower()
    {
        System.out.println("\nEnter Client's ID: ");
        
        int id = 0;
        
        Scanner scanner = new Scanner(System.in);
        
        try{
            id = scanner.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nInvalid Input");
        }

        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).getID() == id && users.get(i).getClass().getSimpleName().equals("Client"))
                return (Client)(users.get(i));
        }
        
        System.out.println("\nSorry this ID didn't match any Client's ID.");
        return null;
    }
    
    public Client findClient()
    {
        return findBorrower();
    }
    
    public Operator findClerk()
    {
        System.out.println("\nEnter Operator's ID: ");
        
        int id = 0;
        
        Scanner scanner = new Scanner(System.in);
        
        try{
            id = scanner.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nInvalid Input");
        }

        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).getID() == id && users.get(i).getClass().getSimpleName().equals("Operator"))
                return (Operator)(users.get(i));
        }
        
        System.out.println("\nSorry this ID didn't match any Operator's ID.");
        return null;
    }
    
    public Operator findOperator()
    {
        return findClerk();
    }
    
    /*------- FUNCS. on Assets In Repository--------------*/
    public void addBookinLibrary(DigitalAsset b)
    {
        assetsInRepository.add(b);
    }
    
    //When this function is called, only the pointer of the asset placed in assetsInRepository is removed. But the real object of asset
    //is still there in memory because pointers of that asset placed in AssignedAssets and ReturnedAssets are still pointing to that asset. And we
    //are maintaining those pointers so that we can maintain history.
    //But if we donot want to maintain history then we can delete those pointers placed in AssignedAssets and ReturnedAssets as well which are
    //pointing to that asset. In this way the asset will be really removed from memory.
    public void removeAssetFromRepository(DigitalAsset b)  
    {
        boolean delete = true;
        
        //Checking if this asset is currently assigned to some client
        for (int i = 0; i < users.size() && delete; i++)
        {
            if (users.get(i).getClass().getSimpleName().equals("Client"))
            {
                ArrayList<Assignment> cliAssets = ((Client)(users.get(i))).getAssignedAssets();
                
                for (int j = 0; j < cliAssets.size() && delete; j++)
                {
                    if (cliAssets.get(j).getAsset() == b)
                    {
                        delete = false;
                        System.out.println("This particular asset is currently assigned to some client.");
                    }
                }              
            }
        }
        
        if (delete)
        {
            System.out.println("\nCurrently this asset is not assigned to anyone.");
            ArrayList<ReservationRequest> hRequests = b.getReservationRequests();
            
            if(!hRequests.isEmpty())
            {
                System.out.println("\nThis asset might be on reservation requests by some clients. Deleting this asset will delete the relevant reservation requests too.");
                System.out.println("Do you still want to delete the asset? (y/n)");
                
                Scanner sc = new Scanner(System.in);
                
                while (true)
                {
                    String choice = sc.next();
                    
                    if(choice.equals("y") || choice.equals("n"))
                    {
                        if(choice.equals("n"))
                        {
                            System.out.println("\nDelete Unsuccessful.");
                            return;
                        }                            
                        else
                        {
                            //Empty the assets reservation request array
                            //Delete the reservation request from the clients too
                            for (int i = 0; i < hRequests.size() && delete; i++)
                            {
                                ReservationRequest hr = hRequests.get(i);
                                hr.getClient().removeReservationRequest(hr);
                                reservationRequestsOperations.removeReservationRequest();
                            }
                        }
                    }
                    else
                        System.out.println("Invalid Input. Enter (y/n): ");
                }
                
            }
            else
                System.out.println("This asset has no reservation requests.");
                
            assetsInRepository.remove(b);
            System.out.println("The asset is successfully removed.");
        }
        else
            System.out.println("\nDelete Unsuccessful.");
    }
    
    
    
    // Searching Assets on basis of name, Category or Creator 
    public ArrayList<DigitalAsset> searchAssets() throws IOException
    {
        String choice;
        String name = "", category = "", creator = "";
                
        Scanner sc = new Scanner(System.in);  
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        while (true)
        {
            System.out.println("\nEnter either '1' or '2' or '3' for search by Name, Category or Creator of Asset respectively: ");  
            choice = sc.next();
            
            if (choice.equals("1") || choice.equals("2") || choice.equals("3"))
                break;
            else
                System.out.println("\nWrong Input!");
        }

        if (choice.equals("1"))
        {
            System.out.println("\nEnter the Name of the Asset: ");              
            name = reader.readLine();  
        }

        else if (choice.equals("2"))
        {
            System.out.println("\nEnter the Category of the Asset: ");              
            category = reader.readLine();  
        }
        
        else
        {
            System.out.println("\nEnter the Creator of the Asset: ");              
            creator = reader.readLine();              
        }
        
        ArrayList<DigitalAsset> matchedAssets = new ArrayList();
        
        //Retrieving all the assets which matched the user's search query
        for(int i = 0; i < assetsInRepository.size(); i++)
        {
            DigitalAsset b = assetsInRepository.get(i);
            
            if (choice.equals("1"))
            { 
                if (b.getAssetName().equals(name))
                    matchedAssets.add(b);
            }
            else if (choice.equals("2"))
            { 
                if (b.getCategory().equals(category))
                    matchedAssets.add(b);
            }
            else
            {
                if (b.getCreator().equals(creator))
                    matchedAssets.add(b);                
            }
        }
        
        //Printing all the matched Assets
        if (!matchedAssets.isEmpty())
        {
            System.out.println("\nThese assets are found: \n");
                        
            System.out.println("------------------------------------------------------------------------------");            
            System.out.println("No.\t\tName\t\t\tCreator\t\t\tCategory");
            System.out.println("------------------------------------------------------------------------------");
            
            for (int i = 0; i < matchedAssets.size(); i++)
            {                      
                System.out.print(i + "-" + "\t\t");
                matchedAssets.get(i).displayInfo();
                System.out.print("\n");
            }
            
            return matchedAssets;
        }
        else
        {
            System.out.println("\nSorry. No Assets were found related to your query.");
            return null;
        }
    }
    
    
    
    // View Info of all Assets in Repository
     public void viewAllAssets()
    {
        if (!assetsInRepository.isEmpty())
        { 
            System.out.println("\nAssets are: ");
            
            System.out.println("------------------------------------------------------------------------------");            
            System.out.println("No.\t\tName\t\t\tCreator\t\t\tCategory");
            System.out.println("------------------------------------------------------------------------------");
            
            for (int i = 0; i < assetsInRepository.size(); i++)
            {                      
                System.out.print(i + "-" + "\t\t");
                assetsInRepository.get(i).displayInfo();
                System.out.print("\n");
            }
        }
        else
            System.out.println("\nCurrently, Repository has no assets.");                
    }

     
    //Computes total penalty for all assignments of a client
    public double calculatePenalty(Client client)
    {
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");            
        System.out.println("No.\t\tAsset's Name\t\tClient's Name\t\t\tAssigned Date\t\t\tReturned Date\t\t\t\tPenalty(Rs)");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------");        
        
        double totalPenalty = 0;        
        double per_assignment_penalty = 0;
        
        for (int i = 0; i < assignments.size(); i++)
        {
            Assignment l = assignments.get(i);
            
            if ((l.getBorrower() == client))
            {
                per_assignment_penalty = l.computeFine1();
                System.out.print(i + "-" + "\t\t" + assignments.get(i).getAsset().getAssetName() + "\t\t\t" + assignments.get(i).getBorrower().getName() + "\t\t" + assignments.get(i).getIssuedDate() +  "\t\t\t" + assignments.get(i).getReturnDate() + "\t\t\t\t" + per_assignment_penalty  + "\n");                
                
                totalPenalty += per_assignment_penalty;
            }            
        }
        
        return totalPenalty;
    }
    
    
    public void createUser(char x)
    {
        createPerson(x);
    }
    
    public void createPerson(char x)
    {
        Scanner sc = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
              
        System.out.println("\nEnter Name: ");
        String n = "";
        try {
            n = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(AssetRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Enter Address: ");
        String address = "";
        try {
            address = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(AssetRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int phone = 0;
        
        try{
            System.out.println("Enter Phone Number: ");
            phone = sc.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nInvalid Input.");
        }
            
        //If operator is to be created
        if (x == 'o')
        {
            double salary = 0;
            
            try{
                System.out.println("Enter Salary: ");
                salary = sc.nextDouble();
            }
            catch (java.util.InputMismatchException e)
            {
                System.out.println("\nInvalid Input.");
            }
            
            Operator c = new Operator(-1,n,address,phone,salary,-1);            
            addClerk(c);
            
            System.out.println("\nOperator with name " + n + " created successfully.");
            System.out.println("\nYour ID is : " + c.getID());
            System.out.println("Your Password is : " + c.getPassword());
        }
        
        //If manager is to be created
        else if (x == 'm')
        {
            double salary = 0;            
            try{
                System.out.println("Enter Salary: ");
                salary = sc.nextDouble();
            }
            catch (java.util.InputMismatchException e)
            {
                System.out.println("\nInvalid Input.");
            }
            
            Manager l = new Manager(-1,n,address,phone,salary,-1); 
            if(Manager.addManager(l))
            {
                System.out.println("\nManager with name " + n + " created successfully.");
                System.out.println("\nYour ID is : " + l.getID());
                System.out.println("Your Password is : " + l.getPassword());
            }
        }

        //If client is to be created
        else
        {
            Client b = new Client(-1,n,address,phone);
            addBorrower(b);            
            System.out.println("\nClient with name " + n + " created successfully.");

            System.out.println("\nYour ID is : " + b.getID());
            System.out.println("Your Password is : " + b.getPassword());            
        }        
    }
     

       
    public void createAsset(String name, String category, String creator)
    {
        DigitalAsset b = new DigitalAsset(-1,name,category,creator,false);
        
        addBookinLibrary(b);
        
        System.out.println("\nAsset with Name " + b.getAssetName() + " is successfully created.");
    }
    

    
    // Called when want an access to Portal
    public User login()
    {
        Scanner input = new Scanner(System.in);
        
        int id = 0;
        String password = "";
        
        System.out.println("\nEnter ID: ");
        
        try{
            id = input.nextInt();
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("\nInvalid Input");
        }
        
        System.out.println("Enter Password: ");
        password = input.next();
        
        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).getID() == id && users.get(i).getPassword().equals(password))
            {
                System.out.println("\nLogin Successful");
                return users.get(i);
            }
        }
        
        if(manager!=null)
        {
            if (manager.getID() == id && manager.getPassword().equals(password))
            {
                System.out.println("\nLogin Successful");
                return manager;
            }
        }
        
        System.out.println("\nSorry! Wrong ID or Password");        
        return null;
    }
    
    
    // History when an Asset was Assigned and was Returned!
    public void viewHistory()
    {
        if (!assignments.isEmpty())
        { 
            System.out.println("\nAssigned Assets are: ");
            
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");            
            System.out.println("No.\tAsset's Name\tClient's Name\t  Assigner's Name\t\tAssigned Date\t\t\tReceiver's Name\t\tReturned Date\t\tPenalty Paid");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
            
            for (int i = 0; i < assignments.size(); i++)
            {    
                if(assignments.get(i).getIssuer()!=null)
                    System.out.print(i + "-" + "\t" + assignments.get(i).getAsset().getAssetName() + "\t\t\t" + assignments.get(i).getBorrower().getName() + "\t\t" + assignments.get(i).getIssuer().getName() + "\t    " + assignments.get(i).getIssuedDate());
                
                if (assignments.get(i).getReceiver() != null)
                {
                    System.out.print("\t" + assignments.get(i).getReceiver().getName() + "\t\t" + assignments.get(i).getReturnDate() +"\t   " + assignments.get(i).getFineStatus() + "\n");
                }
                else
                    System.out.print("\t\t" + "--" + "\t\t\t" + "--" + "\t\t" + "--" + "\n");
            }
        }
        else
            System.out.println("\nNo assigned assets.");                        
    }
    
    
    
    
    
    
    
    
    
    //---------------------------------------------------------------------------------------//
    /*--------------------------------IN- COLLABORATION WITH DATA BASE------------------------------------------*/
    
    // Making Connection With Database    
    public Connection makeConnection()
    {        
        try
        {
            String host = "jdbc:derby://localhost:1527/DAM";
            String uName = "admin";
            String uPass= "admin123";
            Connection con = DriverManager.getConnection( host, uName, uPass );
            return con;
        }
        catch ( SQLException err ) 
        {
            System.out.println( err.getMessage( ) );
            return null;
        }   
    }
    
    
    // Loading all info in code via Database.
    public void populateRepository(Connection con) throws SQLException, IOException
    {       
            AssetRepository repo = this;
            Statement stmt = con.createStatement( );
            
            /* --- Populating Asset ----*/
            String SQL = "SELECT * FROM ASSET";
            ResultSet rs = stmt.executeQuery( SQL );
            
            if(!rs.next())
            {
               System.out.println("\nNo Assets Found in Repository"); 
            }
            else
            {
                int maxID = 0;
                
                do
                {
                    if(rs.getString("NAME") !=null && rs.getString("CREATOR")!=null && rs.getString("CATEGORY")!=null && rs.getInt("ID")!=0)
                    {
                        String name=rs.getString("NAME");
                        String creator=rs.getString("CREATOR");
                        String category=rs.getString("CATEGORY");
                        int id= rs.getInt("ID");
                        boolean assigned=rs.getBoolean("IS_ASSIGNED");
                        DigitalAsset b = new DigitalAsset(id,name,category,creator,assigned);
                        addBookinLibrary(b);
                        
                        if (maxID < id)
                            maxID = id;
                    }
                }while(rs.next());
                
                // setting Asset Count
                DigitalAsset.setIDCount(maxID);              
            }
            
            /* ----Populating Operators----*/
           
            SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,WORKSTATION_NO FROM PERSON INNER JOIN OPERATOR ON ID=O_ID INNER JOIN EMPLOYEE ON E_ID=O_ID";
            
            rs=stmt.executeQuery(SQL);
                      
            if(!rs.next())
            {
               System.out.println("No operators Found in Repository"); 
            }
            else
            {
                do
                {
                    int id=rs.getInt("ID");
                    String cname=rs.getString("PNAME");
                    String adrs=rs.getString("ADDRESS"); 
                    int phn=rs.getInt("PHONE_NO");
                    double sal=rs.getDouble("SALARY");
                    int workstation=rs.getInt("WORKSTATION_NO");
                    Operator c = new Operator(id,cname,adrs,phn,sal,workstation);
                    
                    addClerk(c);
                }
                while(rs.next());
                                
            }
            
            /*-----Populating Manager---*/
            SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO,SALARY,OFFICE_NO FROM PERSON INNER JOIN MANAGER ON ID=M_ID INNER JOIN EMPLOYEE ON E_ID=M_ID";
            
            rs=stmt.executeQuery(SQL);
            if(!rs.next())
            {
               System.out.println("No Manager Found in Repository"); 
            }
            else
            {
                do
                {
                    int id=rs.getInt("ID");
                    String lname=rs.getString("PNAME");
                    String adrs=rs.getString("ADDRESS"); 
                    int phn=rs.getInt("PHONE_NO");
                    double sal=rs.getDouble("SALARY");
                    int off=rs.getInt("OFFICE_NO");
                    Manager l= new Manager(id,lname,adrs,phn,sal,off);

                    Manager.addManager(l);
                    
                }while(rs.next());
           
            }
                                    
            /*---Populating Clients (partially)!!!!!!--------*/
            
            SQL="SELECT ID,PNAME,ADDRESS,PASSWORD,PHONE_NO FROM PERSON INNER JOIN CLIENT ON ID=C_ID";
            
            rs=stmt.executeQuery(SQL);
                      
            if(!rs.next())
            {
               System.out.println("No Client Found in Repository"); 
            }
            else
            {
                do
                {
                        int id=rs.getInt("ID");
                        String name=rs.getString("PNAME");
                        String adrs=rs.getString("ADDRESS"); 
                        int phn=rs.getInt("PHONE_NO"); 
                        
                        Client b= new Client(id,name,adrs,phn);
                        addBorrower(b);
                                                
                }while(rs.next());
                                
            }
            
            /*----Populating Assignment----*/
            
            SQL="SELECT * FROM ASSIGNMENT";
            
            rs=stmt.executeQuery(SQL);
            if(!rs.next())
            {
               System.out.println("No Assets Assigned Yet!"); 
            }
            else
            {
                do
                    {
                        int cliid=rs.getInt("CLIENT");
                        int assetid=rs.getInt("ASSET");
                        int assignerid=rs.getInt("ASSIGNER");
                        Integer receiverid=(Integer)rs.getObject("RECEIVER");
                        int rd=0;
                        Date rdate;
                        
                        Date assigneddate=new Date (rs.getTimestamp("ASSIGNED_DATE").getTime());
                        
                        if(receiverid!=null)    // if there is a receiver 
                        {
                            rdate=new Date (rs.getTimestamp("RETURNED_DATE").getTime()); 
                            rd=(int)receiverid;
                        }
                        else
                        {
                            rdate=null;
                        }
                        
                        boolean penaltyStatus = rs.getBoolean("PENALTY_PAID");
                        
                        boolean set=true;
                        
                        Client bb = null;
                       
                        
                        for(int i=0;i<getPersons().size() && set;i++)
                        {
                            if(getPersons().get(i).getID()==cliid)
                            {
                                set=false;
                                bb=(Client)(getPersons().get(i));
                            }
                        }
                        
                        set =true;
                        Employee s[]=new Employee[2];
                        
                        if(assignerid==getLibrarian().getID())
                        {
                            s[0]=getLibrarian();
                        }
                            
                        else
                        {                                
                            for(int k=0;k<getPersons().size() && set;k++)
                            {
                                if(getPersons().get(k).getID()==assignerid && getPersons().get(k).getClass().getSimpleName().equals("Operator"))
                                {
                                    set=false;
                                    s[0]=(Operator)(getPersons().get(k));
                                }
                            }
                        }       
                        
                        set=true;
                        // If not returned yet...
                        if(receiverid==null)
                        {
                            s[1]=null;  // no reciever 
                            rdate=null;      
                        }
                        else
                        {
                            if(rd==getLibrarian().getID())
                                s[1]=getLibrarian();

                            else
                            {    //System.out.println("ff");
                                 for(int k=0;k<getPersons().size() && set;k++)
                                {
                                    if(getPersons().get(k).getID()==rd && getPersons().get(k).getClass().getSimpleName().equals("Operator"))
                                    {
                                        set=false;
                                        s[1]=(Operator)(getPersons().get(k));
                                    }
                                }
                            }     
                        }
                        
                        set=true;
                        
                        ArrayList<DigitalAsset> assets = getBooks();
                        
                        for(int k=0;k<assets.size() && set;k++)
                        {
                            if(assets.get(k).getID()==assetid)
                            {
                              set=false;   
                              Assignment l = new Assignment(bb,assets.get(k),s[0],s[1],assigneddate,rdate,penaltyStatus);
                              assignments.add(l);
                            }
                        }
                        
                    }while(rs.next());
            }
            
            /*----Populationg Reserved Assets----*/
            
            SQL="SELECT * FROM ON_RESERVATION_ASSET";
            
            rs=stmt.executeQuery(SQL);
            if(!rs.next())
            {
               System.out.println("No Assets on Reservation Yet!"); 
            }
            else
            {
                do
                    {
                        int cliid=rs.getInt("CLIENT");
                        int assetid=rs.getInt("ASSET");
                        Date reqdate=new Date (rs.getDate("REQ_DATE").getTime());
                        
                        boolean set=true;
                        Client bb =null;
                        
                        ArrayList<User> persons = repo.getPersons();
                        
                        for(int i=0;i<persons.size() && set;i++)
                        {
                            if(persons.get(i).getID()==cliid)
                            {
                                set=false;
                                bb=(Client)(persons.get(i));
                            }
                        }
                                              
                        set=true;
                        
                        ArrayList<DigitalAsset> assets = repo.getBooks();
                        
                        for(int i=0;i<assets.size() && set;i++)
                        {
                            if(assets.get(i).getID()==assetid)
                            {
                              set=false;   
                              ReservationRequest hasset= new ReservationRequest(bb,assets.get(i),reqdate);
                             reservationRequestsOperations.addReservationRequest(hasset);
                             bb.addReservationRequest(hasset);
                            }
                        }
                        }while(rs.next());
            }
            
            /* --- Populating Client's Remaining Info----*/
            
            // Assigned Assets
            SQL="SELECT ID,ASSET FROM PERSON INNER JOIN CLIENT ON ID=C_ID INNER JOIN ASSIGNED_ASSET ON C_ID=CLIENT ";
            
            rs=stmt.executeQuery(SQL);
                      
            if(!rs.next())
            {
               System.out.println("No Client has been assigned yet from Repository"); 
            }
            else
            {
                
                do
                    {
                        int id=rs.getInt("ID");      // client
                        int assetid=rs.getInt("ASSET");   // asset
                        
                        Client bb=null;
                        boolean set=true;
                        boolean okay=true;
                        
                        for(int i=0;i<repo.getPersons().size() && set;i++)
                        {
                            if(repo.getPersons().get(i).getClass().getSimpleName().equals("Client"))
                            {
                                if(repo.getPersons().get(i).getID()==id)
                                {
                                   set =false;
                                    bb=(Client)(repo.getPersons().get(i));
                                }
                            }
                        }
                        
                        set=true;
                        
                        ArrayList<Assignment> assets = assignments;
                        
                        for(int i=0;i<assets.size() && set;i++)
                        {
                            if(assets.get(i).getAsset().getID()==assetid &&assets.get(i).getReceiver()==null )
                            {
                              set=false;   
                              Assignment bAsset= new Assignment(bb,assets.get(i).getAsset(),assets.get(i).getIssuer(),null,assets.get(i).getIssuedDate(),null,assets.get(i).getFineStatus());
                              bb.addAssignedAsset(bAsset);
                            }
                        }
                                 
                    }while(rs.next());               
            }
                      
            ArrayList<User> persons = repo.getPersons();
            
            /* Setting Person ID Count */
            int max=0;
            
            for(int i=0;i<persons.size();i++)
            {
                if (max < persons.get(i).getID())
                    max=persons.get(i).getID();
            }

            User.setIDCount(max);  
    }
    
    
    // Filling Changes back to Database
    public void saveToDatabase(Connection con) throws SQLException,SQLIntegrityConstraintViolationException
    {
            /*-----------Assignment Table Cleared------------*/
            
            String template = "DELETE FROM DAM.ASSIGNMENT";
            PreparedStatement stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                        
            /*-----------Assigned Assets Table Cleared------------*/
            
            template = "DELETE FROM DAM.ASSIGNED_ASSET";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                       
            /*-----------OnReservationAssets Table Cleared------------*/
            
            template = "DELETE FROM DAM.ON_RESERVATION_ASSET";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Assets Table Cleared------------*/
            
            template = "DELETE FROM DAM.ASSET";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                       
            /*-----------Operator Table Cleared------------*/
            
            template = "DELETE FROM DAM.OPERATOR";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Manager Table Cleared------------*/
            
            template = "DELETE FROM DAM.MANAGER";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
                       
            /*-----------Client Table Cleared------------*/
            
            template = "DELETE FROM DAM.CLIENT";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Employee Table Cleared------------*/
            
            template = "DELETE FROM DAM.EMPLOYEE";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
            
            /*-----------Person Table Cleared------------*/
            
            template = "DELETE FROM DAM.PERSON";
            stmts = con.prepareStatement(template);
            
            stmts.executeUpdate();
           
            AssetRepository repo = this;
            
        /* Filling Person's Table*/
        for(int i=0;i<repo.getPersons().size();i++)
        {
            template = "INSERT INTO DAM.PERSON (ID,PNAME,PASSWORD,ADDRESS,PHONE_NO) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1, repo.getPersons().get(i).getID());
            stmt.setString(2, repo.getPersons().get(i).getName());
            stmt.setString(3,  repo.getPersons().get(i).getPassword());
            stmt.setString(4, repo.getPersons().get(i).getAddress());
            stmt.setInt(5, repo.getPersons().get(i).getPhoneNumber());
            
            stmt.executeUpdate();
        }
        
        /* Filling Operator's Table and Employee Table*/
        for(int i=0;i<repo.getPersons().size();i++)
        {
            if (repo.getPersons().get(i).getClass().getSimpleName().equals("Operator"))
            {
                template = "INSERT INTO DAM.EMPLOYEE (E_ID,TYPE,SALARY) values (?,?,?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1,repo.getPersons().get(i).getID());
                stmt.setString(2,"Operator");
                stmt.setDouble(3, ((Operator)(repo.getPersons().get(i))).getSalary());

                stmt.executeUpdate();

                template = "INSERT INTO DAM.OPERATOR (O_ID,WORKSTATION_NO) values (?,?)";
                stmt = con.prepareStatement(template);

                stmt.setInt(1,repo.getPersons().get(i).getID());
                stmt.setInt(2, ((Operator)(repo.getPersons().get(i))).workstationNo);

                stmt.executeUpdate();
            }
        
        }
        
        if(repo.getLibrarian()!=null)    // if  manager is there
            {
            template = "INSERT INTO DAM.EMPLOYEE (E_ID,TYPE,SALARY) values (?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
             
            stmt.setInt(1, repo.getLibrarian().getID());
            stmt.setString(2,"Manager");
            stmt.setDouble(3,repo.getLibrarian().getSalary());
            
            stmt.executeUpdate();
            
            template = "INSERT INTO DAM.MANAGER (M_ID,OFFICE_NO) values (?,?)";
            stmt = con.prepareStatement(template);
            
            stmt.setInt(1,repo.getLibrarian().getID());
            stmt.setInt(2, repo.getLibrarian().officeNo);
            
            stmt.executeUpdate();  
            }
        
        /* Filling Client's Table*/
        for(int i=0;i<repo.getPersons().size();i++)
        {
            if (repo.getPersons().get(i).getClass().getSimpleName().equals("Client"))
            {
                template = "INSERT INTO DAM.CLIENT(C_ID) values (?)";
                PreparedStatement stmt = con.prepareStatement(template);

                stmt.setInt(1, repo.getPersons().get(i).getID());

                stmt.executeUpdate();    
            }
        }
                       
        ArrayList<DigitalAsset> assets = repo.getBooks();
        
        /*Filling Asset's Table*/
        for(int i=0;i<assets.size();i++)
        {
            template = "INSERT INTO DAM.ASSET (ID,NAME,CREATOR,CATEGORY,IS_ASSIGNED) values (?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1,assets.get(i).getID());
            stmt.setString(2,assets.get(i).getAssetName());
            stmt.setString(3, assets.get(i).getCreator());
            stmt.setString(4, assets.get(i).getCategory());
            stmt.setBoolean(5, assets.get(i).getAssignedStatus());
            stmt.executeUpdate();
            
        }
         
        /* Filling Assignment Table*/
        for(int i=0;i<assignments.size();i++)
        {
            template = "INSERT INTO DAM.ASSIGNMENT(A_ID,CLIENT,ASSET,ASSIGNER,ASSIGNED_DATE,RECEIVER,RETURNED_DATE,PENALTY_PAID) values (?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1,i+1);
            stmt.setInt(2,assignments.get(i).getBorrower().getID());
            stmt.setInt(3,assignments.get(i).getAsset().getID());
            stmt.setInt(4,assignments.get(i).getIssuer().getID());
            stmt.setTimestamp(5,new java.sql.Timestamp(assignments.get(i).getIssuedDate().getTime()));
            stmt.setBoolean(8,assignments.get(i).getFineStatus());
            if(assignments.get(i).getReceiver()==null)
            {
                stmt.setNull(6,Types.INTEGER); 
                stmt.setDate(7,null);
            }
            else
            {
                stmt.setInt(6,assignments.get(i).getReceiver().getID());  
                stmt.setTimestamp(7,new java.sql.Timestamp(assignments.get(i).getReturnDate().getTime()));
            }
                
            stmt.executeUpdate(); 
   
        }
       
        /* Filling On_Reservation_ Table*/
        
        int x=1;
        for(int i=0;i<repo.getBooks().size();i++)
        {
            for(int j=0;j<repo.getBooks().get(i).getReservationRequests().size();j++)
            {
            template = "INSERT INTO DAM.ON_RESERVATION_ASSET(REQ_ID,ASSET,CLIENT,REQ_DATE) values (?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(template);
            
            stmt.setInt(1,x);
            stmt.setInt(3,repo.getBooks().get(i).getReservationRequests().get(j).getClient().getID());
            stmt.setInt(2,repo.getBooks().get(i).getReservationRequests().get(j).getAsset().getID());
            stmt.setDate(4,new java.sql.Date(repo.getBooks().get(i).getReservationRequests().get(j).getRequestDate().getTime()));
                    
            stmt.executeUpdate(); 
            x++;
            
            }
        }
            
        /* Filling Assigned Asset Table*/
        for(int i=0;i<repo.getBooks().size();i++)
          {
              if(repo.getBooks().get(i).getAssignedStatus()==true)
              {
                  boolean set=true;
                  for(int j=0;j<assignments.size() && set ;j++)
                  {
                      if(repo.getBooks().get(i).getID()==assignments.get(j).getAsset().getID())
                      {
                          if(assignments.get(j).getReceiver()==null)
                          {
                            template = "INSERT INTO DAM.ASSIGNED_ASSET(ASSET,CLIENT) values (?,?)";
                            PreparedStatement stmt = con.prepareStatement(template);
                            stmt.setInt(1,assignments.get(j).getAsset().getID());
                            stmt.setInt(2,assignments.get(j).getBorrower().getID());
                  
                            stmt.executeUpdate();
                            set=false;
                          }
                      }
                      
                  }
                  
              }
          }   
    } // Filling Done!  
    
    
    
    
    
    
}   // AssetRepository Class Closed
