package raihana.msd.rgl.model;

public class SearchArticleClass {
    private String article, description, brand, price, stock;

    public SearchArticleClass() {
    }

    public SearchArticleClass(String article, String description, String brand, String price, String stock) {
        this.article = article;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }

    public String getArticle() {
        return article;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getPrice() {
        return price;
    }

    public String getStock() {
        return stock;
    }
}
