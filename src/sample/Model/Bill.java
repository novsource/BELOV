package sample.Model;

import java.util.List;

public class Bill {
    private Client client;
    private String clientName;
    private List<Dish> dishesList;
    private Discount discount;
    private int price;
    private String date;

    public Bill(Client client, String date, int price) {
        this.client = client;
        this.date = date;
        this.price = price;
        this.clientName = client.getName();
    }

    public Bill(Client client, List<Dish> dishesList, Discount discount, int price, String date) {
        this.client = client;
        this.dishesList = dishesList;
        this.discount = discount;
        this.price = price;
        this.date = date;
        this.clientName = client.getName();
    }

    public int getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Dish> getDishesList() {
        return dishesList;
    }

    public void setDishesList(List<Dish> dishesList) {
        this.dishesList = dishesList;
    }

    public Discount getDiscount() {
        return discount;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "client=" + client +
                ", dishesList=" + dishesList +
                ", discount=" + discount +
                '}';
    }
}
