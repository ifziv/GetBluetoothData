package com.example.happi.getbluetoothdata.model;

/**
 * Created by happi on 16/7/19.
 */
public class VendorsResult {

    private String id;
    private String product_vendor;
    private String created_at;
    private String updated_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_vendor() {
        return product_vendor;
    }

    public void setProduct_vendor(String product_vendor) {
        this.product_vendor = product_vendor;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "VendorsResult{" +
                "id='" + id + '\'' +
                ", product_vendor='" + product_vendor + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
