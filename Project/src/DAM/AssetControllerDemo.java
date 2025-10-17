package DAM;

// Including Header Files.
import java.io.*;
import java.util.*;
import java.sql.*;

public class AssetControllerDemo 
{
    // Clearing Required Area of Screen
    public static void clearScreen()
    {
        for (int i = 0; i < 20; i++)
            System.out.println();
    }

    // Asking for Input as Choice
    public static int getUserChoice(int min, int max)
    {    
        String choice;
        Scanner input = new Scanner(System.in);        
        
        while(true)
        {
            System.out.println("\nEnter Choice: ");

            choice = input.next();

            if((!choice.matches(".*[a-zA-Z]+.*")) && (Integer.parseInt(choice) > min && Integer.parseInt(choice) < max))
            {
                return Integer.parseInt(choice);
            }
            
            else
                System.out.println("\nInvalid Input.");
        }
          
    }

    // Functionalities of all Users
    public static void executeUserFunction(User user, int choice) throws IOException
    {
        AssetRepository repo = AssetRepository.getInstance();
        
        Scanner scanner = new Scanner(System.in);
        int input = 0;
        
        //Search Asset
        if (choice == 1)
        {
            repo.searchAssets();
        }
        
        //Do Reservation Request
        else if (choice == 2)
        {
            ArrayList<DigitalAsset> assets = repo.searchAssets();
            
            if (assets != null)
            {
                input = getUserChoice(-1,assets.size());
                
                DigitalAsset a = assets.get(input);
                
                if("Operator".equals(user.getClass().getSimpleName()) || "Manager".equals(user.getClass().getSimpleName()))
                {                
                    Client cli = repo.findClient();

                    if (cli != null)
                        a.createReservationRequest(cli);
                }
                else                
                    a.createReservationRequest((Client)user);
            }
        }
        
        //View client's personal information
        else if (choice == 3)
        {
            if("Operator".equals(user.getClass().getSimpleName()) || "Manager".equals(user.getClass().getSimpleName()))
            {
                Client cli = repo.findClient();
                
                if(cli!=null)
                    cli.displayInfo();
            }
            else
                user.displayInfo();
        }
        
        //Compute Penalty of a Client
        else if (choice == 4)
        {
            if("Operator".equals(user.getClass().getSimpleName()) || "Manager".equals(user.getClass().getSimpleName()))
            {
                Client cli = repo.findClient();
                
                if(cli!=null)
                {
                    double totalPenalty = repo.calculatePenalty(cli);
                    System.out.println("\nYour Total Penalty is : Rs " + totalPenalty );                     
                }
            }
            else
            {
                double totalPenalty = repo.calculatePenalty((Client)user);
                System.out.println("\nYour Total Penalty is : Rs " + totalPenalty );                 
            }
        }
        
        //Check reservation request queue of an asset
        else if (choice == 5)
        {
            ArrayList<DigitalAsset> assets = repo.searchAssets();
            
            if (assets != null)
            {
                input = getUserChoice(-1,assets.size());
                assets.get(input).displayReservationRequests();
            }
        }
                       
        //Assign an Asset
        else if (choice == 6)
        {
            ArrayList<DigitalAsset> assets = repo.searchAssets();

            if (assets != null)
            {
                input = getUserChoice(-1,assets.size());
                DigitalAsset a = assets.get(input);
                
                Client cli = repo.findClient();

                if(cli!=null)
                {
                    a.assignAsset(cli, (Employee)user);            
                }
            }
        }        

        //Return an Asset
        else if (choice == 7)
        {
            Client cli = repo.findClient();

            if(cli!=null)
            {
                cli.displayAssignedAssets();
                ArrayList<Assignment> assignments = cli.getAssignedAssets();
                
                if (!assignments.isEmpty())
                {
                    input = getUserChoice(-1,assignments.size());
                    Assignment ass = assignments.get(input);
                    
                    ass.getAsset().returnAsset(cli, ass, (Employee)user);            
                }
                else
                    System.out.println("\nThis client " + cli.getName() + " has no asset to return.");
            }
        }        

        //Extend an Asset Assignment
        else if (choice == 8)
        {
            Client cli = repo.findClient();

            if(cli!=null)
            {
                cli.displayAssignedAssets();
                ArrayList<Assignment> assignments = cli.getAssignedAssets();
                
                if (!assignments.isEmpty())
                {
                    input = getUserChoice(-1,assignments.size());
 
                    assignments.get(input).extendAssignment(new java.util.Date()); 
                }
                else
                    System.out.println("\nThis client " + cli.getName() + " has no assigned asset which can be extended.");                    
            }
        }        

        //Add new Client
        else if (choice == 9)
        {
            repo.createUser('c');
        }        

        //Update Client's Personal Info
        else if (choice == 10)
        {
            Client cli = repo.findClient();
            
            if(cli != null)
                cli.updateClientInfo();
        }        
        
        //Add new Asset
        else if (choice == 11)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("\nEnter Asset Name:");
            String name = reader.readLine();

            System.out.println("\nEnter Category:");
            String category = reader.readLine();

            System.out.println("\nEnter Creator:");
            String creator = reader.readLine();
            
            repo.createAsset(name, category, creator);
        }        
        
