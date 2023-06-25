package budget;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner;
    private final Plan plan;

    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
        this.plan = new Plan();
    }

    public void boot() {
        while (true) {
            this.displayMenu();
            int userAction = this.scanner.nextInt();

            if (userAction == 0) {
                System.out.println("\nBye!");
                break;
            }

            this.processUserInput(userAction);
            System.out.println();
        }
    }

    private void processUserInput(int userAction) {
        switch (userAction) {
            case 1 -> this.addIncome();
            case 2 -> this.addPurchase();
            case 3 -> this.showPurchaseListByCategory();
            case 4 -> this.getBalance();
            case 5 -> this.savePurchasesToFile();
            case 6 -> this.loadPurchasesFromFile();
            case 7 -> this.analyzePurchases();
            default -> System.out.println("Invalid action.");
        }
    }

    private void loadPurchasesFromFile() {
        boolean wasPurchasesLoaded = this.plan.loadPurchasesFromFile();
        if (wasPurchasesLoaded) {
            System.out.println("\nPurchases were loaded!");
        }
    }

    private void savePurchasesToFile() {
        boolean wasPurchasesSaved = this.plan.savePurchasesToFile();
        if (wasPurchasesSaved) {
            System.out.println("\nPurchases were saved!");
        }
    }

    private void displayMenu() {
        System.out.println("Choose your action:");
        System.out.println("1) Add income");
        System.out.println("2) Add purchase");
        System.out.println("3) Show list of purchases");
        System.out.println("4) Balance");
        System.out.println("5) Save");
        System.out.println("6) Load");
        System.out.println("7) Analyze (sort)");
        System.out.println("0) Exit");
    }

    private void addIncome() {
        this.scanner.nextLine(); // consume line

        System.out.println("\nEnter income:");

        double income = this.scanner.nextDouble();
        this.plan.addIncome(income);

        System.out.println("Income was added!");
    }

    private void addPurchase() {
        this.scanner.nextLine(); // consume line

        while (true) {
            this.showPurchaseTypes("add");
            int userTypeInput = this.scanner.nextInt();
            this.scanner.nextLine(); // consume line

            if (userTypeInput == 5) {
                break;
            }

            String purchaseType = this.plan.getPurchaseType(userTypeInput);

            System.out.println("\nEnter purchase name:");
            String purchaseName = this.scanner.nextLine();

            System.out.println("Enter its price:");
            double purchasePrice = this.scanner.nextDouble();

            String purchase = purchaseName + " $" + purchasePrice;
            boolean wasPurchaseAdded = this.plan.addPurchase(purchaseType, purchase);
            if (wasPurchaseAdded) {
                System.out.println("Purchase was added!");
            } else {
                System.out.println("\nNot enough income to add this purchase: " + purchase);
            }
        }
    }

    private void getBalance() {
        System.out.println("\nBalance: $" + this.plan.getBalance());
    }

    private void showPurchaseTypes(String option) {
        System.out.println("\nChoose the type of purchases");
        System.out.println("1) Food");
        System.out.println("2) Clothes");
        System.out.println("3) Entertainment");
        System.out.println("4) Other");
        if (!this.plan.getPurchases().isEmpty() && option.equalsIgnoreCase("show")) {
            System.out.println("5) All");
            System.out.println("6) Back");
        } else {
            System.out.println("5) Back");
        }
    }

    private void showPurchaseListByCategory() {
        if (this.plan.getPurchases().isEmpty()) {
            System.out.println("The purchase list is empty!");
            return;
        }

        while (true) {
            this.showPurchaseTypes("show");
            int purchaseTypeIndex = this.scanner.nextInt();
            if (this.plan.getPurchases().isEmpty() && purchaseTypeIndex == 5) {
                break;
            }

            if (!this.plan.getPurchases().isEmpty() && purchaseTypeIndex == 6) {
                break;
            }

            this.showPurchaseList(purchaseTypeIndex);
        }
    }

    private void showPurchaseList(int purchaseIndex) {
        Map<String, String> purchases = this.plan.getPurchases();

        if (purchases.isEmpty()) {
            System.out.println("The purchase list is empty");
            return;
        }

        this.showPurchases(purchases, purchaseIndex);
    }

    private void showPurchases(Map<String, String> purchases, int purchaseIndex) {
        double total = 0.0;
        String purchaseType;

        if (purchaseIndex == 5) {
            purchaseType = "all";
        } else {
            purchaseType = this.plan.getPurchaseType(purchaseIndex);
        }

        this.displayCategoryList(purchaseType);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        for (String purchase : purchases.keySet()) {
            int lastIndexOfDollar = purchase.lastIndexOf("$");
            String purchasePrice = purchase.substring(lastIndexOfDollar + 1).trim();

            if (purchaseType.equalsIgnoreCase("all")) {
                System.out.println(purchase);
                total += Double.parseDouble(purchasePrice);
            } else if (purchases.get(purchase).equalsIgnoreCase(purchaseType)) {
                System.out.println(purchase);
                total += Double.parseDouble(purchasePrice);
            }
        }

        if (!purchases.containsValue(purchaseType) && !purchaseType.equalsIgnoreCase("all")) {
            System.out.println("The purchase list is empty");
            return;
        }

        System.out.println("Total sum: $" + decimalFormat.format(total));
    }

    private void analyzePurchases() {
        while (true) {
            this.displayAnalyzeMenu();
            int userAction = this.scanner.nextInt();
            this.scanner.nextLine(); // consume input

            if (userAction == 4) {
                break;
            }

            switch (userAction) {
                case 1 -> {
                    List<String> sortedList = this.plan.getAllSortedPurchases();
                    this.displayAllSortedPurchases(sortedList);
                }
                case 2 -> {
                    Map<String, Double> sortedList = this.plan.getSortedPurchasesByTypes();
                    this.displaySortedPurchasesByTypes(sortedList);
                }
                case 3 -> {
                    this.displayCategoryAnalyzeMenu();
                    int type = this.scanner.nextInt();
                    this.scanner.nextLine(); // consume input

                    List<String> sortedList = this.plan.getSortedPurchasesByCertainType(type);
                    this.displayCategorySortedPurchases(type, sortedList);
                }
                default -> System.out.println("\nInvalid sorting action..");
            }
        }
    }

    private void displaySortedPurchasesByTypes(Map<String, Double> sortedList) {
        System.out.println("\nTypes:");
        if (this.plan.getPurchases().isEmpty()) {
            System.out.println(Category.FOOD + " - $0");
            System.out.println(Category.ENTERTAINMENT + " - $0");
            System.out.println(Category.CLOTHES + " - $0");
            System.out.println(Category.OTHER + " - $0");
            return;
        }

        String typeName = "";
        for (var purchaseKey : sortedList.keySet()) {
            if (purchaseKey.equalsIgnoreCase(Category.FOOD.name())) {
                typeName = "Food";
            }

            if (purchaseKey.equalsIgnoreCase(Category.ENTERTAINMENT.name())) {
                typeName = "Entertainment";
            }

            if (purchaseKey.equalsIgnoreCase(Category.CLOTHES.name())) {
                typeName = "Clothes";
            }

            if (purchaseKey.equalsIgnoreCase(Category.OTHER.name())) {
                typeName = "Other";
            }

            if (purchaseKey.equalsIgnoreCase("total")) {
                System.out.println("Total sum: $" + sortedList.get(purchaseKey));
            } else {
                System.out.println(typeName + " - $" + sortedList.get(purchaseKey));
            }
        }
    }

    private void displayCategorySortedPurchases(int type, List<String> sortedList) {
        if (sortedList == null) {
            System.out.println("\nThe purchase list is empty!");
            return;
        }

        this.displayCategoryList(Category.getKeyByValue(type).name());
        List<String> sortedPurchases = this.plan.getSortedPurchasesByCertainType(type);
        for (String purchase : sortedPurchases) {
            System.out.println(purchase);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        double total = 0.00;
        for (String purchase : sortedPurchases) {
            int lastIndexOfDollar = purchase.lastIndexOf("$");
            String purchasePrice = purchase.substring(lastIndexOfDollar + 1).trim();

            total += Double.parseDouble(purchasePrice);
        }

        System.out.println("Total sum: $" + decimalFormat.format(total));
    }

    private void displayAllSortedPurchases(List<String> sortedList) {
        if (this.plan.getPurchases().isEmpty()) {
            System.out.println("\nThe purchase list is empty!");
            return;
        }

        if (sortedList == null) {
            System.out.println("\nThe purchase list is empty!");
            return;
        }

        System.out.println("\nAll:");
        List<String> sortedPurchases = this.plan.getAllSortedPurchases();
        for (String purchase : sortedPurchases) {
            System.out.println(purchase);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        double total = 0.00;
        for (String purchase : sortedPurchases) {
            int lastIndexOfDollar = purchase.lastIndexOf("$");
            String purchasePrice = purchase.substring(lastIndexOfDollar + 1).trim();

            total += Double.parseDouble(purchasePrice);
        }

        System.out.println("Total sum: $" + decimalFormat.format(total));
    }

    private void displayAnalyzeMenu() {
        System.out.println("\nHow do you want to sort?");
        System.out.println("1) Sort all purchases");
        System.out.println("2) Sort by type");
        System.out.println("3) Sort certain type");
        System.out.println("4) Back");
    }

    private void displayCategoryAnalyzeMenu() {
        System.out.println("\nChoose the type of purchase?");
        System.out.println("1) Food");
        System.out.println("2) Clothes");
        System.out.println("3) Entertainment");
        System.out.println("4) Other");
    }

    private void displayCategoryList(String purchaseType) {
        char purchaseTypeFirstLetter = purchaseType.charAt(0);
        System.out.println("\n" + Character.toUpperCase(purchaseTypeFirstLetter) + purchaseType.substring(1).toLowerCase() + ":");
    }
}
