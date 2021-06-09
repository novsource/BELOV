package sample.Model;

public class Client {
    private int id;
    private String name;
    private Discount discount;
    private int discountPers;
    private String phoneNumber;

    public Client(int id, String name, Discount discount) {
        this.id = id;
        this.name = name;
        this.discount = discount;
        this.discountPers = discount.getValue();
    }

    public Client(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Client(int id, String name, Discount discount, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.discount = discount;
        this.phoneNumber = phoneNumber;
        this.discountPers = discount.getValue();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscountPers() {
        return discountPers;
    }

    public void setDiscountPers(int discountPers) {
        this.discountPers = discountPers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