        //Remove an Asset
        else if (choice == 12)
        {
            ArrayList<DigitalAsset> assets = repo.searchAssets();
            
            if (assets != null)
            {
                input = getUserChoice(-1,assets.size());
            
                repo.removeAssetFromRepository(assets.get(input));
            }
        }        

        //Change an Asset's Info
        else if (choice == 13)
        {
            ArrayList<DigitalAsset> assets = repo.searchAssets();
            
            if (assets!=null)
            {
                input = getUserChoice(-1,assets.size());
            
                assets.get(input).modifyAssetInfo();
            }
        }        
            
        //View operator's personal information
        else if (choice == 14)
        {
            Operator op = repo.findOperator();

            if(op!=null)
                op.displayInfo();
        }
        
        // Functionality Performed.
        System.out.println("\nPress any key to continue..\n");
        scanner.next();
    }
    
    
    
    
   
    
    /*-------------------------------------MAIN---------------------------------------------------*/
    
    public static void main(String[] args)
    {
        Scanner admin = new Scanner(System.in);
        
        //-------------------INTERFACE---------------------------//
        
        AssetRepository repo = AssetRepository.getInstance();
        
        // Setting some by default information like name of repository, penalty, deadline and limit of reservation request
        repo.setPenalty(20);
        repo.setRequestExpiry(7);
        repo.setReturnDeadline(5);
        repo.setName("TechCorp Digital Repository");
        
        // Add some sample data for demonstration
        repo.createAsset("Digital Photography Guide", "Technology", "John Smith");
        repo.createAsset("Web Development Basics", "Programming", "Jane Doe");
        repo.createAsset("Data Structures", "Computer Science", "Bob Johnson");
        
        repo.createUser('c'); // Create a sample client
        
        try {

        boolean stop = false;
        while(!stop)
        {   
            clearScreen();

            // FRONT END //
            System.out.println("--------------------------------------------------------");
            System.out.println("\tWelcome to Digital Asset Management System");
            System.out.println("--------------------------------------------------------");
            
            System.out.println("Following Functionalities are available: \n");
            System.out.println("1- Login");
            System.out.println("2- Exit");
            System.out.println("3- Administrative Functions"); // Administration has access only 
            
            System.out.println("-----------------------------------------\n");        
            
            int choice = 0;

            choice = getUserChoice(0,4);
            
            if (choice == 3)
            {                   
                System.out.println("\nEnter Password: ");
                String aPass = admin.next();
                
                if(aPass.equals("admin"))
                {
                    while (true)    // Way to Admin Portal
                    {
                        clearScreen();

                        System.out.println("--------------------------------------------------------");
                        System.out.println("\tWelcome to Admin's Portal");
                        System.out.println("--------------------------------------------------------");
                        System.out.println("Following Functionalities are available: \n");

                        System.out.println("1- Add Operator");
                        System.out.println("2- Add Manager"); 
                        System.out.println("3- View Assignment History");  
                        System.out.println("4- View All Assets in Repository"); 
                        System.out.println("5- Logout"); 

                        System.out.println("---------------------------------------------");

                        choice = getUserChoice(0,6);

                        if (choice == 5)
                            break;

                        if (choice == 1)
                            repo.createUser('o');
                        else if (choice == 2)
                            repo.createUser('m');

                        else if (choice == 3)
                            repo.viewHistory();

                        else if (choice == 4)
                            repo.viewAllAssets();
                        
                        System.out.println("\nPress any key to continue..\n");
                        admin.next();                        
                    }
                }
                else
                    System.out.println("\nSorry! Wrong Password.");
            }
 
            else if (choice == 1)
            {
                User user = repo.login();

                if (user == null){}
                
                else if (user.getClass().getSimpleName().equals("Client"))
                {                    
                    while (true)    // Way to Client's Portal
                    {
                        clearScreen();
                                        
                        System.out.println("--------------------------------------------------------");
                        System.out.println("\tWelcome to Client's Portal");
                        System.out.println("--------------------------------------------------------");
                        System.out.println("Following Functionalities are available: \n");
                        System.out.println("1- Search an Asset");
                        System.out.println("2- Place an Asset on reservation");
                        System.out.println("3- Check Personal Info of Client");
                        System.out.println("4- Check Total Penalty of Client"); 
                        System.out.println("5- Check Reservation Requests Queue of an Asset");                         
                        System.out.println("6- Logout");
                        System.out.println("--------------------------------------------------------");
                        
                        choice = getUserChoice(0,7);

                        if (choice == 6)
                            break;
                        
                        executeUserFunction(user,choice);
                    }
                }
                
                else if (user.getClass().getSimpleName().equals("Operator"))
                {
                    while(true) // Way to Operator's Portal
                    {
                        clearScreen();
                                        
                        System.out.println("--------------------------------------------------------");
                        System.out.println("\tWelcome to Operator's Portal");
                        System.out.println("--------------------------------------------------------");
                        System.out.println("Following Functionalities are available: \n");
                        System.out.println("1- Search an Asset");
                        System.out.println("2- Place an Asset on reservation");
                        System.out.println("3- Check Personal Info of Client");
                        System.out.println("4- Check Total Penalty of Client");               
                        System.out.println("5- Check Reservation Requests Queue of an Asset");                        
                        System.out.println("6- Assign an Asset");
                        System.out.println("7- Return an Asset");                        
                        System.out.println("8- Extend an Asset Assignment");
                        System.out.println("9- Add a new Client");
                        System.out.println("10- Update a Client's Info");
                        System.out.println("11- Logout");
                        System.out.println("--------------------------------------------------------");                    
                        
                        choice = getUserChoice(0,12);

                        if (choice == 11)
                            break;
                                            
                        executeUserFunction(user,choice);                        
                    }                    
                }
                
                else if (user.getClass().getSimpleName().equals("Manager"))
                {
                    while(true) // Way to Manager Portal
                    {
                        clearScreen();
                                        
                        System.out.println("--------------------------------------------------------");
                        System.out.println("\tWelcome to Manager's Portal");
                        System.out.println("--------------------------------------------------------");
                        System.out.println("Following Functionalities are available: \n");
                        System.out.println("1- Search an Asset");
                        System.out.println("2- Place an Asset on reservation");
                        System.out.println("3- Check Personal Info of Client");
                        System.out.println("4- Check Total Penalty of Client");      
                        System.out.println("5- Check Reservation Requests Queue of an Asset");                        
                        System.out.println("6- Assign an Asset");
                        System.out.println("7- Return an Asset");                        
                        System.out.println("8- Extend an Asset Assignment");
                        System.out.println("9- Add a new Client");
                        System.out.println("10- Update a Client's Info");
                        System.out.println("11- Add new Asset");
                        System.out.println("12- Remove an Asset");
                        System.out.println("13- Change an Asset's Info");
                        System.out.println("14- Check Personal Info of Operator");                        
                        System.out.println("15- Logout");
                        System.out.println("--------------------------------------------------------");
                        
                        choice = getUserChoice(0,16);

                        if (choice == 15)
                            break;
                                               
                        executeUserFunction(user,choice);                        
                    }                    
                }
                
            }

            else
                stop = true;

            System.out.println("\nPress any key to continue..\n");
            Scanner scanner = new Scanner(System.in);
            scanner.next();            
        }
        
        }
        catch(Exception e)
        {
            System.out.println("\nExiting...\n");
        }   // System Closed!
       
    }    // Main Closed
    
}   // Class closed.
