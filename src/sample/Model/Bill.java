package sample.Model;

import java.util.List;

public class Bill {
    private Client client;
    private List<Dish> dishesList;
    private Discount discount;

    public Bill(Client client, List<Dish> dishesList, Discount discount) {
        this.client = client;
        this.dishesList = dishesList;
        this.discount = discount;
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
