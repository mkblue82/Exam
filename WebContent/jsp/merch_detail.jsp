<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Merchandise, bean.MerchandiseImage, bean.Store, java.util.*" %>
<%
    Merchandise merch = (Merchandise) request.getAttribute("merchandise");
    Store store = (Store) request.getAttribute("store");

    // 割引情報
    Boolean isDiscountApplied = (Boolean) request.getAttribute("isDiscountApplied");
    Integer discountRate = (Integer) request.getAttribute("discountRate");

    // デバッグ出力
    System.out.println("=== 商品詳細ページ デバッグ ===");
    System.out.println("merchandise: " + (merch != null ? "あり" : "null"));
    if (merch != null) {
        System.out.println("商品名: " + merch.getMerchandiseName());
        System.out.println("価格: " + merch.getPrice());
        System.out.println("在庫: " + merch.getStock());
        System.out.println("消費期限: " + merch.getUseByDate());
    }
    System.out.println("store: " + (store != null ? store.getStoreName() : "null"));
    System.out.println("割引適用: " + isDiscountApplied);
    System.out.println("割引率: " + discountRate);

    if (merch == null) {
        request.setAttribute("errorMessage", "商品情報が取得できませんでした。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
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
    text-align: center;
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


.price-display {
    display: inline-flex;
    flex-direction: column;
    gap: 3px;
}
.discounted-price {
    font-size: 1.3rem;
    font-weight: bold;
    color: #d9534f;
}
.original-price {
    font-size: 0.9rem;
    color: #999;
    text-decoration: line-through;
}

.reserve-section {
    margin: 30px 0;
    padding: 20px;
    border-radius: 8px;
}

.reserve-btn {
    text-align: center;
}
.reserve-btn button {
    display: inline-block;
    background: #c07148;
    color: white;
    padding: 15px 50px;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    font-weight: bold;
    font-family: inherit;
    cursor: pointer;
    transition: background-color 0.3s, transform 0.1s;
}
.reserve-btn button:hover {
    background-color: #a85d38;
    transform: translateY(-2px);
}
.reserve-btn button:active {
    transform: scale(0.98);
}
.reserve-btn button:disabled {
    background: #ccc;
    cursor: not-allowed;
    transform: none;
}

.back-btn {
    margin-top: 30px;
    text-align: center;
}
.back-btn a {
    display: inline-block;
    background: #ccc;
    color: #333;
    padding: 12px 40px;
    border-radius: 5px;
    text-decoration: none;
    font-size: 16px;
    font-weight: bold;
    transition: all 0.3s;
}
.back-btn a:hover {
    background-color: #c07148;
    color: #fff;
    transform: translateY(-3px);
}

.stock-warning {
    color: #d9534f;
    font-weight: bold;
}

.tag-container {
    display: inline-flex;
    flex-wrap: wrap;
    gap: 8px;
    align-items: center;
}

.tag-badge {
    display: inline-block;
    background: #fff3e0;
    color: #c07148;
    padding: 4px 12px;
    border-radius: 15px;
    font-size: 0.9rem;
    font-weight: normal;
    border: 1px solid #c07148;
}

.store-link {
    color: inherit;
    text-decoration: none;
    transition: text-decoration 0.3s;
}
.store-link:hover {
    text-decoration: underline;
}
</style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="detail-box">
            <div class="detail-title"><%= merch.getMerchandiseName() %></div>

            <!-- 画像 -->
            <div class="detail-image">
                <% if (merch.getImages() != null && !merch.getImages().isEmpty()) { %>
                    <img src="<%= request.getContextPath() %>/image/<%= merch.getImages().get(0).getImageId() %>"
                         alt="<%= merch.getMerchandiseName() %>">
                <% } else { %>
                    <p>画像なし</p>
                <% } %>
            </div>

            <!-- 商品情報 -->
            <div class="detail-info">
                <% if (store != null) { %>
                    <p>店舗名：
                        <a href="<%= request.getContextPath() %>/foodloss/StoreMerchandise.action?storeId=<%= store.getStoreId() %>"
                           class="store-link">
                            <%= store.getStoreName() %>
                        </a>
                    </p>
                <% } %>
                <p>価格：
                    <%
                        int originalPrice = merch.getPrice();
                        if (isDiscountApplied != null && isDiscountApplied && discountRate != null) {
                            int discountedPrice = (int)(originalPrice * (100 - discountRate) / 100.0);
                    %>
                        <span class="price-display">
                            <span class="discounted-price">¥<%= discountedPrice %></span>
                            <span class="original-price">(元価格: ¥<%= originalPrice %>)</span>
                        </span>
                    <% } else { %>
                        <strong>¥<%= originalPrice %></strong>
                    <% } %>
                </p>
                <p>在庫：<strong><%= merch.getStock() %></strong>個
                    <% if (merch.getStock() <= 0) { %>
                        <span class="stock-warning">（在庫切れ）</span>
                    <% } %>
                </p>
                <% if (merch.getUseByDate() != null) { %>
                    <p>消費期限：<%= merch.getUseByDate() %></p>
                <% } %>

                <!-- タグ表示 -->
                <% if (merch.getMerchandiseTag() != null && !merch.getMerchandiseTag().trim().isEmpty()) { %>
                    <p>
                        タグ：
                        <span class="tag-container">
                            <%
                            String[] tags = merch.getMerchandiseTag().split(",");
                            for (String tag : tags) {
                                tag = tag.trim();
                                if (!tag.isEmpty()) {
                            %>
                                <span class="tag-badge"><%= tag %></span>
                            <%
                                }
                            }
                            %>
                        </span>
                    </p>
                <% } %>
            </div>

            <!-- 予約ボタン -->
            <% if (merch.getStock() > 0) { %>
                <div class="reserve-section">
                    <div class="reserve-btn">
                        <form action="<%= request.getContextPath() %>/foodloss/ReserveConfirm.action" method="get">
                            <input type="hidden" name="merchandiseId" value="<%= merch.getMerchandiseId() %>">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit">この商品を予約する</button>
                        </form>
                    </div>
                </div>
            <% } else { %>
                <div class="reserve-section">
                    <p class="stock-warning" style="text-align:center;">申し訳ございません。現在在庫切れです。</p>
                </div>
            <% } %>

            <!-- 戻るボタン -->
            <div class="back-btn">
                <a href="<%= request.getContextPath() %>/foodloss/Menu.action">商品一覧へ戻る</a>
            </div>
        </div>
        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="footer.jsp" />
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>