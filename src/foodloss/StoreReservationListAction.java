package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Booking;
import dao.BookingDAO;
import tool.Action;

public class StoreReservationListAction extends Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BookingDAO dao = new BookingDAO();
        List<Booking> list = dao.findAll();
        request.setAttribute("reservationList", list);
        return "/jsp/reservation_list.jsp";
    }
}
