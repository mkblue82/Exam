package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.DiscountSettingDAO;
import tool.Action;

/**
 * 割引設定アクション
 */
public class DiscountSettingAction extends Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // セッションから店舗情報を取得
        HttpSession session = request.getSession();
        Employee employee = (Employee) session.getAttribute("employee");

        if (employee == null) {
            return "error.jsp?error=セッションが切れています";
        }

        // GETリクエストの場合は画面表示のみ
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return "discount_setting.jsp";
        }

        // POSTリクエストの場合は設定を保存
        try {
            // パラメータ取得
            String timeStr = request.getParameter("time");
            String discountStr = request.getParameter("discount");

            // バリデーション
            if (timeStr == null || timeStr.isEmpty() ||
                discountStr == null || discountStr.isEmpty()) {
                request.setAttribute("error", "すべての項目を入力してください");
                return "discount_setting.jsp";
            }

            int time = Integer.parseInt(timeStr);
            int discount = Integer.parseInt(discountStr);

            // 範囲チェック
            if (time < 0 || time > 23) {
                request.setAttribute("error", "時間は0〜23の範囲で入力してください");
                return "discount_setting.jsp";
            }

            if (discount < 1 || discount > 100) {
                request.setAttribute("error", "割引率は1〜100の範囲で入力してください");
                return "discount_setting.jsp";
            }

            // データベースに保存
            DiscountSettingDAO dao = new DiscountSettingDAO(getConnection());
            int result = dao.saveDiscountSetting(employee.getStoreId(), time, discount);

            if (result > 0) {
                // 成功時はリダイレクト
                return "discount_setting.jsp?success=true";
            } else {
                request.setAttribute("error", "割引設定の保存に失敗しました");
                return "discount_setting.jsp";
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "数値を正しく入力してください");
            return "discount_setting.jsp";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "エラーが発生しました: " + e.getMessage());
            return "discount_setting.jsp";
        }
    }
}