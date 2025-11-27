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

/* 検索結果用のスタイル追加 */
.search-section {
    background:#fff;
    padding:20px;
    border-radius:10px;
    margin-bottom:30px;
    box-shadow:0 2px 8px rgba(0,0,0,0.1);
}

.section-header {
    font-size:1.4rem;
    font-weight:bold;
    color:#333;
    border-left:5px solid #c07148;
    padding-left:15px;
    margin-bottom:20px;
}

.store-card {
    background:#f9f9f9;
    padding:20px;
    border-radius:8px;
    margin-bottom:15px;
    border:1px solid #e0e0e0;
    transition: all 0.3s;
}

.store-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.store-card-title {
    font-size:1.3rem;
    font-weight:bold;
    color:#c07148;
    margin-bottom:10px;
}

.store-card-info {
    color:#666;
    font-size:0.95rem;
    line-height:1.6;
}

.result-count {
    text-align:center;
    color:#666;
    font-size:1rem;
    margin:10px 0;
}

.no-result {
    text-align:center;
    color:#999;
    padding:30px;
    background:#f9f9f9;
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
                List<Store> storeList = (List<Store>) request.getAttribute("storeList");
                Map<Store, List<Merchandise>> storeToMerchMap = (Map<Store, List<Merchandise>>) request.getAttribute("storeToMerchMap");
                String searchKeyword = (String) request.getAttribute("searchKeyword");

                // 通常の店舗ごとの商品マップを取得
                Map<Store, List<Merchandise>> shopMerchMap =
                    (Map<Store, List<Merchandise>>) request.getAttribute("shopMerchMap");

                // デバッグ出力
                System.out.println("=== JSP デバッグ ===");
                System.out.println("itemList: " + (itemList != null ? itemList.size() + "件" : "null"));
                System.out.println("storeList: " + (storeList != null ? storeList.size() + "件" : "null"));
                System.out.println("searchKeyword: " + searchKeyword);
                System.out.println("shopMerchMap: " + (shopMerchMap != null ? "あり" : "null"));
            %>

            <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                <!-- ========== 検索結果表示 ========== -->
                <h2 style="text-align:center; margin:30px 0; color:#c07148;">検索結果: "<%= searchKeyword %>"</h2>

                <!-- 店舗検索結果 -->
                <div class="search-section">
                    <div class="section-header">🏪 店舗検索結果</div>

                    <% if (storeList != null && !storeList.isEmpty()) { %>
                        <p class="result-count"><%= storeList.size() %>件の店舗が見つかりました</p>

                        <% for (Store store : storeList) { %>
                            <div class="store-card">
                                <div class="store-card-title">
                                    <a href="StoreInfo.action?storeId=<%= store.getStoreId() %>"
                                       style="text-decoration:none; color:#c07148;">
                                        <%= store.getStoreName() %>
                                    </a>
                                </div>
                                <div class="store-card-info">
                                    <p>📍 <%= store.getAddress() %></p>
                                    <p>📞 <%= store.getPhone() %></p>
                                </div>

                                <!-- この店舗の商品を表示 -->
                                <%
                                    List<Merchandise> storeProducts = null;
                                    if (storeToMerchMap != null) {
                                        storeProducts = storeToMerchMap.get(store);
                                    }
                                    if (storeProducts != null && !storeProducts.isEmpty()) {
                                %>
                                    <div style="margin-top:15px; padding-top:15px; border-top:1px solid #ddd;">
                                        <div style="font-weight:bold; margin-bottom:10px; color:#666;">この店舗の商品:</div>
                                        <div class="merch-list">
                                            <% for (Merchandise merch : storeProducts) { %>
                                                <div class="merch-item">
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
                                                    <div style="margin-top:8px;"><%= merch.getMerchandiseName() %></div>
                                                    <div class="merch-price">¥ <%= merch.getPrice() %></div>
                                                </div>
                                            <% } %>
                                        </div>
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="no-result">該当する店舗はありませんでした。</p>
                    <% } %>
                </div>

                <!-- 商品検索結果 -->
                <div class="search-section">
                    <div class="section-header">🛒 商品検索結果</div>

                    <% if (itemList != null && !itemList.isEmpty()) { %>
                        <p class="result-count"><%= itemList.size() %>件の商品が見つかりました</p>

                        <div class="merch-list">
                            <% for (Merchandise merch : itemList) { %>
                                <div class="merch-item">
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
                                    <div style="margin-top:8px;"><%= merch.getMerchandiseName() %></div>
                                    <div class="merch-price">¥ <%= merch.getPrice() %></div>
                                </div>
                            <% } %>
                        </div>
                    <% } else { %>
                        <p class="no-result">該当する商品はありませんでした。</p>
                    <% } %>
                </div>

                <!-- 結果が何もない場合 -->
                <% if ((itemList == null || itemList.isEmpty()) && (storeList == null || storeList.isEmpty())) { %>
                    <p style="text-align:center; padding:50px; color:#999; font-size:1.2rem;">
                        「<%= searchKeyword %>」に一致する店舗・商品は見つかりませんでした。
                    </p>
                <% } %>

                <p style="text-align:center; margin-top:30px;">
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action"
                       style="display:inline-block; padding:12px 30px; background:#c07148; color:#fff;
                              text-decoration:none; border-radius:8px; font-weight:bold;">
                        ホームに戻る
                    </a>
                </p>

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

                        <% for (Merchandise merch : merchList) { %>

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

                                <!-- 値段のみ表示 -->
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