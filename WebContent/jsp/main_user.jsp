<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, bean.Store, bean.Merchandise, bean.MerchandiseImage" %>

<%
    HttpSession userSession = request.getSession(false);
    if (userSession == null || userSession.getAttribute("user") == null) {
        request.setAttribute("errorMessage", "セッションが切れています。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }
%>


<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>出店店舗と商品一覧</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.store-box { background:#fff; padding:20px; border-radius:10px; margin-bottom:30px; box-shadow:0 2px 8px rgba(0,0,0,0.1);}
.store-title { font-size:1.6rem; font-weight:bold; color:#c07148; border-bottom:2px solid #c07148; padding-bottom:10px; margin-bottom:15px;}
.merch-item { padding:10px 15px; margin-bottom:10px; border-left:4px solid #c07148; background:#fafafa; border-radius:5px; display:flex; gap:15px;}
.merch-info { flex:1; }
.merch-image img { max-width:120px; border-radius:5px; }
</style>
</head>
<body>

<div id="container">
    <jsp:include page="header_user.jsp" />

    <main class="column">
        <div class="main-contents">
            <h2 style="text-align:center; margin:30px 0; color:#c07148;">出店店舗と商品一覧</h2>

            <%
                Map<Store, List<Merchandise>> shopMerchMap =
                    (Map<Store, List<Merchandise>>) request.getAttribute("shopMerchMap");
            %>

            <% if (shopMerchMap != null) { %>
                <% for (Map.Entry<Store, List<Merchandise>> entry : shopMerchMap.entrySet()) {
                    Store store = entry.getKey();
                    List<Merchandise> merchList = entry.getValue();
                %>

                    <div class="store-box">
                        <div class="store-title">
                            店舗名：<%= store.getStoreName() %>（ID: <%= store.getStoreId() %>）
                        </div>

                        <% if (merchList != null && !merchList.isEmpty()) { %>
                            <% for (Merchandise m : merchList) { %>
                                <div class="merch-item">
                                    <div class="merch-image">
                                        <% if (m.getImages() != null && !m.getImages().isEmpty()) { %>
                                            <img src="<%= request.getContextPath() + "/ImageDisplay.action?imageId=" + m.getImages().get(0).getImageId() %>"
                                                 alt="<%= m.getMerchandiseName() %>">
                                        <% } else { %>
                                            <span>画像なし</span>
                                        <% } %>
                                    </div>
                                    <div class="merch-info">
                                        <strong>商品名：</strong><%= m.getMerchandiseName() %><br>
                                        <strong>値段：</strong><%= m.getPrice() %> 円<br>
                                        <strong>在庫：</strong><%= m.getStock() %><br>
                                        <strong>消費期限：</strong><%= m.getUseByDate() %><br>
                                    </div>
                                </div>
                            <% } %>
                        <% } else { %>
                            <p>（この店舗の商品はありません）</p>
                        <% } %>
                    </div>

                <% } %>
            <% } else { %>
                <p style="text-align:center;">商品情報が取得できませんでした。</p>
            <% } %>

        </div>
    </main>

    <jsp:include page="footer.jsp" />
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
