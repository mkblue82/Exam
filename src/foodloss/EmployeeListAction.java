package foodloss;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeListAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        // ★ セッションから店舗コードを取得
        HttpSession session = req.getSession(false);
        String storeCode = null;

        if (session != null) {
            storeCode = (String) session.getAttribute("storeCode");
        }

        // ★ 検索用パラメータ
        String employeeCode = req.getParameter("employeeCode");

        // ★ DAO 呼び出し（Connection は渡さない）
        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> list = new ArrayList<>();

        try {

            if (employeeCode != null && !employeeCode.isEmpty()) {

                // 個別検索
                Employee e = dao.selectByCode(employeeCode);
                if (e != null && e.getStoreCode().equals(storeCode)) {
                    list.add(e);
                }

            } else if (storeCode != null && !storeCode.isEmpty()) {

                // 全社員（店舗コード検索）
                list = dao.selectByStoreCode(storeCode);
            }

            // JSP に渡す
            req.setAttribute("employeeList", list);
            req.setAttribute("employeeCode", employeeCode);
            req.setAttribute("storeCode", storeCode);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "社員情報の取得中にエラーが発生しました。");
        }

        // ★ 社員一覧 JSP に forward
        req.getRequestDispatcher("/store_jsp/employee_list.jsp")
           .forward(req, res);
    }
}
