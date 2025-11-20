package foodloss;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.DiscountDAO;
import tool.Action;

public class DiscountSettingAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();

        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) {
            response.sendRedirect("error.jsp?error=セッションが切れています");
            return;
        }

        int storeId = employee.getStoreId();
        Connection connection = getConnection();
        DiscountDAO discountDao = new DiscountDAO(connection);

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            DiscountDAO.DiscountSetting currentSetting = discountDao.getDiscountSetting(storeId);
            if (currentSetting != null && currentSetting.isActive()) {
                request.setAttribute("currentTime", currentSetting.getDiscountTime());
                request.setAttribute("currentDiscount", currentSetting.getDiscountRate());
            }
            connection.close();
            request.getRequestDispatcher("discount-setting.jsp").forward(request, response);
            return;
        }

        try {
            String timeStr = request.getParameter("time");
            String discountStr = request.getParameter("discount");

            if (timeStr == null || discountStr == null ||
                timeStr.trim().isEmpty() || discountStr.trim().isEmpty()) {
                request.setAttribute("error", "入力値が不正です");
                connection.close();
                request.getRequestDispatcher("discount-setting.jsp").forward(request, response);
                return;
            }

            int time = Integer.parseInt(timeStr);
            int discount = Integer.parseInt(discountStr);

            if (time < 0 || time > 23) {
                request.setAttribute("error", "時間は0〜23の範囲で入力してください");
                connection.close();
                request.getRequestDispatcher("discount-setting.jsp").forward(request, response);
                return;
            }

            if (discount < 1 || discount > 100) {
                request.setAttribute("error", "割引率は1〜100の範囲で入力してください");
                connection.close();
                request.getRequestDispatcher("discount-setting.jsp").forward(request, response);
                return;
            }

            discountDao.saveDiscountSetting(storeId, time, discount);
            connection.close();

            response.sendRedirect("discount-setting.jsp?success=true");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "数値形式が不正です");
            connection.close();
            request.getRequestDispatcher("discount-setting.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "割引設定に失敗しました: " + e.getMessage());
            connection.close();
            request.getRequestDispatcher("discount-setting.jsp").forward(request, response);
        }
    }
}