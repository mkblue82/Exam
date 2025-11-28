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

import bean.Employee;
import bean.Merchandise;
import bean.MerchandiseImage;
import dao.EmployeeDAO;
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

                    // ★ 現在の担当社員情報を取得
                    EmployeeDAO empDao = new EmployeeDAO();
                    Employee employee = empDao.selectById(merchandise.getEmployeeId());

                    // ★ その店舗の社員リストを取得（セレクトボックス用）
                    List<Employee> employeeList = empDao.selectByStoreCode(String.valueOf(storeId));

                    request.setAttribute("merchandise", merchandise);
                    request.setAttribute("images", images);
                    request.setAttribute("employee", employee);
                    request.setAttribute("employeeList", employeeList);  // ★追加

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
                    MerchandiseImageDAO imageDao = new MerchandiseImageDAO(conn);

                    Merchandise merchandise = dao.selectById(merchandiseId);

                    if (merchandise == null || (storeId != null && merchandise.getStoreId() != storeId)) {
                        showError(request, response, "商品が見つからないか、アクセス権限がありません。");
                        return;
                    }

                    // ============================
                    // 入力値セット
                    // ============================
                    merchandise.setMerchandiseName(request.getParameter("merchandiseName"));
                    merchandise.setPrice(Integer.parseInt(request.getParameter("price")));
                    merchandise.setStock(Integer.parseInt(request.getParameter("stock")));
                    merchandise.setUseByDate(java.sql.Date.valueOf(request.getParameter("useByDate")));
                    merchandise.setEmployeeId(Integer.parseInt(request.getParameter("employeeId")));
                    merchandise.setMerchandiseTag(request.getParameter("merchandiseTag"));

                    // ============================
                    // ① 既存画像の削除
                    // ============================
                    String deletedIdsParam = request.getParameter("deletedImageIds");

                    if (deletedIdsParam != null && !deletedIdsParam.isEmpty()) {
                        String[] ids = deletedIdsParam.split(",");

                        for (String idStr : ids) {
                            try {
                                int imageId = Integer.parseInt(idStr.trim());
                                imageDao.delete(imageId);
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    // ============================
                    // ② 新規画像の登録
                    // ============================
                    List<MerchandiseImage> newImages = new ArrayList<>();

                    for (Part part : request.getParts()) {
                        if ("imageFile".equals(part.getName()) && part.getSize() > 0) {

                            byte[] data;
                            try (InputStream is = part.getInputStream()) {
                                data = readAll(is);
                            }

                            MerchandiseImage img = new MerchandiseImage();
                            img.setMerchandiseId(merchandiseId);
                            img.setImageData(data);
                            img.setFileName(part.getSubmittedFileName());
                            img.setDisplayOrder(0);

                            newImages.add(img);
                        }
                    }

                    // 追加された画像を DB に保存
                    for (MerchandiseImage img : newImages) {
                        imageDao.insert(img);
                    }

                    // ============================
                    // ③ 本体更新
                    // ============================
                    int result = dao.update(merchandise);

                    if (result > 0) {
                        response.sendRedirect(request.getContextPath() + "/foodloss/MerchandiseList.action");
                    } else {
                        showError(request, response, "更新に失敗しました。");
                    }
                }
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError(request, response, "エラーが発生しました: " + e.getMessage());
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