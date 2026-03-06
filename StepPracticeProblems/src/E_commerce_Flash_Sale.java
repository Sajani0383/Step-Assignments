import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
public class E_commerce_Flash_Sale {
    private static ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Queue<String>> waitingList = new ConcurrentHashMap<>();
    public static void main(String[] args) throws InterruptedException {
        inventory.put("IPHONE15promax", new AtomicInteger(100));
        waitingList.put("IPHONE15promax", new ConcurrentLinkedQueue<>());
        int customers = 50000;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= customers; i++) {
            String customerId = "Customer-" + i;
            executor.execute(() -> purchaseProduct(customerId, "IPHONE15promax"));
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("Final Stock: " + checkStock("IPHONE15promax"));
        System.out.println("Waiting List Size: " + waitingList.get("IPHONE15promax").size());
    }
    public static void purchaseProduct(String customerId, String productId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) {
            System.out.println("Product not found.");
            return;
        }
        while (true) {
            int currentStock = stock.get();
            if (currentStock <= 0) {
                waitingList.get(productId).add(customerId);
                return;
            }
            if (stock.compareAndSet(currentStock, currentStock - 1)) {
                System.out.println(customerId + " purchased " + productId +
                        " | Remaining Stock: " + (currentStock - 1));
                return;
            }
        }
    }
    public static int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return stock != null ? stock.get() : 0;
    }
}