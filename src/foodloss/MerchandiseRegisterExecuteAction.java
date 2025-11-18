package foodloss;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import bean.MerchandiseImage;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
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
            String priceStr = null;
            String quantityStr = null;
            String expirationDateStr = null;
            String employeeNumberStr = null;
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
                        } else if ("price".equals(fieldName)) {
                            priceStr = fieldValue;
                        } else if ("quantity".equals(fieldName)) {
                            quantityStr = fieldValue;
                        } else if ("expirationDate".equals(fieldName)) {
                            expirationDateStr = fieldValue;
                        } else if ("employeeNumber".equals(fieldName)) {
                            employeeNumberStr = fieldValue;
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
            System.out.println("★ price = [" + priceStr + "]");
            System.out.println("★ quantity = [" + quantityStr + "]");
            System.out.println("★ expirationDate = [" + expirationDateStr + "]");
            System.out.println("★ employeeNumber = [" + employeeNumberStr + "]");
            System.out.println("★ tags = [" + tags + "]");

            // バリデーション
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "商品名を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            if (priceStr == null || priceStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "価格を入力してください");
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

            if (employeeNumberStr == null || employeeNumberStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "社員番号を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            // 数値変換
            int price = Integer.parseInt(priceStr);
            int stock = Integer.parseInt(quantityStr);
            int employeeId = Integer.parseInt(employeeNumberStr);
            java.sql.Date useByDate = java.sql.Date.valueOf(expirationDateStr);

            // 価格の妥当性チェック
            if (price < 0) {
                request.setAttribute("errorMessage", "価格は0円以上で入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            // 商品情報を登録
            Merchandise m = new Merchandise();
            m.setStoreId(storeId);
            m.setMerchandiseName(name);
            m.setPrice(price);
            m.setStock(stock);
            m.setUseByDate(useByDate);
            m.setEmployeeId(employeeId);
            m.setMerchandiseTag(tags != null ? tags : "");
            m.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
            m.setBookingStatus(false);

            System.out.println("★ Merchandise設定完了: name=" + m.getMerchandiseName()
                + ", price=" + m.getPrice()
                + ", employeeId=" + m.getEmployeeId()
                + ", storeId=" + storeId);

            // DBManagerを使って接続
            DBManager dbManager = new DBManager();
            connection = dbManager.getConnection();

            // 商品を登録
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            int result = dao.insert(m);

            // 登録された商品のIDを取得
            int merchandiseId = 0;
            PreparedStatement st = connection.prepareStatement(
                "SELECT currval('t002_merchandise_t002_pk1_merchandise_seq')");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                merchandiseId = rs.getInt(1);
            }
            rs.close();
            st.close();

            System.out.println("✅ 商品登録成功！ merchandiseId = " + merchandiseId);

            // 画像がアップロードされている場合、画像を保存
            if (imageFile != null && imageFile.getSize() > 0) {
                System.out.println("★ 画像アップロード開始");
                System.out.println("★ ファイル名: " + imageFile.getName());
                System.out.println("★ ファイルサイズ: " + imageFile.getSize() + " bytes");
//sddf
                InputStream inputStream = imageFile.getInputStream();
                byte[] imageData = new byte[(int) imageFile.getSize()];
                int bytesRead = inputStream.read(imageData);
                inputStream.close();

                System.out.println("★ 読み込んだバイト数: " + bytesRead);

                MerchandiseImage img = new MerchandiseImage();
                img.setMerchandiseId(merchandiseId);
                img.setImageData(imageData);

                // ファイル名を取得
                String fileName = imageFile.getName();
                if (fileName == null || fileName.isEmpty()) {
                    fileName = "image_" + merchandiseId + ".jpg";
                }
                img.setFileName(fileName);
                img.setDisplayOrder(1);

                MerchandiseImageDAO imgDao = new MerchandiseImageDAO(connection);
                int imgResult = imgDao.insert(img);

                System.out.println("✅ 画像登録結果: " + imgResult + " 件");
            } else {
                System.out.println("⚠️ 画像ファイルが選択されていないか、サイズが0です");
                if (imageFile != null) {
                    System.out.println("   ファイルサイズ: " + imageFile.getSize());
                }
            }

            session.setAttribute("successMessage", "商品を登録しました");
            response.sendRedirect(request.getContextPath() + "/foodloss/Menu.action");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "価格、個数、社員番号は数値で入力してください");
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