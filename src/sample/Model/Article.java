package sample.Model;

public class Article extends LibraryItem{
    private String conference;

    public Article(int id, String title, String author, String conference, String date) {
        super(id,author,title,date);
        this.conference = conference;
    }

    public String getConference() {
        return conference;
    }

    @Override
    public String toString() {
        return getId()+": " + getTitle() + " " + getAuthor() + " " + getConference() + " " + getDate();
    }
}
