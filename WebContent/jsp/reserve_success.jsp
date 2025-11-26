<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Booking, bean.Merchandise" %>
<%
    Booking booking = (Booking) request.getAttribute("booking");
    Merchandise merchandise = (Merchandise) request.getAttribute("merchandise");
    Integer totalPrice = (Integer) request.getAttribute("totalPrice");
    String message = (String) request.getAttribute("message");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>予約完了</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.success-box {
    max-width: 700px;
    margin: 100px auto;
    padding: 40px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 3px 10px rgba(0,0,0,0.1);
    text-align: center;
}
.success-icon {
    font-size: 4rem;
    color: #5cb85c;
    margin-bottom: 20px;
}
.success-message {
    font-size: 1.5rem;
    color: #333;
    margin-bottom: 30px;
    font-weight: bold;
}
.reservation-details {
    background: #f9f9f9;
    padding: 25px;
    border-radius: 8px;
    margin: 30px 0;
    text-align: left;
}
.detail-row {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #e0e0e0;
}
.detail-row:last-child {
    border-bottom: none;
}
.detail-label {
    width: 150px;
    font-weight: bold;
    color: #555;
}
.detail-value {
    flex: 1;
    color: #333;
}
.total-price {
    font-size: 1.3rem;
    color: #c07148;
    font-weight: bold;
}
.button-group {
    margin-top: 30px;
    display: flex;
    gap: 15px;
    justify-content: center;
}
.button-group a {
    padding: 12px 30px;
    border-radius: 5px;
    text-decoration: none;
    transition: opacity 0.3s;
    font-size: 1rem;
}
.btn-primary {
    background: #c07148;
    color: white;
}
.btn-secondary {
    background: #999;
    color: white;
}
.button-group a:hover {
    opacity: 0.8;
}
</style>
</head>
<body>
<div id="container">
    <jsp:include page="header_user.jsp" />
    <main class="column">
        <div class="success-box">
            <div class="success-icon">✓</div>
            <div class="success-message">
                <%= message != null ? message : "予約が完了しました" %>
            </div>

            <% if (booking != null && merchandise != null) { %>
            <div class="reservation-details">
                <div class="detail-row">
                    <div class="detail-label">商品名:</div>
                    <div class="detail-value"><%= merchandise.getMerchandiseName() %></div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">予約数量:</div>
                    <div class="detail-value"><%= booking.getCount() %> 個</div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">単価:</div>
                    <div class="detail-value">¥<%= merchandise.getPrice() %></div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">合計金額:</div>
                    <div class="detail-value total-price">¥<%= totalPrice != null ? totalPrice : (merchandise.getPrice() * booking.getCount()) %></div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">受け取り日時:</div>
                    <div class="detail-value"><%= booking.getPickupTime() %></div>
                </div>
            </div>
            <% } %>

            <div class="button-group">
                <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="btn-primary">商品一覧へ</a>
                <a href="${pageContext.request.contextPath}/foodloss/BookingUserList.action" class="btn-secondary">予約一覧を見る</a>
            </div>
        </div>
    </main>
    <jsp:include page="footer.jsp" />
</div>
</body>
</html>