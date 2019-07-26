package raihana.msd.rgl.model;

public class BDetailClass {
    private String article, qty, price, gross, notes;

    public BDetailClass() {
    }

    public BDetailClass(String article, String qty, String price, String gross, String notes) {
        this.article = article;
        this.qty = qty;
        this.price = price;
        this.gross = gross;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
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
