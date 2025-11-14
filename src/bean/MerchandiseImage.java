package bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class MerchandiseImage implements Serializable {
    private int imageId;                // t002_1_pk1_image
    private int merchandiseId;          // t002_1_fd1_merchandise_id
    private byte[] imageData;           // t002_1_fd2_image_data
    private String fileName;            // t002_1_fd3_file_name
    private int displayOrder;           // t002_1_fd4_display_order
    private Timestamp uploadedAt;       // t002_1_fd5_uploaded_at

    public MerchandiseImage() {
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getMerchandiseId() {
        return merchandiseId;
    }

    public void setMerchandiseId(int merchandiseId) {
        this.merchandiseId = merchandiseId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
