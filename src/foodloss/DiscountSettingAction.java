package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import bean.Store;
import dao.MerchandiseDAO;
import tool.Action;

public class DiscountSettingAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();

        // セッションから店舗情報を取得
        Store store = (Store) session.getAttribute("store");

        // セッションチェック
        if (store == null) {
            request.setAttribute("error", "セッションが切れています。再度ログインしてください。");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // パラメータ取得
        String timeStr = request.getParameter("time");
        String discountStr = request.getParameter("discount");
        String merchandiseIdStr = request.getParameter("merchandiseId");

        // ①-1 フィールドが未入力の場合
        if (timeStr == null || timeStr.trim().isEmpty() ||
            discountStr == null || discountStr.trim().isEmpty()) {
            request.setAttribute("error", "時間と割引率は必須項目です。");
            request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
            return;
        }

        Connection con = null;

        try {
            // DB接続
            con = getConnection();

            // 入力値を数値に変換
            int time = Integer.parseInt(timeStr);
            int discount = Integer.parseInt(discountStr);

            // ②-1 時間と割引率のバリデーション
            if (time < 0 || time > 23) {
                request.setAttribute("error", "時間は0〜23の範囲で入力してください。");
                request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
                return;
            }

            if (discount < 1 || discount > 100) {
                request.setAttribute("error", "割引率は1〜100の範囲で入力してください。");
                request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
                return;
            }

            MerchandiseDAO merchandiseDAO = new MerchandiseDAO(con);

            // 商品IDが指定されている場合は商品の存在チェック
            if (merchandiseIdStr != null && !merchandiseIdStr.trim().isEmpty()) {
                int merchandiseId = Integer.parseInt(merchandiseIdStr);

                Merchandise merchandise = merchandiseDAO.getMerchandiseById(merchandiseId);

                // ③-1 商品が未登録の場合
                if (merchandise == null) {
                    request.setAttribute("error", "登録されている商品がありません。");
                    request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
                    return;
                }

                // 商品に対する割引設定を更新
                int result = merchandiseDAO.updateDiscount(merchandiseId, time, discount);

                if (result > 0) {
                    // 成功時は割引設定完了画面へ
                    request.setAttribute("time", time);
                    request.setAttribute("discount", discount);
                    request.setAttribute("merchandise", merchandise);
                    request.getRequestDispatcher("store_jsp/discount_setting_complete.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "割引設定に失敗しました。");
                    request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
                }
            } else {
                // 商品IDが指定されていない場合（店舗全体の設定など）
                int result = merchandiseDAO.updateDiscountByStore(store.getStoreId(), time, discount);

                if (result > 0) {
                    // 成功メッセージをセッションに保存
                    session.setAttribute("successMessage", "割引設定が完了しました。");

                    // 成功時は割引設定画面へリダイレクト
                    response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?success=true");
                } else {
                    request.setAttribute("error", "割引設定に失敗しました。");
                    request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
                }
            }

        } catch (NumberFormatException e) {
            // ②-1 数字以外が入力された場合
            request.setAttribute("error", "時間と割引率は数字で入力してください。");
            request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
        } catch (Exception e) {
            // その他のエラー
            e.printStackTrace();
            request.setAttribute("error", "システムエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("store_jsp/discount_setting.jsp").forward(request, response);
        } finally {
            // DB接続を閉じる
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