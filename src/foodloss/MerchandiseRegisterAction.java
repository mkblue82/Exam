package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class MerchandiseRegisterAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        System.out.println("===== 商品登録画面 開始 =====");

        HttpSession session = request.getSession(false);

        // ログインチェック
        if (session == null || session.getAttribute("store") == null) {
            System.out.println("❌ ログイン情報なし");
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        try {
            // 店舗情報を取得
            bean.Store store = (bean.Store) session.getAttribute("store");
            String storeCode = String.valueOf(store.getStoreId());
            System.out.println("✅ 店舗ID: " + storeCode + " (" + store.getStoreName() + ")");

            // その店舗に所属する社員リストを取得
            EmployeeDAO employeeDAO = new EmployeeDAO();
            System.out.println("✅ DAO生成OK");

            List<Employee> employeeList = employeeDAO.selectByStoreCode(storeCode);
            System.out.println("✅ selectByStoreCode実行完了");
            System.out.println("✅ 取得件数: " + employeeList.size());

            for (Employee emp : employeeList) {
                System.out.println("  → ID:" + emp.getId() +
                                 " 社員番号:" + emp.getEmployeeNumber() +
                                 " 名前:" + emp.getEmployeeName());
            }

            request.setAttribute("employeeList", employeeList);

        } catch (Exception e) {
            System.out.println("❌ エラー発生: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "社員情報の取得に失敗しました");
        }

        System.out.println("===== JSPへフォワード =====");
        request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp")
               .forward(request, response);
    }
}