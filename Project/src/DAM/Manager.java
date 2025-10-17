package DAM;

public class Manager extends Employee {

    int officeNo;     //Office Number of the Manager
    public static int currentOfficeNumber = 0;

    public Manager(int id, String n, String a, int p, double s, int of) // para cons.
    {
        super(id, n, a, p, s);

        if (of == -1)
            officeNo = currentOfficeNumber;
        else
            officeNo = of;

        currentOfficeNumber++;
    }

    // Printing Manager's Info
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Office Number: " + officeNo);
    }

    public static boolean addManager(Manager mgr) {
        //One Repository can have only one Manager
        AssetRepository repo = AssetRepository.getInstance();
        if (repo.manager == null) {
            repo.manager = mgr;
            repo.users.add(mgr);
            return true;
        } else
            System.out.println("\nSorry, the repository already has one manager. New Manager can't be created.");
        return false;
    }
}
