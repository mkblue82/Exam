package bean;

public class FileInfo {
    private byte[] data;
    private String type;

    // ★ これが必要なコンストラクタです！
    public FileInfo(byte[] data, String type) {
        this.data = data;
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
