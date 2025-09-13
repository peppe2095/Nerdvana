package Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cart {
    private List<ProductBean> products;

    public Cart() {
        products = new ArrayList<>();
    }

    public void addProduct(ProductBean product, int quantity) {
        boolean found = false;
        for (ProductBean existingProduct : products) {
            if (existingProduct.getNumeroSeriale().equals(product.getNumeroSeriale())) {
                existingProduct.setSelectedQuantity(existingProduct.getSelectedQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            product.setSelectedQuantity(quantity);
            products.add(product);
        }
    }

    public void updateQuantity(String serialNumber, int newQuantity) {
        for (ProductBean product : products) {
            if (product.getNumeroSeriale().equals(serialNumber)) {
                product.setSelectedQuantity(newQuantity);
                return;
            }
        }
    }

    public void removeProduct(String serialNumber) {
        Iterator<ProductBean> iterator = products.iterator();
        while (iterator.hasNext()) {
            ProductBean product = iterator.next();
            if (product.getNumeroSeriale().equals(serialNumber)) {
                iterator.remove();
                return;
            }
        }
    }

    public void clear() {
        products.clear();
    }

    public List<ProductBean> getProducts() {
        return products;
    }

    public int getTotalItems() {
        int totalItems = 0;
        for (ProductBean product : products) {
            totalItems += product.getSelectedQuantity();
        }
        return totalItems;
    }

    public double getTotalPrice() {
        double totalPrice = 0.0;
        for (ProductBean product : products) {
            totalPrice += product.getPrezzo() * product.getSelectedQuantity();
        }
        return totalPrice;
    }
}
