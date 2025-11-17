package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import bean.Store;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeRegisterAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // JSPから値取得
        String employeeCode = request.getParameter("employeeCode");
        String employeeName = request.getParameter("employeeName");

        // セッションから店舗情報を取得
        HttpSession session = request.getSession();
        Store store = (Store) session.getAttribute("store");

        if (store == null) {
            request.setAttribute("error", "店舗情報が取得できません。ログインし直してください。");
            request.getRequestDispatcher("/store_jsp/login_store.jsp")
                   .forward(request, response);
            return;
        }

        // DB登録処理
        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = new Employee();
        emp.setEmployeeCode(employeeCode);
        emp.setEmployeeName(employeeName);
        emp.setStoreCode(String.valueOf(store.getStoreId())); // 店舗IDを文字列に変換してセット

        int result = dao.insert(emp);

        // 結果
        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
        } else {
            request.setAttribute("error", "登録に失敗しました。");
            request.getRequestDispatcher("/store_jsp/employee_register.jsp")
                   .forward(request, response);
        }
    }
}
