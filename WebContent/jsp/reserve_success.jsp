<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>予約完了</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.success-box {
    max-width: 600px;
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
}
.button-group {
    margin-top: 30px;
}
.button-group a {
    display: inline-block;
    margin: 0 10px;
    padding: 12px 30px;
    border-radius: 5px;
    text-decoration: none;
    transition: opacity 0.3s;
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
                <%= request.getAttribute("message") %>
            </div>
            <p>予約数量: <strong><%= request.getAttribute("quantity") %></strong> 個</p>

            <div class="button-group">
                <a href="${pageContext.request.contextPath}/Menu.action" class="btn-primary">商品一覧へ</a>
                <a href="${pageContext.request.contextPath}/MyReservations.action" class="btn-secondary">予約一覧を見る</a>
            </div>
        </div>
    </main>

    <jsp:include page="footer.jsp" />
</div>
</body>
</html>