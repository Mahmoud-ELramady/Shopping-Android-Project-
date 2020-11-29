package com.example.shopping.Model;

public class Cart {

    private String pid, name, quantity, price, discount;

    public Cart(){}

    public Cart(String pid, String name, String quantity, String price, String discount) {
        this.pid = pid;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
