package com.binoydipu.quickstock.services.cloud;

public class ItemModel {
    private String itemName, itemCode;
    private double purchasePrice, salePrice, stockValue;
    private int stockQuantity;
    private long expireDateInMillis;

    public ItemModel() {}

    public ItemModel(String itemName, String itemCode, double purchasePrice, double salePrice, int stockQuantity, long expireDateInMillis, double stockValue) {
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.expireDateInMillis = expireDateInMillis;
        this.stockValue = stockValue;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public long getExpireDateInMillis() {
        return expireDateInMillis;
    }

    public void setExpireDateInMillis(long expireDateInMillis) {
        this.expireDateInMillis = expireDateInMillis;
    }

    public double getStockValue() {
        return stockValue;
    }

    public void setStockValue(double stockValue) {
        this.stockValue = stockValue;
    }
}
