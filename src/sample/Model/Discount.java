package sample.Model;

public class Discount {
    private int id;
    private int criteria;
    private int value;

    public Discount(int id, int value, int criteria) {
        this.id = id;
        this.criteria = criteria;
        this.value = value;
    }

    public Discount(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCriteria() {
        return criteria;
    }

    public void setCriteria(int criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
