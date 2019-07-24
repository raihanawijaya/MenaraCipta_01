package raihana.msd.rgl.model;

public class SearchStoreClass {
    private String storeCode, storeName, address, wilayah;

    public SearchStoreClass() {
    }

    public SearchStoreClass(String storeCode, String storeName, String address, String wilayah) {
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.address = address;
        this.wilayah = wilayah;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getAddress() {
        return address;
    }

    public String getWilayah() {
        return wilayah;
    }
}
