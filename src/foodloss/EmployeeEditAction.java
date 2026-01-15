package foodloss;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeEditAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);

        // ログインチェック
        if (session == null || session.getAttribute("storeCode") == null) {
            res.sendRedirect(req.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        String storeCode = (String) session.getAttribute("storeCode");

        // GET: 編集画面表示
        if (req.getMethod().equalsIgnoreCase("GET")) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    EmployeeDAO dao = new EmployeeDAO();
                    Employee emp = dao.selectById(id);

                    // 自店舗の社員かチェック
                    if (emp != null && emp.getStoreCode().equals(storeCode)) {
                        req.setAttribute("employee", emp);
                        req.getRequestDispatcher("/store_jsp/employee_edit.jsp").forward(req, res);
                        return;
                    } else {
                        session.setAttribute("errorMessage", "指定された社員が見つからないか、他店舗の社員です。");
                        res.sendRedirect(req.getContextPath() + "/foodloss/EmployeeList.action?fromEdit=true");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errorMessage", "社員情報の取得中にエラーが発生しました。");
                    res.sendRedirect(req.getContextPath() + "/foodloss/EmployeeList.action?fromEdit=true");
                    return;
                }
            }
        }

        // POST: 更新処理
        try {
            int id = Integer.parseInt(req.getParameter("employeeId"));
            String name = req.getParameter("employeeName");

            EmployeeDAO dao = new EmployeeDAO();
            Employee emp = dao.selectById(id);  // 現在の情報取得

            // 自店舗の社員かチェック
            if (emp == null) {
                session.setAttribute("errorMessage", "更新対象の社員が見つかりません。");
            } else if (!emp.getStoreCode().equals(storeCode)) {
                session.setAttribute("errorMessage", "他店舗の社員は更新できません。");
            } else {
                emp.setEmployeeName(name);  // 氏名を更新
                int result = dao.update(emp);  // 更新実行

                if (result > 0) {
                    session.setAttribute("successMessage", "社員情報を更新しました。");
                } else {
                    session.setAttribute("errorMessage", "社員情報の更新に失敗しました。");
                }
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "無効な社員IDです。");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "社員情報の更新中にエラーが発生しました。");
        }

        res.sendRedirect(req.getContextPath() + "/foodloss/EmployeeList.action?fromEdit=true");
    }
}