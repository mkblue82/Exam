package foodloss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.EmployeeDAO;

@WebServlet("/employee_list")
public class EmployeeListAction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // セッションからログイン中店舗IDを取得
        HttpSession session = request.getSession(false);
        String storeId = null;

        if (session != null) {
            storeId = (String) session.getAttribute("storeId");
        }

        // 検索パラメータを取得（個別検索用）
        String employeeCode = request.getParameter("employeeCode");

        try {
            EmployeeDAO dao = new EmployeeDAO();
            List<Employee> list = new ArrayList<>();

            if (employeeCode != null && !employeeCode.isEmpty()) {
                // 社員コードで検索（単一）
                Employee e = dao.selectByCode(employeeCode);
                if (e != null && e.getStoreId().equals(storeId)) { // 自店舗所属のみ表示
                    list.add(e);
                }
            } else if (storeId != null && !storeId.isEmpty()) {
                // ログイン店舗の社員一覧を取得
                list = dao.selectByStoreId(storeId);
            }

            // JSP にデータを渡す
            request.setAttribute("employeeList", list);
            request.setAttribute("employeeCode", employeeCode);
            request.setAttribute("storeId", storeId);

            // 一覧ページへ
            request.getRequestDispatcher("/store_jsp/employee_list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "社員情報の取得中にエラーが発生しました。");
            request.getRequestDispatcher("/store_jsp/error.jsp").forward(request, response);
        }
    }
}
