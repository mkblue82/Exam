package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import bean.Employee;
import bean.Merchandise;
import bean.MerchandiseImage;
import dao.EmployeeDAO;
import dao.FavoriteDAO;
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

            String name = null;
            String priceStr = null;
            String quantityStr = null;
            String expirationDateStr = null;
            String employeeNumberStr = null;
            String tags = null;
            List<FileItem> imageFiles = new ArrayList<>();

            if (ServletFileUpload.isMultipartContent(request)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setHeaderEncoding("UTF-8");

                ServletRequestContext context = new ServletRequestContext(request);
                List<FileItem> items = upload.parseRequest(context);

                System.out.println("★★★ 受信したFileItem数: " + items.size());

                for (FileItem item : items) {
                    System.out.println("★ FileItem: name=" + item.getFieldName() +
                                     ", isFormField=" + item.isFormField() +
                                     ", size=" + item.getSize());

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
                            System.out.println("★★★ フォームから受信した社員番号: [" + fieldValue + "]");
                        } else if ("tags".equals(fieldName)) {
                            tags = fieldValue;
                        }
                    } else {
                        if ("merchandiseImage".equals(item.getFieldName())) {
                            if (item.getSize() > 0) {
                                imageFiles.add(item);
                                System.out.println("✅ 画像ファイル追加: " + item.getName() + " (" + item.getSize() + " bytes)");
                            }
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
            System.out.println("★★★ 最終的な画像ファイル数 = " + imageFiles.size());

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

            // ★★★ 追加: 社員番号を3桁ゼロ埋に変換 ★★★
            employeeNumberStr = employeeNumberStr.trim();
            System.out.println("★★★ 受信した社員番号（trim後）: [" + employeeNumberStr + "] 長さ: " + employeeNumberStr.length());

            String formattedEmployeeNumber = employeeNumberStr;
            try {
                int empNum = Integer.parseInt(employeeNumberStr);
                formattedEmployeeNumber = String.format("%03d", empNum);
                System.out.println("★★★ フォーマット後の社員番号: [" + formattedEmployeeNumber + "]");
            } catch (NumberFormatException e) {
                System.out.println("★★★ 社員番号は数値変換不可、そのまま使用: [" + employeeNumberStr + "]");
            }

            if (imageFiles.isEmpty()) {
                request.setAttribute("errorMessage", "少なくとも1枚の画像を選択してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            int price = Integer.parseInt(priceStr);
            int stock = Integer.parseInt(quantityStr);
            java.sql.Date useByDate = java.sql.Date.valueOf(expirationDateStr);

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());


            if (useByDate.before(today)) {
                request.setAttribute("errorMessage", "消費期限には本日以降の日付を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp")
                       .forward(request, response);
                return;
            }

            // ★★★ 修正: フォーマット済み社員番号で検索 ★★★
            EmployeeDAO empDao = new EmployeeDAO();
            Employee employee = empDao.selectByCode(formattedEmployeeNumber);

            if (employee == null) {
                System.err.println("❌ 社員番号が見つかりません: [" + formattedEmployeeNumber + "]");
                request.setAttribute("errorMessage", "社員番号 " + employeeNumberStr + " は存在しません。正しい社員番号を入力してください。");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            int employeeId = employee.getId();
            System.out.println("✅ 社員番号 " + formattedEmployeeNumber + " → 社員ID " + employeeId + " に変換");
            System.out.println("✅ 社員名: " + employee.getEmployeeName());

            if (price < 0) {
                request.setAttribute("errorMessage", "価格は0円以上で入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

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

            System.out.println("★ Merchandise設定完了: name=" + m.getMerchandiseName());

            DBManager dbManager = new DBManager();
            connection = dbManager.getConnection();

            MerchandiseDAO dao = new MerchandiseDAO(connection);
            dao.insert(m);

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

            // 画像をDBに保存（bytea）
            System.out.println("★ 画像アップロード開始（" + imageFiles.size() + "枚）- DBにbytea保存");

            MerchandiseImageDAO imgDao = new MerchandiseImageDAO(connection);
            int displayOrder = 1;
            int successCount = 0;

            for (FileItem imageFile : imageFiles) {
                try {
                    System.out.println("★ 画像" + displayOrder + "処理開始: " + imageFile.getName() + " (" + imageFile.getSize() + " bytes)");

                    String originalFileName = imageFile.getName();

                    if (originalFileName != null && originalFileName.contains("\\")) {
                        originalFileName = originalFileName.substring(originalFileName.lastIndexOf("\\") + 1);
                    }
                    if (originalFileName != null && originalFileName.contains("/")) {
                        originalFileName = originalFileName.substring(originalFileName.lastIndexOf("/") + 1);
                    }

                    if (originalFileName == null || originalFileName.isEmpty()) {
                        originalFileName = "image_" + displayOrder + ".jpg";
                    }

                    System.out.println("★ ファイル名: " + originalFileName);

                    byte[] imageData = imageFile.get();

                    if (imageData == null || imageData.length == 0) {
                        System.err.println("❌ 警告: 画像データが空です");
                        continue;
                    }

                    System.out.println("★ 画像データサイズ: " + imageData.length + " bytes");

                    MerchandiseImage img = new MerchandiseImage();
                    img.setMerchandiseId(merchandiseId);
                    img.setImageData(imageData);
                    img.setFileName(originalFileName);
                    img.setDisplayOrder(displayOrder);

                    int imgResult = imgDao.insert(img);
                    System.out.println("✅ DB画像登録結果(" + displayOrder + "): " + imgResult + " 件");

                    successCount++;
                    displayOrder++;

                } catch (Exception imgEx) {
                    System.err.println("❌ 画像" + displayOrder + "の保存エラー: " + imgEx.getMessage());
                    imgEx.printStackTrace();
                }
            }

            System.out.println("✅ 画像保存完了！ 成功: " + successCount + "/" + imageFiles.size() + "枚");

            if (successCount == 0) {
                throw new Exception("画像の保存に失敗しました");
            }

            // ★★★ メール通知処理 ★★★
            try {
                FavoriteDAO favoriteDao = new FavoriteDAO(connection);
                List<String> notificationEmails = favoriteDao.getNotificationEnabledEmails(storeId);

                if (!notificationEmails.isEmpty()) {
                    System.out.println("★ 通知対象メールアドレス数: " + notificationEmails.size());

                    // 店舗名を取得
                    String storeName = (store != null && store.getStoreName() != null)
                                     ? store.getStoreName()
                                     : "お気に入り店舗";

                    // メール送信
                    EmailUtility.sendMerchandiseRegistrationNotification(
                        notificationEmails,
                        name,      // 商品名
                        price,     // 価格
                        storeName  // 店舗名
                    );

                    System.out.println("✅ 通知メール送信完了: " + notificationEmails.size() + "件");
                } else {
                    System.out.println("★ 通知対象ユーザーなし");
                }
            } catch (Exception emailEx) {
                // メール送信エラーは商品登録自体は成功させる
                System.err.println("❌ メール送信エラー(商品登録は成功): " + emailEx.getMessage());
                emailEx.printStackTrace();
            }
            // ★★★ メール通知処理ここまで ★★★

            m.setMerchandiseId(merchandiseId);
            session.setAttribute("registeredMerchandise", m);

            session.setAttribute("successMessage", "商品を登録しました（画像" + successCount + "枚）");
            response.sendRedirect(request.getContextPath() + "/store_jsp/merchandise_register_store_done.jsp");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "価格、個数は数値で入力してください");
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