package DAM;

public abstract class Employee extends User 
{   
    protected double salary;        // Salary of every employee related to repository
    
    public Employee(int idNum, String name, String address, int phoneNum, double sal)   // para cons.
    {
        super(idNum, name, address, phoneNum);
        salary = sal;
    }        
    
    // Printing Info of an Employee
    @Override
    public void displayInfo()
    {
        super.displayInfo();
        System.out.println("Salary: " + salary);
    }
    
    /*-------Getter FUNCs.--------*/
    public double getSalary()
    {
        return salary;
    }
    /*---------------------------*/
   
} // Employee Class Closed
