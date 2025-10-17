package DAM;

public abstract class User 
{   
    protected int userId;           // ID of every user related to repository
    protected String userPassword;  // Password of every user related to repository
    protected String userName;      // Name of every user related to repository
    protected String userAddress;   // Address of every user related to repository
    protected int userPhone;      // PhoneNo of every user related to repository
    
    static int currentUserIdNumber = 0;     //This will be unique for every user, since it will be incremented when everytime
                                       //when a user is created

    public User(int idNum, String name, String address, int phoneNum)   // para cons.
    {
        currentUserIdNumber++;
        
        if(idNum==-1)
        {
            userId = currentUserIdNumber;
        }
        else
            userId = idNum;
        
        userPassword = Integer.toString(userId);
        this.userName = name;
        this.userAddress = address;
        userPhone = phoneNum;
    }        
    
    // Printing Info of a User
    public void displayInfo()
    {
        System.out.println("-----------------------------------------");
        System.out.println("\nThe details are: \n");
        System.out.println("ID: " + userId);
        System.out.println("Name: " + userName);
        System.out.println("Address: " + userAddress);
        System.out.println("Phone No: " + userPhone + "\n");
    }
    
    /*---------Setter FUNCs.---------*/
    public void setAddress(String a)
    {
        userAddress = a;
    }
    
    public void setPhone(int p)
    {
        userPhone = p;
    }
    
    public void setName(String n)
    {
        userName = n;
    }
    /*----------------------------*/
    
    /*-------Getter FUNCs.--------*/
    public String getName()
    {
        return userName;
    }
    
    public String getPassword()
    {
        return userPassword;
    }
    
     public String getAddress()
    {
        return userAddress;
    }
     
     public int getPhoneNumber()
    {
        return userPhone;
    }
    public int getID()
    {
        return userId;
    }
    /*---------------------------*/
    
     public static void setIDCount(int n)
    {
        currentUserIdNumber=n;
    }
   
} // User Class Closed
