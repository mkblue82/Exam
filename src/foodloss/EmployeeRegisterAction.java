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

        // ========== GET（社員登録画面を表示） ==========
        if (request.getMethod().equalsIgnoreCase("GET")) {
            request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
                   .forward(request, response);
            return;
        }

        // ========== POST（登録処理） ==========
        request.setCharacterEncoding("UTF-8");

        String employeeCodeStr = request.getParameter("employeeCode");
        String employeeName = request.getParameter("employeeName");

        // 入力チェック
        if (employeeCodeStr == null || employeeCodeStr.isEmpty()) {
            request.setAttribute("error", "社員コードを入力してください。");
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

        // 社員コード（PK）は int
        int employeeCode = Integer.parseInt(employeeCodeStr);

        // セッションの店舗情報
        HttpSession session = request.getSession();
        Store store = (Store) session.getAttribute("store");

        if (store == null) {
            request.setAttribute("error", "ログイン情報がありません。再ログインしてください。");
            request.getRequestDispatcher("/store_jsp/login_store.jsp")
                   .forward(request, response);
            return;
        }

        // DAO で DB登録
        EmployeeDAO dao = new EmployeeDAO();

        Employee emp = new Employee();
        emp.setId(employeeCode);                     // PK(int)
        emp.setEmployeeCode(employeeCodeStr);        // 表示用
        emp.setEmployeeName(employeeName);
        emp.setStoreCode(String.valueOf(store.getStoreId())); // 店舗ID(String)

        int result = dao.insert(emp);

        if (result > 0) {
            // 登録成功 → 一覧へ
            response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
        } else {
            request.setAttribute("error", "登録に失敗しました。");
            request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
                   .forward(request, response);
        }
    }
}
