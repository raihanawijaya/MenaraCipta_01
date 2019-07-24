package raihana.msd.rgl.model;

public class BDetailSearchClass {
    private String uniqueID, article, price, trxNo;

    public BDetailSearchClass() {
    }

    public BDetailSearchClass(String uniqueID, String article, String price, String trxNo) {
        this.uniqueID = uniqueID;
        this.article = article;
        this.price = price;
        this.trxNo = trxNo;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public String getArticle() {
        return article;
    }

    public String getPrice() {
        return price;
    }

    public String getTrxNo() {
        return trxNo;
    }
}
