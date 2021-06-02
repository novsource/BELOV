package sample.Model;

public abstract class LibraryItem {
    private int id;
    private String author;
    private String title;
    private String date;

    public LibraryItem(int id, String author, String title, String date) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getId()+": " + getTitle() + " " + getAuthor() + " " + getDate();
    }
}
