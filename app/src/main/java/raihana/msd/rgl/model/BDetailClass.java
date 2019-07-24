package raihana.msd.rgl.model;

public class BDetailClass {
    private String article, qty, price, gross;

    public BDetailClass() {
    }

    public BDetailClass(String article, String qty, String price, String gross) {
        this.article = article;
        this.qty = qty;
        this.price = price;
        this.gross = gross;
    }

    public String getArticle() {
        return article;
    }

    public String getQty() {
        return qty;
    }

    public String getPrice() {
        return price;
    }

    public String getGross() {
        return gross;
    }
}
