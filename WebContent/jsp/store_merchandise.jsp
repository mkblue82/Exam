<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, bean.Store, bean.Merchandise, bean.MerchandiseImage" %>
<%@ page import="java.sql.Date" %>

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
<title>店舗商品一覧</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<style>
/* 店舗情報ヘッダー */
.store-header-box {
    text-align:center;
}

.store-title-link {
    text-decoration:none;
    color:#c07148;
    transition: color 0.2s;
}

.store-title-link:hover {
    color:#a85d38;
    text-decoration: underline;
}

.store-main-title {
    font-size:2rem;
    font-weight:bold;
    color:#c07148;
    margin-bottom:15px;
    cursor: pointer;
}

.store-detail-info {
    color:#666;
    font-size:14px;
    margin:8px 0;
}

/* 割引通知 */
.discount-notice {
    text-align: center;
    background-color: #fff3cd;
    color: #856404;
    padding: 10px;
    border-radius: 5px;
    margin: 20px auto;
    max-width: 600px;
    font-weight: bold;
}

/* 商品一覧 */
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
    transition: transform 0.3s, box-shadow 0.3s;
    cursor: pointer;
}

.merch-item:hover {
	transform: translateY(-5px);
	box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.merch-item a {
	text-decoration: none;
	color: inherit;
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

.price-display {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;
    margin-top: 8px;
}

.discounted-price {
    font-size: 1.2rem;
    font-weight: bold;
    color: #d9534f;
}

.original-price {
    font-size: 0.85rem;
    color: #999;
    text-decoration: line-through;
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

/* 期限間近バッジ */
.expiry-badge {
    background:#ff6b6b;
    color:#fff;
    padding:3px 8px;
    border-radius:5px;
    font-size:0.75rem;
    margin-bottom:5px;
    display:inline-block;
}

/* 商品なしメッセージ */
.no-merchandise {
    text-align:center;
    padding:60px 20px;
    color:#999;
    background:#f9f9f9;
    border-radius:8px;
}

/* 戻るボタン */
.back-button-container {
    text-align:center;
    margin-top:30px;
}

.back-button {
    display:inline-block;
    padding:12px 30px;
    background:#999;
    color:#fff;
    text-decoration:none;
    border-radius:8px;
    font-weight:bold;
    transition: background 0.3s;
}

.back-button:hover {
    background-color: #c07148;
    color: #fff;
    transform: translateY(-3px);
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
                // 店舗情報と商品リストを取得
                Store store = (Store) request.getAttribute("store");
                List<Merchandise> merchandiseList = (List<Merchandise>) request.getAttribute("merchandiseList");

                // 割引情報
                Boolean isDiscountApplied = (Boolean) request.getAttribute("isDiscountApplied");
                Integer discountRate = (Integer) request.getAttribute("discountRate");

                // 現在日時を取得（消費期限チェック用）
                Date today = new Date(System.currentTimeMillis());

                // デバッグ出力
                System.out.println("=== store_merchandise.jsp デバッグ ===");
                System.out.println("store: " + (store != null ? store.getStoreName() : "null"));
                System.out.println("merchandiseList: " + (merchandiseList != null ? merchandiseList.size() + "件" : "null"));
                System.out.println("割引適用: " + isDiscountApplied);
                System.out.println("割引率: " + discountRate);
            %>

            <% if (store != null) { %>
                <!-- 店舗情報ヘッダー -->
                <div class="store-header-box">
                    <a href="${pageContext.request.contextPath}/foodloss/StoreInfo.action?storeId=<%= store.getStoreId() %>" class="store-title-link">
                        <h2 class="store-main-title"><%= store.getStoreName() %></h2>
                    </a>
                </div>

                <!-- 割引適用中の通知 -->
                <% if (isDiscountApplied != null && isDiscountApplied && discountRate != null) { %>
                    <div class="discount-notice">
                        現在、全商品<%= discountRate %>%OFF！
                    </div>
                <% } %>

                <!-- 商品一覧 -->
                <h3 style="font-size:1.5rem; font-weight:bold; color:#333; border-left:5px solid #c07148; padding-left:15px; margin-bottom:25px;">
                    🛒 商品一覧
                </h3>

                <%
                    // 表示可能な商品をカウント
                    int displayableCount = 0;
                    if (merchandiseList != null) {
                        for (Merchandise m : merchandiseList) {
                            if (m.getStock() > 0) {
                                Date checkDate = m.getUseByDate();
                                if (checkDate == null || !checkDate.before(today)) {
                                    displayableCount++;
                                }
                            }
                        }
                    }
                %>

                <% if (displayableCount > 0) { %>
                    <div class="merch-list">
                        <% for (Merchandise merch : merchandiseList) {
                            // 在庫0の商品はスキップ
                            if (merch.getStock() == 0) {
                                continue;
                            }

                            // 消費期限チェック
                            Date useByDate = merch.getUseByDate();
                            if (useByDate != null && useByDate.before(today)) {
                                continue;
                            }

                            // 消費期限まで3日以内かチェック
                            boolean isExpiringSoon = false;
                            if (useByDate != null) {
                                long diff = useByDate.getTime() - today.getTime();
                                long daysUntilExpiry = diff / (1000 * 60 * 60 * 24);
                                isExpiringSoon = daysUntilExpiry <= 3;
                            }
                        %>
                            <div class="merch-item">
                                <% if (isExpiringSoon) { %>
                                    <div class="expiry-badge">🔥 まもなく期限切れ</div>
                                <% } %>

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

                                <!-- 価格表示（割引対応） -->
                                <%
                                    int originalPrice = merch.getPrice();
                                    if (isDiscountApplied != null && isDiscountApplied && discountRate != null) {
                                        int discountedPrice = (int)(originalPrice * (100 - discountRate) / 100.0);
                                %>
                                    <div class="price-display">
                                        <span class="discounted-price">¥<%= discountedPrice %></span>
                                        <span class="original-price">(¥<%= originalPrice %>)</span>
                                    </div>
                                <% } else { %>
                                    <div class="merch-price">¥ <%= originalPrice %></div>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                <% } else { %>
                    <div class="no-merchandise">
                        <p>現在、この店舗に販売中の商品はありません。</p>
                    </div>
                <% } %>

                <!-- 戻るボタン -->
                <div class="back-button-container">
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="back-button">
                        ホームに戻る
                    </a>
                </div>

            <% } else { %>
                <p style="text-align:center; padding:50px; color:#999;">
                    店舗情報が取得できませんでした。
                </p>
                <div class="back-button-container">
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="back-button">
                        ホームに戻る
                    </a>
                </div>
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