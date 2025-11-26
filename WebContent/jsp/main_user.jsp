<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, bean.Store, bean.Merchandise, bean.MerchandiseImage" %>

<%
    HttpSession userSession = request.getSession(false);
    if (userSession == null || userSession.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/index.jsp");
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
/* 店舗枠 */
.store-box {
    background:#fff;
    padding:20px;
    border-radius:10px;
    margin-bottom:40px;
    box-shadow:0 2px 8px rgba(0,0,0,0.1);
}

/* 店舗名 */
.store-title {
    font-size:1.6rem;
    font-weight:bold;
    color:#c07148;
    border-bottom:2px solid #c07148;
    padding-bottom:8px;
    margin-bottom:15px;
}

/* 商品横並び全体 */
.merch-list {
    display:flex;
    gap:20px;
    flex-wrap:wrap;
}

/* 商品1つの箱 */
.merch-item {
    width:200px;
    padding:15px;
    border-radius:10px;
    background:#fafafa;
    box-shadow:0 1px 5px rgba(0,0,0,0.1);
    text-align:center;
}

/* 商品画像 */
.merch-image img {
    width:180px;
    height:130px;
    object-fit:cover;
    border-radius:8px;
}

/* 金額表示 */
.merch-price {
    margin-top:8px;
    font-size:1.1rem;
    font-weight:bold;
    color:#c07148;
}

/* 画像がない場合 */
.no-image {
    width:180px;
    height:130px;
    background:#ddd;
    display:flex;
    align-items:center;
    justify-content:center;
    color:#666;
    border-radius:8px;
}
</style>
</head>

<body>
<div id="container">

    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />

    <main class="column">
        <div class="main-contents">

            <%
                // 検索結果を取得
                List<Merchandise> itemList = (List<Merchandise>) request.getAttribute("itemList");
                String searchKeyword = (String) request.getAttribute("searchKeyword");

                // 通常の店舗ごとの商品マップを取得
                Map<Store, List<Merchandise>> shopMerchMap =
                    (Map<Store, List<Merchandise>>) request.getAttribute("shopMerchMap");

                // デバッグ出力
                System.out.println("=== JSP デバッグ ===");
                System.out.println("itemList: " + (itemList != null ? itemList.size() + "件" : "null"));
                System.out.println("searchKeyword: " + searchKeyword);
                System.out.println("shopMerchMap: " + (shopMerchMap != null ? "あり" : "null"));
            %>

            <% if (itemList != null) { %>
                <!-- ========== 検索結果表示 ========== -->
                <h2 style="text-align:center; margin:30px 0; color:#c07148;">検索結果: "<%= searchKeyword %>"</h2>

                <% if (itemList.isEmpty()) { %>
                    <p style="text-align:center;">該当する商品はありませんでした。</p>
                    <p style="text-align:center;"><a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホームに戻る</a></p>
                <% } else { %>
                    <p style="text-align:center;"><%= itemList.size() %>件の商品が見つかりました</p>

                    <div class="store-box">
                        <div class="merch-list">
                            <% for (Merchandise merch : itemList) {
                                // 在庫0の商品はスキップ
                                if (merch.getStock() == 0) {
                                    continue;
                                }
                            %>
                                <div class="merch-item">
                                    <!-- 画像クリック → 商品詳細へ -->
                                    <a href="<%= request.getContextPath() %>/merch/<%= merch.getMerchandiseId() %>">
                                        <div class="merch-image">
                                            <%
                                            List<MerchandiseImage> images = merch.getImages();
                                            if (images != null && !images.isEmpty()) {
                                                MerchandiseImage img = images.get(0);
                                            %>
                                                <img src="<%= request.getContextPath() %>/image/<%= img.getImageId() %>"
                                                     alt="<%= merch.getMerchandiseName() %>">
                                            <%
                                            } else {
                                            %>
                                                <div class="no-image">画像なし</div>
                                            <%
                                            }
                                            %>
                                        </div>
                                    </a>

                                    <!-- 商品名と値段 -->
                                    <div style="margin-top:8px;"><%= merch.getMerchandiseName() %></div>
                                    <div class="merch-price">¥ <%= merch.getPrice() %></div>
                                </div>
                            <% } %>
                        </div>
                    </div>
                <% } %>

            <% } else if (shopMerchMap != null) { %>
                <!-- ========== 通常の店舗ごと表示 ========== -->
                <h2 style="text-align:center; margin:30px 0; color:#c07148;">出店店舗と商品一覧</h2>

                <% for (Map.Entry<Store, List<Merchandise>> entry : shopMerchMap.entrySet()) {
                    Store store = entry.getKey();
                    List<Merchandise> merchList = entry.getValue();
                %>

                <div class="store-box">

                    <!-- 店舗名（クリックで店舗詳細へ） -->
                    <div class="store-title">
                        <a href="StoreInfo.action?storeId=<%= store.getStoreId() %>"
                           style="text-decoration:none;color:#c07148;">
                           <%= store.getStoreName() %>
                        </a>
                    </div>

                    <% if (merchList != null && !merchList.isEmpty()) { %>

                        <div class="merch-list">

                        <% for (Merchandise merch : merchList) {
                            // 在庫0の商品はスキップ
                            if (merch.getStock() == 0) {
                                continue;
                            }
                        %>

                            <div class="merch-item">
                                <!-- 画像クリック → 商品詳細へ -->
                                <a href="<%= request.getContextPath() %>/merch/<%= merch.getMerchandiseId() %>">
                                    <div class="merch-image">
                                        <%
                                        List<MerchandiseImage> images = merch.getImages();
                                        if (images != null && !images.isEmpty()) {
                                            MerchandiseImage img = images.get(0);
                                        %>
                                            <img src="<%= request.getContextPath() %>/image/<%= img.getImageId() %>"
                                                 alt="<%= merch.getMerchandiseName() %>">
                                        <%
                                        } else {
                                        %>
                                            <div class="no-image">画像なし</div>
                                        <%
                                        }
                                        %>
                                    </div>
                                </a>

                                <!-- 商品名と値段 -->
                                <div style="margin-top:8px;"><%= merch.getMerchandiseName() %></div>
                                <div class="merch-price">
                                    ¥ <%= merch.getPrice() %>
                                </div>
                            </div>

                        <% } %>

                        </div>

                    <% } else { %>

                        <p>この店舗の商品はありません。</p>

                    <% } %>

                </div>

                <% } %>

            <% } else { %>

                <p style="text-align:center;">商品情報が取得できませんでした。</p>

            <% } %>

        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="footer.jsp" />

</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>