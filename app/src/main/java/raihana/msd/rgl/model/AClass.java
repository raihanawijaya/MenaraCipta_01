package raihana.msd.rgl.model;

public class AClass {

    //declaration
    private String trxNo, trxDate, storeCode, storeName, qty, gross;

    //constructor
    public AClass() {
    }

    //constructor
    public AClass(String trxNo, String trxDate, String storeCode, String storeName, String qty, String gross) {
        this.trxNo = trxNo;
        this.trxDate = trxDate;
        this.qty = qty;
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.gross = gross;
    }

    //getter

    public String getTrxNo() {
        return trxNo;
    }
    public String getTrxDate() {
        return trxDate;
    }

    public String getQty() {
        return qty;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getGross() {
        return gross;
    }

    //setter
}
