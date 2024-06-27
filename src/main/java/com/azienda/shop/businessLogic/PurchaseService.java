package com.azienda.shop.businessLogic;

import com.azienda.shop.dao.PurchaseDAO;
import com.azienda.shop.dao.CartDAO;
import com.azienda.shop.dao.ProductDAO;
import com.azienda.shop.model.Purchase;
import com.azienda.shop.model.Cart;
import com.azienda.shop.model.Product;
import com.azienda.shop.model.User;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * Service class for managing operations related to purchases.
 * Extends AbstractService to leverage common transactional and CRUD operations.
 */
public class PurchaseService extends AbstractService<Purchase> {

    private CartDAO cartDAO;
    private ProductDAO productDAO;

    /**
     * Constructs a PurchaseService instance.
     *
     * @param manager     EntityManager instance for managing JPA operations.
     * @param purchaseDAO PurchaseDAO instance for CRUD operations on purchases.
     * @param cartDAO     CartDAO instance for CRUD operations on carts.
     * @param productDAO  ProductDAO instance for CRUD operations on products.
     */
    public PurchaseService(EntityManager manager, PurchaseDAO purchaseDAO, CartDAO cartDAO, ProductDAO productDAO) {
        super(manager, purchaseDAO);
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    /**
     * Creates a new purchase for a user and a product with the specified quantity.
     *
     * @param user     User making the purchase.
     * @param product  Product being purchased.
     * @param quantity Quantity of the product being purchased.
     * @return The created Purchase object.
     * @throws Exception if there is an issue creating the purchase.
     */
    public Purchase createPurchase(User user, Product product, int quantity) throws Exception {
        return executeTransaction(() -> {
            // Check if there's enough stock
            if (product.getQuantity() < quantity) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }

            // Create the purchase
            Purchase purchase = new Purchase(new Date(), product, user);
            Purchase createdPurchase = insert(purchase);

            // Update product stock
            product.setQuantity(product.getQuantity() - quantity);
            productDAO.update(product);

            // Remove the product from the user's cart if it exists
            Cart cart = cartDAO.findByUser(user);
            if (cart != null) {
                cart.getProducts().remove(product);
                cartDAO.update(cart);
            }

            return createdPurchase;
        });
    }

    /**
     * Retrieves the purchase history of a specific user.
     *
     * @param user User for whom to retrieve the purchase history.
     * @return List of Purchase objects representing the user's purchase history.
     * @throws Exception if there is an issue retrieving the purchase history.
     */
    public List<Purchase> getUserPurchaseHistory(User user) throws Exception {
        return executeTransaction(() -> ((PurchaseDAO) getDao()).findByUser(user));
    }

    /**
     * Retrieves the purchase history for a specific product.
     *
     * @param product Product for which to retrieve the purchase history.
     * @return List of Purchase objects representing the product's purchase history.
     * @throws Exception if there is an issue retrieving the purchase history.
     */
    public List<Purchase> getProductPurchaseHistory(Product product) throws Exception {
        return executeTransaction(() -> ((PurchaseDAO) getDao()).findByProduct(product));
    }

    /**
     * Retrieves purchases made within a specified date range.
     *
     * @param startDate Start date of the date range.
     * @param endDate   End date of the date range.
     * @return List of Purchase objects made within the specified date range.
     * @throws Exception if there is an issue retrieving purchases by date range.
     */
    public List<Purchase> getPurchasesByDateRange(Date startDate, Date endDate) throws Exception {
        return executeTransaction(() -> ((PurchaseDAO) getDao()).findByDateRange(startDate, endDate));
    }

    /**
     * Retrieves the count of purchases made for a specific product.
     *
     * @param product Product for which to count the purchases.
     * @return Number of purchases made for the specified product.
     * @throws Exception if there is an issue counting purchases for the product.
     */
    public Long getProductPurchaseCount(Product product) throws Exception {
        return executeTransaction(() -> ((PurchaseDAO) getDao()).countPurchasesByProduct(product));
    }

    /**
     * Retrieves the most recent purchases up to a specified count.
     *
     * @param count Number of recent purchases to retrieve.
     * @return List of Purchase objects representing the most recent purchases.
     * @throws Exception if there is an issue retrieving recent purchases.
     */
    public List<Purchase> getRecentPurchases(int count) throws Exception {
        return executeTransaction(() -> ((PurchaseDAO) getDao()).findMostRecentPurchases(count));
    }

    /**
     * Retrieves a purchase by its unique identifier.
     *
     * @param purchaseId ID of the purchase to retrieve.
     * @return Purchase object with the specified ID.
     * @throws IllegalArgumentException if no purchase is found with the specified ID.
     */
    @Override
    public Purchase retrieveById(Integer purchaseId) {
        Purchase purchase = super.retrieveById(purchaseId);
        if (purchase == null) {
            throw new IllegalArgumentException("Purchase not found with id: " + purchaseId);
        }
        return purchase;
    }

    /**
     * Cancels a purchase and restores the product's stock.
     *
     * @param purchaseId ID of the purchase to cancel.
     * @throws Exception if there is an issue cancelling the purchase.
     */
    public void cancelPurchase(Integer purchaseId) throws Exception {
        executeTransaction(() -> {
            Purchase purchase = retrieveById(purchaseId);

            // Restore product stock
            Product product = purchase.getProduct();
            product.setQuantity(product.getQuantity() + 1); // Assuming quantity is always 1
            productDAO.update(product);

            // Delete the purchase
            delete(purchase);
        });
    }

    /**
     * Retrieves the total revenue generated from all purchases.
     *
     * @return Total revenue as a double value.
     * @throws Exception if there is an issue retrieving the total revenue.
     */
    public double getTotalRevenue() throws Exception {
        return executeTransaction(() -> {
            List<Purchase> allPurchases = retrieveAll();
            return allPurchases.stream()
                    .mapToDouble(purchase -> purchase.getProduct().getPrice())
                    .sum();
        });
    }
}
