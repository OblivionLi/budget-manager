package budget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Plan {
    private double balance;
    private final Map<String, String> purchases = new HashMap<>();
    private final String filename = "purchases.txt";

    public void addIncome(double income) {
        this.balance += income;
    }

    public String getBalance() {
        return this.getFormattedBalance();
    }

    public Map<String, String> getPurchases() {
        return this.purchases;
    }

    public String getPurchaseType(int typeIndex) {
        return Category.getKeyByValue(typeIndex).name();
    }

    public boolean addPurchase(String purchaseType, String purchase) {
        String[] purchaseParts = purchase.split("\\$");
        if (purchaseParts.length < 2) {
            return false;
        }

        double purchasePrice;

        try {
            purchasePrice = Double.parseDouble(purchaseParts[purchaseParts.length - 1].trim());
        } catch (NumberFormatException e) {
            System.out.printf("Invalid price format %s", e.getMessage());
            return false;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedPrice = decimalFormat.format(purchasePrice);

        if (this.balance >= purchasePrice) {
            this.balance -= purchasePrice;

            String formattedPurchase = purchase.replace(purchaseParts[purchaseParts.length - 1].trim(), formattedPrice);
            this.purchases.put(formattedPurchase, purchaseType);

            return true;
        }

        return false;
    }

    public boolean savePurchasesToFile() {
        if (this.purchases.isEmpty()) {
            return false;
        }

        File file = new File(this.filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Balance:" + this.getFormattedBalance() + "\n");

            for (String purchase : this.purchases.keySet()) {
                String purchaseType = this.purchases.get(purchase);
                writer.write(purchaseType + ":" + purchase + "\n");
            }

            return true;
        } catch (IOException e) {
            System.out.printf("An exception occurred %s", e.getMessage());
            return false;
        }
    }

    public boolean loadPurchasesFromFile() {
        File file = new File(this.filename);

        try (Scanner fileData = new Scanner(file)) {
            while (fileData.hasNextLine()) {
                String fileLineData = fileData.nextLine();
                if (fileLineData.isEmpty()) {
                    break;
                }

                String[] fileLineParts = fileLineData.split(":");

                if (fileLineParts[0].equalsIgnoreCase("balance")) {
                    this.balance += Double.parseDouble(fileLineParts[1]);
                    continue;
                }

                String purchaseType = fileLineParts[0];
                String purchase = fileLineParts[1];
                this.purchases.put(purchase, purchaseType);
            }

            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return false;
        }
    }

    private String getFormattedBalance() {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(this.balance);
    }

    public List<String> getAllSortedPurchases() {
        Map<String, Double> unsortedPurchases = new HashMap<>();
        for (String purchaseKey : this.purchases.keySet()) {
            String formattedPrice = this.getFormattedPrice(purchaseKey);

            if (formattedPrice == null) {
                return new ArrayList<>();
            }

            unsortedPurchases.put(purchaseKey, Double.parseDouble(formattedPrice));
        }

        return this.sortListInDescOrder(unsortedPurchases);
    }

    public Map<String, Double> getSortedPurchasesByTypes() {
        Map<String, Double> unsortedPurchases = new HashMap<>();

        double total = 0.00;

        for (Map.Entry<String, String> entry : purchases.entrySet()) {
            String purchaseKey = entry.getKey();
            String purchaseType = entry.getValue();
            String formattedPrice = getFormattedPrice(purchaseKey);

            if (formattedPrice == null) {
                continue;
            }

            double price = Double.parseDouble(formattedPrice);
            unsortedPurchases.put(purchaseType, unsortedPurchases.getOrDefault(purchaseType, 0.0) + price);
            total += price;
        }

        Map<String, Double> sortedPurchases = new TreeMap<>(Comparator.comparingDouble(unsortedPurchases::get).reversed());
        sortedPurchases.putAll(unsortedPurchases);

        Map<String, Double> result = new LinkedHashMap<>();

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        for (Map.Entry<String, Double> entry : sortedPurchases.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();

            if (key.equals(Category.FOOD.name()) || key.equals(Category.ENTERTAINMENT.name())
                    || key.equals(Category.CLOTHES.name()) || key.equals(Category.OTHER.name())) {
                result.put(key, Double.parseDouble(decimalFormat.format(value)));
            }
        }

        result.put("TOTAL", Double.parseDouble(decimalFormat.format(total)));
        return result;
    }

    public List<String> getSortedPurchasesByCertainType(int type) {
        String purchaseCategory = Category.getKeyByValue(type).name();
        if (!this.purchases.containsValue(purchaseCategory)) {
            return null;
        }

        Map<String, Double> unsortedPurchases = new HashMap<>();
        for (String purchaseKey : this.purchases.keySet()) {
            String purchase = this.purchases.get(purchaseKey);
            if (purchaseCategory.equalsIgnoreCase(purchase)) {
                String formattedPrice = this.getFormattedPrice(purchaseKey);

                if (formattedPrice == null) {
                    continue;
                }

                unsortedPurchases.put(purchaseKey, Double.parseDouble(formattedPrice));
            }
        }

        return this.sortListInDescOrder(unsortedPurchases);
    }

    private List<String> sortListInDescOrder(Map<String, Double> list) {
        List<String> sortedList = new ArrayList<>(list.keySet());
        sortedList.sort(Comparator.comparingDouble(list::get).reversed());
        return sortedList;
    }

    private String getFormattedPrice(String purchase) {
        String[] purchaseParts = purchase.split("\\$");
        if (purchaseParts.length < 2) {
            return null;
        }

        double purchasePrice;

        try {
            purchasePrice = Double.parseDouble(purchaseParts[purchaseParts.length - 1].trim());
        } catch (NumberFormatException e) {
            System.out.printf("Invalid price format %s", e.getMessage());
            return null;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(purchasePrice);
    }
}
