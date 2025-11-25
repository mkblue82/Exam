<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Merchandise, bean.MerchandiseImage, bean.Store, java.util.*" %>
<%
    Merchandise merch = (Merchandise) request.getAttribute("merchandise");  // ← "merchandise" に変更
    Store store = (Store) request.getAttribute("store");  // ← store も取得

    if (merch == null) {
        request.setAttribute("errorMessage", "商品情報が取得できませんでした。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品詳細</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.detail-box {
    max-width: 800px;
    margin: 40px auto;
    padding: 25px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 3px 10px rgba(0,0,0,0.1);
}
.detail-image {
    text-align: center;
    margin-bottom: 20px;
}
.detail-image img {
    max-width: 300px;
    border-radius: 8px;
}
.detail-info {
    font-size: 1.1rem;
    line-height: 2;
}
.detail-title {
    font-size: 1.6rem;
    font-weight: bold;
    color: #c07148;
    margin-bottom: 15px;
}
.back-btn {
    margin-top: 30px;
    text-align: center;
}
.back-btn a {
    background: #c07148;
    color: white;
    padding: 10px 25px;
    border-radius: 5px;
    text-decoration: none;
}
.back-btn a:hover {
    opacity: 0.8;
}
</style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />
    <main class="column">
        <div class="detail-box">
            <div class="detail-title"><%= merch.getMerchandiseName() %></div>

            <!-- 画像 -->
            <div class="detail-image">
                <% if (merch.getImages() != null && !merch.getImages().isEmpty()) { %>
                    <!-- 新しいServlet経由で画像表示 -->
                    <img src="<%= request.getContextPath() %>/image/<%= merch.getImages().get(0).getImageId() %>"
                         alt="<%= merch.getMerchandiseName() %>">
                <% } else { %>
                    <p>画像なし</p>
                <% } %>
            </div>

            <!-- 商品情報 -->
            <div class="detail-info">
                <% if (store != null) { %>
                    <p>店舗名：<%= store.getStoreName() %></p>
                <% } %>
                <p>価格：<strong>¥<%= merch.getPrice() %></strong></p>
                <p>在庫：<%= merch.getStock() %></p>
                <p>消費期限：<%= merch.getUseByDate() %></p>
            </div>

            <!-- 戻るボタン -->
            <div class="back-btn">
                <a href="<%= request.getContextPath() %>/foodloss/Menu.action">商品一覧へ戻る</a>
            </div>
        </div>
    </main>
    <!-- フッター -->
    <jsp:include page="footer.jsp" />
</div>
</body>
</html>