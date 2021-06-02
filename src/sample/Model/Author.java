package sample.Model;

public class Author {
    private int id;
    private String name;
    //private String surname;

    public Author(int id, String name) {
        this.id = id;
        this.name = name;
        //this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

   /* public String getSurname() {
        return surname;
    }*/

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getId()+": " + getName();
    }
}
