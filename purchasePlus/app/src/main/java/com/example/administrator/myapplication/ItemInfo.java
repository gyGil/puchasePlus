package com.example.administrator.myapplication;

//================================================//
// NAME : ItemInfo
// PURPOSE : a item information for receipt
//================================================//
public class ItemInfo {

    //====================================[ CONSTANTS ]====================================//
    public static final String TYPE_APPLIANCE = "Appliance";
    public static final String TYPE_ELECTORONICS = "Electronics";
    public static final String TYPE_CLOTHES = "Clothes";
    public static final String TYPE_OTHERS = "Others";

    public  static final int TRUE = 1;
    public  static final int FALSE = 0;

    //====================================[ MEMBERS ]====================================//
    private  long itemId;
    private  long listId;
    private  String itemType;
    private  String itemName;
    private  String storeName;
    private  double purchaseAmount;
    private  String purchaseDate;
    private  String refundDueDate;
    private  int hidden;


    //=================================[ CONSTRUCTORS ]=================================//
    public ItemInfo(){
        itemType = itemName = storeName = "";
        purchaseAmount = 0.0f;
        purchaseDate = refundDueDate = "";
        hidden = FALSE;
    }

    public ItemInfo(long listId, String itemType, String itemName, String storeName,
                    double purchaseAmount, String purchaseDate, String refundDueDate, int hidden){
        this.listId = listId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.storeName = storeName;
        this.purchaseAmount = purchaseAmount;
        this.purchaseDate = purchaseDate;
        this.refundDueDate = refundDueDate;
        this.hidden = hidden;
    }

    public ItemInfo(long itemId,long listId, String itemType, String itemName, String storeName,
                    double purchaseAmount, String purchaseDate, String refundDueDate, int hidden){
        this.itemId = itemId;
        this.listId = listId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.storeName = storeName;
        this.purchaseAmount = purchaseAmount;
        this.purchaseDate = purchaseDate;
        this.refundDueDate = refundDueDate;
        this.hidden = hidden;
    }

    //=================================[ SETTER/ GETTER ]=================================//
    public  long getItemId() { return itemId; }
    public  void setItemId(long itemId) { this.itemId = itemId; }
    public  long getListId() { return listId; }
    public  void setListId(long listId) { this.listId = listId; }
    public  String getItemType() { return itemType; }
    public  void setItemType(String itemType) { this.itemType = itemType; }
    public  String getItemName() { return itemName; }
    public  void setItemName(String itemName) { this.itemName = itemName; }
    public  String getStoreName() { return storeName; }
    public  void setStoreName(String storeName) { this.storeName = storeName; }
    public  double getPurchaseAmount() { return  purchaseAmount; }
    public  void setPurchaseAmount(double purchaseAmount) { this.purchaseAmount = purchaseAmount; }
    public  String getPurchaseDate() { return purchaseDate; }
    public  void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
    public  String getRefundDueDate() { return refundDueDate; }
    public  void setRefundDueDate(String refundDueDate) { this.refundDueDate = refundDueDate; }
    public  int getHidden() {return  hidden; }
    public  void setHidden (int hidden) { this.hidden = hidden; }
}

