package sample.Model;

public class Magazine extends LibraryItem{
    private int releaseNumber;

    public Magazine(int id, String title, int releaseNumber , String author, String date) {
        super(id,author,title,date);
        this.releaseNumber = releaseNumber;
    }

    public int getReleaseNumber() {
        return releaseNumber;
    }

    @Override
    public String toString() {
        return getId()+": " + getTitle() + " " + getReleaseNumber() + " " + getAuthor() + " " + getDate();
    }
}
