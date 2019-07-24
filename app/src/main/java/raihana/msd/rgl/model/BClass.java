package raihana.msd.rgl.model;

public class BClass {

    //declaration
    private String trxDate, qty, gross;

    //constructor
    public BClass() {
    }

    //constructor
    public BClass(String trxDate, String qty, String gross) {
        this.trxDate = trxDate;
        this.qty = qty;
        this.gross = gross;
    }

    //getter
    public String getTrxDate() {
        return trxDate;
    }

    public String getQty() {
        return qty;
    }

    public String getGross() {
        return gross;
    }

}
