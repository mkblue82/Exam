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
/* 店舗ボックス */
.store-box {
    background:#fff;
    padding:20px;
    border-radius:10px;
    margin-bottom:30px;
    box-shadow:0 2px 8px rgba(0,0,0,0.1);
}

/* 店舗タイトル */
.store-title {
    font-size:1.6rem;
    font-weight:bold;
    color:#c07148;
    border-bottom:2px solid #c07148;
    padding-bottom:10px;
    margin-bottom:15px;
}

/* ☆ 横スクロール商品一覧 */
.merch-row {
    display:flex;
    gap:20px;
    overflow-x:auto;
    padding:10px 0;
}

/* 商品カード */
.merch-card {
    min-width:150px;
    background:#fff;
    border-radius:10px;
    box-shadow:0 2px 5px rgba(0,0,0,0.1);
    padding:10px;
    text-align:center;
}

/* 商品画像枠 */
.merch-image {
    width:100%;
    height:120px;
    background:#fafafa;
    border-radius:5px;
    display:flex;
    justify-content:center;
    align-items:center;
    overflow:hidden;
}

.merch-image img {
    width:100%;
    height:100%;
    object-fit:cover;
}

/* 値段 */
.merch-price {
    margin-top:8px;
    font-weight:bold;
    color:#c07148;
    font-size:1.1rem;
}
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

                    <!-- 店舗名リンク -->
                    <div class="store-title">
                        店舗名：
                        <a href="StoreInfo.action?storeId=<%= store.getStoreId() %>"
                           style="text-decoration:none; color:#c07148;">
                            <%= store.getStoreName() %>
                        </a>
                    </div>

                    <!-- 商品横スクロール -->
                    <div class="merch-row">

                        <% if (merchList != null && !merchList.isEmpty()) { %>

                            <% for (Merchandise m : merchList) { %>

                                <div class="merch-card">

                                    <!-- 商品画像 -->
                                    <div class="merch-image">
                                        <% if (m.getImages() != null && !m.getImages().isEmpty()) { %>
                                            <img src="<%= request.getContextPath() + "/ImageDisplay.action?imageId=" + m.getImages().get(0).getImageId() %>">
                                        <% } else { %>
                                            <span style="color:#999;">画像なし</span>
                                        <% } %>
                                    </div>

                                    <!-- 値段のみ表示 -->
                                    <div class="merch-price">
                                        <%= m.getPrice() %>円
                                    </div>

                                </div>

                            <% } %>

                        <% } else { %>
                            <p>この店舗の商品はありません</p>
                        <% } %>

                    </div><!-- /.merch-row -->

                </div><!-- /.store-box -->

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
