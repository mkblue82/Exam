package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import bean.Store;
import dao.EmployeeDAO;
import tool.Action;
import tool.DBManager;

public class EmployeeRegisterAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // --- JSPからの値を取得 ---
        String employeeCode = request.getParameter("employeeCode");
        String employeeName = request.getParameter("employeeName");

        // --- セッションから店舗情報を取得 ---
        HttpSession session = request.getSession();
        Store store = (Store) session.getAttribute("store");

        if (store == null) {
            request.setAttribute("error", "店舗情報が取得できません。ログインし直してください。");
            request.getRequestDispatcher("/store_jsp/login_store.jsp").forward(request, response);
            return;
        }

        // --- DB登録処理 ---
        DBManager db = new DBManager();
        EmployeeDAO dao = new EmployeeDAO();
        Employee e = new Employee();
        e.setEmployeeCode(employeeCode);
        e.setEmployeeName(employeeName);
        e.setStoreCode(String.valueOf(store.getStoreId())); // storeIdはint型なのでStringに変換

        int result = dao.insert(e);

        // --- 登録結果 ---
        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
        } else {
            request.setAttribute("error", "登録に失敗しました。");
            request.getRequestDispatcher("/store_jsp/employee_register.jsp").forward(request, response);
        }
    }
}
