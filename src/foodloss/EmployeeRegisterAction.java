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
        // ========== GET（社員登録画面表示） ==========
        if (request.getMethod().equalsIgnoreCase("GET")) {
            request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
                   .forward(request, response);
            return;
        }

        // ========== POST（登録処理） ==========
        request.setCharacterEncoding("UTF-8");
        String employeeNumber = request.getParameter("employeeNumber");
        String employeeName = request.getParameter("employeeName");

        // 入力チェック
        if (employeeNumber == null || employeeNumber.isEmpty()) {
            request.setAttribute("error", "社員番号を入力してください。");
            request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
                   .forward(request, response);
            return;
        }
        if (employeeName == null || employeeName.isEmpty()) {
            request.setAttribute("error", "社員名を入力してください。");
            request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
                   .forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        Store store = (Store) session.getAttribute("store");
        if (store == null) {
            request.setAttribute("error", "ログイン情報がありません。再ログインしてください。");
            request.getRequestDispatcher("/store_jsp/login_store.jsp")
                   .forward(request, response);
            return;
        }

        // ★★★ 重複チェック（新しいDAOインスタンスを使用）★★★
        EmployeeDAO checkDao = new EmployeeDAO();
        Employee existingEmployee = checkDao.getByEmployeeNumber(employeeNumber, String.valueOf(store.getStoreId()));

        if (existingEmployee != null) {
            request.setAttribute("error", "この社員番号は既に登録されています。");
            request.setAttribute("employeeNumber", employeeNumber);
            request.setAttribute("employeeName", employeeName);
            request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
                   .forward(request, response);
            return;
        }

        // セッションにstore情報を再セット（一あ覧用）
        session.setAttribute("storeCode", String.valueOf(store.getStoreId()));
        session.setAttribute("storeId", store.getStoreId());
        session.setAttribute("storeName", store.getStoreName());

        Employee emp = new Employee();
        emp.setEmployeeNumber(employeeNumber);
        emp.setEmployeeCode(employeeNumber);
        emp.setEmployeeName(employeeName);
        emp.setStoreCode(String.valueOf(store.getStoreId()));

        // ★★★ 登録用に新しいDAOインスタンスを使用 ★★★
        EmployeeDAO insertDao = new EmployeeDAO();
        int result = insertDao.insert(emp);

        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
        } else {
            request.setAttribute("error", "登録に失敗しました。");
            request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
                   .forward(request, response);
        }
    }
}