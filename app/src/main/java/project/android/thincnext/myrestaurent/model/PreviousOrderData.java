package project.android.thincnext.myrestaurent.model;

/**
 * Created by thincnext on 01-Mar-18.
 */

public class PreviousOrderData {
    private String _id;
    private String dishName;
    private String dishVeganType;
    private String dishId;
    private String dishQuantity;
    private String dishtotalPrice;
    private String dishPrice;
    private String dishImage;
    private String orderStatus;
    private String orderDate;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishVeganType() {
        return dishVeganType;
    }

    public void setDishVeganType(String dishVeganType) {
        this.dishVeganType = dishVeganType;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishQuantity() {
        return dishQuantity;
    }

    public void setDishQuantity(String dishQuantity) {
        this.dishQuantity = dishQuantity;
    }

    public String getDishtotalPrice() {
        return dishtotalPrice;
    }

    public void setDishtotalPrice(String dishtotalPrice) {
        this.dishtotalPrice = dishtotalPrice;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
