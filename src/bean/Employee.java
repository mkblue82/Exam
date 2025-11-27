package bean;

public class Employee implements java.io.Serializable {
    private int id;
    private String employeeCode;
    private String employeeName;
    private String storeCode;
    private String storeName;
    private String employeeNumber;  // 社員番号を追加

    public int getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getEmployeeNumber() {  // 社員番号のgetterを追加
        return employeeNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setEmployeeNumber(String employeeNumber) {  // 社員番号のsetterを追加
        this.employeeNumber = employeeNumber;
    }
}