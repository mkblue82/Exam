package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import bean.Store;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeUpdateAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        System.out.println("🔧 EmployeeUpdateAction 実行開始");

        // ========== GET（社員編集画面表示） ==========
        if (request.getMethod().equalsIgnoreCase("GET")) {
            String idStr = request.getParameter("id");

            if (idStr == null || idStr.trim().isEmpty()) {
                request.setAttribute("error", "社員IDが指定されていません。");
                response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
                return;
            }

            try {
                int id = Integer.parseInt(idStr);
                EmployeeDAO dao = new EmployeeDAO();
                Employee employee = dao.selectById(id);

                if (employee == null) {
                    request.setAttribute("error", "指定された社員が見つかりません。");
                    response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
                    return;
                }

                request.setAttribute("employee", employee);
                request.getRequestDispatcher("/store_jsp/employee_edit.jsp")
                       .forward(request, response);
                return;

            } catch (NumberFormatException e) {
                request.setAttribute("error", "不正な社員IDです。");
                response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
                return;
            }
        }

        // ========== POST（更新処理） ==========
        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String employeeNumber = request.getParameter("employeeNumber");
        String employeeName = request.getParameter("employeeName");

        System.out.println("📝 更新データ:");
        System.out.println("   ID: " + idStr);
        System.out.println("   社員名: " + employeeName);
        System.out.println("   社員番号: " + employeeNumber);

        // 入力チェック
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("error", "社員IDが指定されていません。");
            response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
            return;
        }

        if (employeeNumber == null || employeeNumber.isEmpty()) {
            request.setAttribute("error", "社員番号を入力してください。");
            request.getRequestDispatcher("/store_jsp/employee_edit.jsp")
                   .forward(request, response);
            return;
        }

        if (employeeName == null || employeeName.isEmpty()) {
            request.setAttribute("error", "社員名を入力してください。");
            request.getRequestDispatcher("/store_jsp/employee_edit.jsp")
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

        try {
            int id = Integer.parseInt(idStr);
            String storeCode = String.valueOf(store.getStoreId());

            // ★★★ 重複チェック（自分以外で同じ社員番号が存在するか）★★★
            EmployeeDAO checkDao = new EmployeeDAO();
            Employee existingEmployee = checkDao.getByEmployeeNumber(employeeNumber, storeCode);

            if (existingEmployee != null && existingEmployee.getId() != id) {
                System.out.println("⚠️ 社員番号が重複しています");
                request.setAttribute("error", "この社員番号は既に登録されています。");

                // 元のemployeeオブジェクトを再取得して渡す
                Employee employee = checkDao.selectById(id);
                employee.setEmployeeNumber(employeeNumber);  // 入力された値を保持
                employee.setEmployeeName(employeeName);      // 入力された値を保持
                request.setAttribute("employee", employee);

                request.getRequestDispatcher("/store_jsp/employee_edit.jsp")
                       .forward(request, response);
                return;
            }

            // セッションにstore情報を再セット（一覧用）
            session.setAttribute("storeCode", storeCode);
            session.setAttribute("storeId", store.getStoreId());
            session.setAttribute("storeName", store.getStoreName());

            // Employeeオブジェクト作成
            Employee emp = new Employee();
            emp.setId(id);
            emp.setEmployeeNumber(employeeNumber);
            emp.setEmployeeCode(employeeNumber);
            emp.setEmployeeName(employeeName);
            emp.setStoreCode(storeCode);

            // ★★★ 更新用に新しいDAOインスタンスを使用 ★★★
            EmployeeDAO updateDao = new EmployeeDAO();
            int result = updateDao.update(emp);

            if (result > 0) {
                System.out.println("✅ 社員情報を更新しました");
                response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
            } else {
                System.out.println("❌ 更新に失敗しました");
                request.setAttribute("error", "更新に失敗しました。");

                // エラー時も入力値を保持
                Employee employee = new Employee();
                employee.setId(id);
                employee.setEmployeeNumber(employeeNumber);
                employee.setEmployeeName(employeeName);
                employee.setStoreCode(storeCode);
                request.setAttribute("employee", employee);

                request.getRequestDispatcher("/store_jsp/employee_edit.jsp")
                       .forward(request, response);
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ 数値変換エラー: " + e.getMessage());
            request.setAttribute("error", "不正なデータ形式です。");
            response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
        } catch (Exception e) {
            System.out.println("❌ エラー発生: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "更新処理中にエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/store_jsp/employee_edit.jsp")
                   .forward(request, response);
        }
    }
}