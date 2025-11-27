package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeDeleteAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        HttpSession session = req.getSession(false);

        // ログインチェック
        if (session == null || session.getAttribute("storeCode") == null) {
            res.sendRedirect(req.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        String storeCode = (String) session.getAttribute("storeCode");
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            session.setAttribute("errorMessage", "削除対象の社員IDが指定されていません。");
            res.sendRedirect(req.getContextPath() + "/foodloss/EmployeeList.action");
            return;
        }

        try {
            int employeeId = Integer.parseInt(idParam);
            EmployeeDAO dao = new EmployeeDAO();

            // 削除対象の社員が自店舗の社員かチェック
            Employee emp = dao.selectById(employeeId);
            if (emp == null) {
                session.setAttribute("errorMessage", "指定された社員が見つかりませんでした。");
            } else if (!emp.getStoreCode().equals(storeCode)) {
                session.setAttribute("errorMessage", "他店舗の社員は削除できません。");
            } else {
                // 削除実行（戻り値はintで、削除された行数）
                int result = dao.delete(idParam);
                if (result > 0) {
                    session.setAttribute("successMessage", emp.getEmployeeName() + "さんを削除しました。");
                } else {
                    session.setAttribute("errorMessage", "社員の削除に失敗しました。");
                }
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "無効な社員IDです。");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "社員の削除中にエラーが発生しました。");
        }

        // 社員一覧にリダイレクト
        res.sendRedirect(req.getContextPath() + "/foodloss/EmployeeList.action");
    }
}