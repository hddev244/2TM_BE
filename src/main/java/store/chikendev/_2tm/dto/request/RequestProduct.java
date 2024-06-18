package store.chikendev._2tm.dto.request;

public class RequestProduct {
    private String name;
    private Double price;
    private Integer quantity;
    private String description;
    private String accountId;
    private Long storeId;

    // Constructors, getters, and setters

    public RequestProduct() {}

    public RequestProduct(String name, Double price, Integer quantity, String description, String accountId, Long storeId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.accountId = accountId;
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}
