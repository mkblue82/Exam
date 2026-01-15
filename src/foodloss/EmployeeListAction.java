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
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        HttpSession session = req.getSession(false);

        // ★ ログインチェック（storeCodeがない場合はログインページにリダイレクト）
        if (session == null || session.getAttribute("storeCode") == null) {
            res.sendRedirect(req.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        String storeCode = (String) session.getAttribute("storeCode");
        String employeeCode = req.getParameter("employeeCode");

        // ★ 削除・編集後のリダイレクト以外でアクセスされた場合、メッセージをクリア
        String fromDelete = req.getParameter("fromDelete");
        String fromEdit = req.getParameter("fromEdit");
        if (fromDelete == null && fromEdit == null) {
            session.removeAttribute("successMessage");
            session.removeAttribute("errorMessage");
        }

        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> list = new ArrayList<>();

        try {
            if (employeeCode != null && !employeeCode.isEmpty()) {
                Employee e = dao.selectByCode(employeeCode);
                if (e != null && e.getStoreCode().equals(storeCode)) {
                    list.add(e);
                }
            } else {
                list = dao.selectByStoreCode(storeCode);
            }
            req.setAttribute("employeeList", list);
            req.setAttribute("employeeCode", employeeCode);
            req.setAttribute("storeCode", storeCode);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "社員情報の取得中にエラーが発生しました。");
        }

        req.getRequestDispatcher("/store_jsp/employee_list.jsp").forward(req, res);
    }
}