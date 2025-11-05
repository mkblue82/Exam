package bean;

import java.sql.Date;
import java.sql.Timestamp;

public class Merchandise implements java.io.Serializable {
    private int id;
    private String merchandiseCode;
    private String field1;
    private String field2;
    private Date field3;
    private String field4;
    private String field5;
    private String field6;
    private Timestamp field7;
    private String storeCode;
    private String storeName;
    private boolean field9;

    public int getId() {
        return id;
    }
    public String getMerchandiseCode() {
        return merchandiseCode;
    }
    public String getField1() {
        return field1;
    }
    public String getField2() {
        return field2;
    }
    public Date getField3() {
        return field3;
    }
    public String getField4() {
        return field4;
    }
    public String getField5() {
        return field5;
    }
    public String getField6() {
        return field6;
    }
    public Timestamp getField7() {
        return field7;
    }
    public String getStoreCode() {
        return storeCode;
    }
    public String getStoreName() {
        return storeName;
    }
    public boolean isField9() {
        return field9;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setMerchandiseCode(String merchandiseCode) {
        this.merchandiseCode = merchandiseCode;
    }
    public void setField1(String field1) {
        this.field1 = field1;
    }
    public void setField2(String field2) {
        this.field2 = field2;
    }
    public void setField3(Date field3) {
        this.field3 = field3;
    }
    public void setField4(String field4) {
        this.field4 = field4;
    }
    public void setField5(String field5) {
        this.field5 = field5;
    }
    public void setField6(String field6) {
        this.field6 = field6;
    }
    public void setField7(Timestamp field7) {
        this.field7 = field7;
    }
    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public void setField9(boolean field9) {
        this.field9 = field9;
    }
}