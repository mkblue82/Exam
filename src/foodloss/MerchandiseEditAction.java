package foodloss;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import bean.Merchandise;
import bean.MerchandiseImage;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
import tool.Action;
import tool.DBManager;

@MultipartConfig(maxFileSize = 10 * 1024 * 1024)  // 最大10MB
public class MerchandiseEditAction extends Action {



    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession();
            Integer storeId = (Integer) session.getAttribute("storeId");

            // ============================
            // GET：編集画面の表示
            // ============================
            if (request.getMethod().equalsIgnoreCase("GET")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.isEmpty()) {
                    showError(request, response, "商品IDが指定されていません。");
                    return;
                }
                int merchandiseId = Integer.parseInt(idParam);

                try (Connection conn = new DBManager().getConnection()) {
                    MerchandiseDAO dao = new MerchandiseDAO(conn);
                    Merchandise merchandise = dao.selectById(merchandiseId);

                    if (merchandise == null || (storeId != null && merchandise.getStoreId() != storeId)) {
                        showError(request, response, "商品が見つからないか、アクセス権限がありません。");
                        return;
                    }

                    MerchandiseImageDAO imageDao = new MerchandiseImageDAO(conn);
                    List<MerchandiseImage> images = imageDao.selectByMerchandiseId(merchandiseId);

                    request.setAttribute("merchandise", merchandise);
                    request.setAttribute("images", images);

                    request.getRequestDispatcher("/store_jsp/merchandise_edit.jsp").forward(request, response);
                }
                return;
            }

            // ============================
            // POST：更新処理
            // ============================
            if (request.getMethod().equalsIgnoreCase("POST")) {

                String idParam = request.getParameter("merchandiseId");
                if (idParam == null || idParam.isEmpty()) {
                    showError(request, response, "商品IDが指定されていません。");
                    return;
                }
                int merchandiseId = Integer.parseInt(idParam);

                try (Connection conn = new DBManager().getConnection()) {
                    MerchandiseDAO dao = new MerchandiseDAO(conn);
                    Merchandise merchandise = dao.selectById(merchandiseId);

                    if (merchandise == null || (storeId != null && merchandise.getStoreId() != storeId)) {
                        showError(request, response, "商品が見つからないか、アクセス権限がありません。");
                        return;
                    }

                    // 入力値取得
                    merchandise.setMerchandiseName(request.getParameter("merchandiseName"));
                    merchandise.setPrice(Integer.parseInt(request.getParameter("price")));
                    merchandise.setStock(Integer.parseInt(request.getParameter("stock")));
                    merchandise.setUseByDate(java.sql.Date.valueOf(request.getParameter("useByDate")));
                    merchandise.setEmployeeId(Integer.parseInt(request.getParameter("employeeId")));
                    merchandise.setMerchandiseTag(request.getParameter("merchandiseTag"));

                    // ============================
                    // ★ 画像処理（複数対応）
                    // ============================
                    List<MerchandiseImage> newImages = new ArrayList<>();

                    for (Part part : request.getParts()) {
                        if ("imageFile".equals(part.getName()) && part.getSize() > 0) {

                            byte[] data;
                            try (InputStream is = part.getInputStream()) {
                                data = readAll(is);   // Java8対応版！
                            }

                            MerchandiseImage img = new MerchandiseImage();
                            img.setMerchandiseId(merchandiseId);
                            img.setImageData(data);
                            img.setFileName(part.getSubmittedFileName());
                            img.setDisplayOrder(0);  // 必要なら順番を付ける

                            newImages.add(img);
                        }
                    }

                    // ============================
                    // ★ 商品と画像を同時に更新
                    // ============================
                    int result = dao.updateWithImages(merchandise, newImages, true); // true = 既存画像削除

                    if (result > 0) {
                        response.sendRedirect(request.getContextPath() + "/foodloss/MerchandiseList.action");
                    } else {
                        showError(request, response, "更新に失敗しました。");
                    }
                }
                return;
            }

            showError(request, response, "不正なリクエストです。");

        } catch (Exception e) {
            e.printStackTrace();
            showError(request, response, "システムエラーが発生しました。");
        }
    }

    private byte[] readAll(InputStream is) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int len;
        while ((len = is.read(tmp)) != -1) {
            buffer.write(tmp, 0, len);
        }
        return buffer.toByteArray();
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String msg) throws IOException {
        request.setAttribute("errorMessage", msg);
        try {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}
