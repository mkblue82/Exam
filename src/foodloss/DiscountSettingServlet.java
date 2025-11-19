package foodloss;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import bean.Store;
import dao.MerchandiseDAO;

@WebServlet("/foodloss/DiscountSetting.action")
public class DiscountSettingServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://ep-rapid-surf-a100ysmf-pooler.ap-southeast-1.aws.neon.tech:5432/neondb";
    private static final String DB_USER = "neondb_owner";
    private static final String DB_PASSWORD = "npg_fpWk07TEejNO";

    private Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        System.out.println("=== DiscountSettingServlet 呼び出し ===");

        // セッションから店舗情報を取得
        Store store = (Store) session.getAttribute("store");

        // セッションチェック
        if (store == null) {
            System.out.println("セッションエラー: 店舗情報なし");
            request.setAttribute("error", "セッションが切れています。再度ログインしてください。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // パラメータ取得
        String timeStr = request.getParameter("time");
        String discountStr = request.getParameter("discount");
        String merchandiseIdStr = request.getParameter("merchandiseId");

        System.out.println("店舗ID: " + store.getStoreId());
        System.out.println("時間: " + timeStr);
        System.out.println("割引率: " + discountStr);

        // 未入力チェック
        if (timeStr == null || timeStr.trim().isEmpty() ||
            discountStr == null || discountStr.trim().isEmpty()) {
            System.out.println("バリデーションエラー: 未入力");
            request.setAttribute("error", "時間と割引率は必須項目です。");
            request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
            return;
        }

        Connection con = null;

        try {
            // DB接続
            con = getConnection();
            System.out.println("DB接続成功");

            // 入力値を数値に変換
            int time = Integer.parseInt(timeStr);
            int discount = Integer.parseInt(discountStr);

            // バリデーション
            if (time < 0 || time > 23) {
                System.out.println("バリデーションエラー: 時間範囲外");
                request.setAttribute("error", "時間は0〜23の範囲で入力してください。");
                request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
                return;
            }

            if (discount < 1 || discount > 100) {
                System.out.println("バリデーションエラー: 割引率範囲外");
                request.setAttribute("error", "割引率は1〜100の範囲で入力してください。");
                request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
                return;
            }

            MerchandiseDAO merchandiseDAO = new MerchandiseDAO(con);

            // 商品IDが指定されている場合
            if (merchandiseIdStr != null && !merchandiseIdStr.trim().isEmpty()) {
                int merchandiseId = Integer.parseInt(merchandiseIdStr);
                Merchandise merchandise = merchandiseDAO.getMerchandiseById(merchandiseId);

                if (merchandise == null) {
                    System.out.println("エラー: 商品が見つかりません");
                    request.setAttribute("error", "登録されている商品がありません。");
                    request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
                    return;
                }

                int result = merchandiseDAO.updateDiscount(merchandiseId, time, discount);
                System.out.println("個別商品更新結果: " + result + "件");

                if (result > 0) {
                    request.setAttribute("time", time);
                    request.setAttribute("discount", discount);
                    request.setAttribute("merchandise", merchandise);
                    request.getRequestDispatcher("/store_jsp/discount_setting_complete.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "割引設定に失敗しました。");
                    request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
                }
            } else {
                // 店舗全体の設定
                int result = merchandiseDAO.updateDiscountByStore(store.getStoreId(), time, discount);
                System.out.println("店舗全体更新結果: " + result + "件");

                if (result >= 0) {
                    session.setAttribute("successMessage", "割引設定が完了しました。");
                    response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?success=true");
                } else {
                    request.setAttribute("error", "割引設定に失敗しました。");
                    request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("数値変換エラー: " + e.getMessage());
            request.setAttribute("error", "時間と割引率は数字で入力してください。");
            request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("システムエラー:");
            e.printStackTrace();
            request.setAttribute("error", "システムエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/store_jsp/discount_setting.jsp").forward(request, response);
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
