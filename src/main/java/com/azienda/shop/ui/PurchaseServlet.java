package com.azienda.shop.ui;

import com.azienda.shop.businessLogic.ProductService;
import com.azienda.shop.businessLogic.PurchaseService;
import com.azienda.shop.businessLogic.UserService;
import com.azienda.shop.dao.CartDAO;
import com.azienda.shop.dao.ProductDAO;
import com.azienda.shop.dao.PurchaseDAO;
import com.azienda.shop.model.Cart;
import com.azienda.shop.model.Product;
import com.azienda.shop.model.Purchase;
import com.azienda.shop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;

public class PurchaseServlet extends HttpServlet {
    PurchaseService purchaseService;
    UserService userService;
    ProductService productService;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Shop");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ProductDAO productDAO = new ProductDAO(entityManager);
        CartDAO cartDAO = new CartDAO(entityManager);
        PurchaseDAO purchaseDAO = new PurchaseDAO(entityManager);
        purchaseService = new PurchaseService(entityManager, purchaseDAO, cartDAO, productDAO);
        productService = new ProductService(entityManager, productDAO, cartDAO);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            Integer productId = Integer.parseInt(req.getParameter("id"));
            String username = (String) session.getAttribute("username");
            Integer quantity = Integer.parseInt(req.getParameter("quantity"));
            if(username == null) {
                throw new ServletException("User is required");
            }
            if(productId == null) {
                throw new ServletException("Product id is required");
            }
            if(quantity == null) {
                throw new ServletException("Quantity is required"); //quantity = 1;
            }
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new ServletException("User not found");
            }
            Product product = productService.retrieveById(productId);
            if(product == null) {
                throw new ServletException("Product not found");
            }
            purchaseService.createPurchase(user,product, quantity);
            //Write sending code here

        } catch(ServletException e){

        }catch (Exception e) {

        }
    }
}