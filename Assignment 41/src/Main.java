import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Bank {
    private String name;
    private List<Branch> branches;

    public Bank(String name) {
        this.name = name;
        this.branches = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void addBranch(Branch branch) {
        branches.add(branch);
        saveData();
    }

    public Deposit findDepositByDepositorName(String depositorName) {
        for (Branch branch : branches) {
            for (Deposit deposit : branch.getDeposits()) {
                if (deposit.getDepositorName().equalsIgnoreCase(depositorName)) {
                    return deposit;
                }
            }
        }
        return null;
    }

    public void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bank_data.txt"))) {
            writer.write("Bank: " + name + "\n");
            for (Branch branch : branches) {
                writer.write("Branch: " + branch.getName() + "\n");
                for (Deposit deposit : branch.getDeposits()) {
                    writer.write("Deposit: " + deposit.getDepositorName() + ", " + deposit.getDepositAmount() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}

class Branch {
    private String name;
    private Bank bank;
    private List<Deposit> deposits;

    public Branch(String name, Bank bank) {
        this.name = name;
        this.bank = bank;
        this.deposits = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Bank getBank() {
        return bank;
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public void addDeposit(Deposit deposit) {
        deposits.add(deposit);
        bank.saveData();
    }

    public void removeDeposit(Deposit deposit) {
        deposits.remove(deposit);
        bank.saveData();
    }

    public double getTotalDepositAmount() {
        double total = 0;
        for (Deposit deposit : deposits) {
            total += deposit.getDepositAmount();
        }
        return total;
    }
}

class Deposit {
    private String depositorName;
    private double depositAmount;
    private Branch branch;

    public Deposit(String depositorName, Branch branch, double initialAmount) {
        this.depositorName = depositorName;
        this.branch = branch;
        this.depositAmount = initialAmount;
    }

    public String getDepositorName() {
        return depositorName;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public Branch getBranch() {
        return branch;
    }

    public void replenishAccount(double amount) {
        if (amount > 0) {
            this.depositAmount += amount;
            branch.getBank().saveData();
        }
    }
}

public class Main {
    private static final String USERNAME = "Xun_Mao";
    private static final String PASSWORD = "12345";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Bank System");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (USERNAME.equals(username) && PASSWORD.equals(password)) {
            Bank bank = new Bank("Global Bank");

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Add Branch");
                System.out.println("2. Add Deposit");
                System.out.println("3. View Branch Deposits");
                System.out.println("4. Replenish Deposit");
                System.out.println("5. Find Deposit by Depositor Name");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter branch name: ");
                        String branchName = scanner.nextLine();
                        Branch branch = new Branch(branchName, bank);
                        bank.addBranch(branch);
                        System.out.println("Branch added successfully.");
                        break;

                    case 2:
                        System.out.println("Available branches:");
                        for (Branch b : bank.getBranches()) {
                            System.out.println("- " + b.getName());
                        }
                        System.out.print("Enter branch name to add deposit: ");
                        String branchForDeposit = scanner.nextLine();
                        Branch selectedBranch = null;
                        for (Branch b : bank.getBranches()) {
                            if (b.getName().equalsIgnoreCase(branchForDeposit)) {
                                selectedBranch = b;
                                break;
                            }
                        }
                        if (selectedBranch != null) {
                            System.out.print("Enter depositor name: ");
                            String depositorName = scanner.nextLine();
                            System.out.print("Enter deposit amount: ");
                            double amount = scanner.nextDouble();
                            Deposit deposit = new Deposit(depositorName, selectedBranch, amount);
                            selectedBranch.addDeposit(deposit);
                            System.out.println("Deposit added successfully.");
                        } else {
                            System.out.println("Branch not found.");
                        }
                        break;

                    case 3:
                        System.out.println("Available branches:");
                        for (Branch b : bank.getBranches()) {
                            System.out.println("- " + b.getName());
                        }
                        System.out.print("Enter branch name to view deposits: ");
                        String branchToView = scanner.nextLine();
                        for (Branch b : bank.getBranches()) {
                            if (b.getName().equalsIgnoreCase(branchToView)) {
                                System.out.println("Deposits in " + b.getName() + " Branch:");
                                for (Deposit d : b.getDeposits()) {
                                    System.out.println(d.getDepositorName() + ": $" + d.getDepositAmount());
                                }
                                System.out.println("Total Deposit Amount: $" + b.getTotalDepositAmount());
                            }
                        }
                        break;

                    case 4:
                        System.out.print("Enter depositor name to replenish account: ");
                        String depositorToReplenish = scanner.nextLine();
                        Deposit depositToReplenish = bank.findDepositByDepositorName(depositorToReplenish);
                        if (depositToReplenish != null) {
                            System.out.print("Enter amount to add: ");
                            double addAmount = scanner.nextDouble();
                            depositToReplenish.replenishAccount(addAmount);
                            System.out.println("Account replenished successfully.");
                        } else {
                            System.out.println("Depositor not found.");
                        }
                        break;

                    case 5:
                        System.out.print("Enter depositor name to search: ");
                        String searchName = scanner.nextLine();
                        Deposit foundDeposit = bank.findDepositByDepositorName(searchName);
                        if (foundDeposit != null) {
                            System.out.println("Found Deposit:");
                            System.out.println("Depositor: " + foundDeposit.getDepositorName());
                            System.out.println("Amount: $" + foundDeposit.getDepositAmount());
                            System.out.println("Branch: " + foundDeposit.getBranch().getName());
                        } else {
                            System.out.println("Deposit not found.");
                        }
                        break;

                    case 6:
                        System.out.println("Exiting system. Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        } else {
            System.out.println("Invalid credentials. Access denied.");
        }
    }
}
