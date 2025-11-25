package batch;

import java.sql.Connection;

import dao.MerchandiseDAO;
import tool.DBManager;

public class DeleteExpiredMerchandiseBatch {

    public static void main(String[] args) {
        try {

            DBManager db = new DBManager();
            Connection con = db.getConnection();

            MerchandiseDAO dao = new MerchandiseDAO(con);

            int count = dao.deleteExpiredWithImages();
            System.out.println("削除した商品数: " + count);

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
