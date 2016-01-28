package ft.findandtravel.modello;


public class Reviewer {

    String author;
    int rating;
    String text;

    public Reviewer(String a,int r,String t){
        this.author = a;
        this.rating = r;
        this.text = t;
    }

    public Reviewer(){

    }

    public void setAuthor(String a){
        this.author = a;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

}
