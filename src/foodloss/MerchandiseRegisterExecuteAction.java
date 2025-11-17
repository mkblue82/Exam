package foodloss;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import bean.Merchandise;
import dao.MerchandiseDAO;
import tool.Action;
import tool.DBManager;

public class MerchandiseRegisterExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Connection connection = null;

        try {
            request.setCharacterEncoding("UTF-8");

            // マルチパートリクエストの処理
            String name = null;
            String quantityStr = null;
            String expirationDateStr = null;
            String tags = null;
            FileItem imageFile = null;

            if (ServletFileUpload.isMultipartContent(request)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setHeaderEncoding("UTF-8");

                ServletRequestContext context = new ServletRequestContext(request);
                List<FileItem> items = upload.parseRequest(context);

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        String fieldName = item.getFieldName();
                        String fieldValue = item.getString("UTF-8");

                        if ("merchandiseName".equals(fieldName)) {
                            name = fieldValue;
                        } else if ("quantity".equals(fieldName)) {
                            quantityStr = fieldValue;
                        } else if ("expirationDate".equals(fieldName)) {
                            expirationDateStr = fieldValue;
                        } else if ("tags".equals(fieldName)) {
                            tags = fieldValue;
                        }
                    } else {
                        // 画像ファイル
                        if ("merchandiseImage".equals(item.getFieldName())) {
                            imageFile = item;
                        }
                    }
                }
            }

            HttpSession session = request.getSession();
            bean.Store store = (bean.Store) session.getAttribute("store");

            int storeId = 0;

            if (store != null) {
                storeId = store.getStoreId();
                System.out.println("✅ セッションからstoreId取得: " + storeId);
            } else {
                bean.User user = (bean.User) session.getAttribute("user");
                if (user != null) {
                    storeId = 2;
                    System.out.println("⚠️ ユーザーログイン中のため、テスト用storeId=2 を使用");
                } else {
                    request.setAttribute("errorMessage", "ログインしてください");
                    response.sendRedirect(request.getContextPath() + "/foodloss/Login_Store.action");
                    return;
                }
            }

            System.out.println("★ merchandiseName = [" + name + "]");
            System.out.println("★ quantity = [" + quantityStr + "]");
            System.out.println("★ expirationDate = [" + expirationDateStr + "]");
            System.out.println("★ tags = [" + tags + "]");

            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "商品名を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "個数を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            if (expirationDateStr == null || expirationDateStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "消費期限を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            int stock = Integer.parseInt(quantityStr);
            java.sql.Date useByDate = java.sql.Date.valueOf(expirationDateStr);

            // 商品情報を登録
            Merchandise m = new Merchandise();
            m.setStoreId(storeId);
            m.setMerchandiseName(name);
            m.setStock(stock);
            m.setUseByDate(useByDate);
            m.setMerchandiseTag(tags != null ? tags : "");
            m.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
            m.setBookingStatus(false);

            System.out.println("★ Merchandise設定完了: name=" + m.getMerchandiseName() + ", storeId=" + storeId);
//どうしてーーー
            // ★ DBManagerを使って接続 ★
            DBManager dbManager = new DBManager();
            connection = dbManager.getConnection();

            MerchandiseDAO dao = new MerchandiseDAO(connection);
            int result = dao.insert(m);

            System.out.println("✅ 商品登録成功！");
            session.setAttribute("successMessage", "商品を登録しました");
            response.sendRedirect(request.getContextPath() + "/foodloss/Menu.action");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "個数は数値で入力してください");
            request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "消費期限の形式が正しくありません");
            request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "商品登録中にエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}