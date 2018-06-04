package project.android.thincnext.myrestaurent.model;

/**
 * Created by thincnext on 22-Jan-18.
 */

public class CartDataItems {

    private String _id;
    private String dishName;
    private String dishVeganType;
    private String dishId;
    private String dishQuantity;
    private String dishtotalPrice;
    private String dishPrice;
    private String dishImage;

    public CartDataItems(){}

    public CartDataItems(String dishName,String dishVeganType, String dishId,String dishQuantity,String dishtotalPrice,String dishPrice, String dishImage){
        this.dishName=dishName;
        this.dishVeganType=dishVeganType;
        this.dishId=dishId;
        this.dishQuantity=dishQuantity;
        this.dishtotalPrice=dishtotalPrice;
        this.dishPrice=dishPrice;
        this.dishImage=dishImage;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setDishVeganType(String dishVeganType) {
        this.dishVeganType = dishVeganType;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public void setDishQuantity(String dishQuantity) {
        this.dishQuantity = dishQuantity;
    }

    public void setDishtotalPrice(String dishtotalPrice) {
        this.dishtotalPrice = dishtotalPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }


    public String get_id() {
        return _id;
    }

    public String getDishName() {
        return dishName;
    }

    public String getDishVeganType() {
        return dishVeganType;
    }

    public String getDishId() {
        return dishId;
    }

    public String getDishQuantity() {
        return dishQuantity;
    }

    public String getDishTotalPrice() {
        return dishtotalPrice;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public String getDishImage() {
        return dishImage;
    }
}
