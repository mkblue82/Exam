package foodloss;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.MerchandiseImage;
import dao.MerchandiseImageDAO;
import tool.DBManager;

@WebServlet("/image/*")  // /image/123 のような形式でアクセス
public class ImageDisplayServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        System.out.println("===== ImageDisplayServlet START =====");

        // URLから画像IDを取得: /image/123 → "123"
        String pathInfo = req.getPathInfo();
        System.out.println("PathInfo: " + pathInfo);

        if (pathInfo == null || pathInfo.length() <= 1) {
            System.out.println("ERROR: No image ID in path");
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "画像IDが指定されていません");
            return;
        }

        String idStr = pathInfo.substring(1); // 先頭の "/" を除去
        System.out.println("Image ID string: " + idStr);

        int imageId = 0;
        try {
            imageId = Integer.parseInt(idStr);
            System.out.println("Parsed imageId: " + imageId);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid imageId format: " + idStr);
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "無効な画像ID");
            return;
        }

        Connection con = null;
        try {
            con = new DBManager().getConnection();
            System.out.println("DB Connection established");

            MerchandiseImageDAO dao = new MerchandiseImageDAO(con);
            MerchandiseImage img = dao.selectById(imageId);

            if (img == null) {
                System.out.println("ERROR: Image not found for ID: " + imageId);
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "画像が見つかりません");
                return;
            }

            byte[] imageData = img.getImageData();
            if (imageData == null || imageData.length == 0) {
                System.out.println("ERROR: Image data is null or empty for ID: " + imageId);
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "画像データが空です");
                return;
            }

            System.out.println("Image found, data length: " + imageData.length);

            // レスポンス設定
            res.setContentType("image/jpeg");
            res.setContentLength(imageData.length);

            // 画像データを出力
            OutputStream out = res.getOutputStream();
            out.write(imageData);
            out.flush();

            System.out.println("===== ImageDisplayServlet END (SUCCESS) =====");

        } catch (Exception e) {
            System.out.println("ERROR: Exception in ImageDisplayServlet");
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "画像の取得に失敗しました");
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}