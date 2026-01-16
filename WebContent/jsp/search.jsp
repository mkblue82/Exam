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
<title>検索結果</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<style>
/* 検索結果用のスタイル */
.search-section {
    background:#fff;
    padding:20px;
    border-radius:10px;
    margin-bottom:30px;
    box-shadow:0 2px 8px rgba(0,0,0,0.1);
    max-width: 1000px;
    margin-left: auto;
    margin-right: auto;
}

.section-header {
    font-size:1.4rem;
    font-weight:bold;
    color:#333;
    border-left:5px solid #c07148;
    padding-left:15px;
    margin-bottom:20px;
}

.store-list {
    margin-bottom: 30px;
}

.store-card {
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    padding: 20px;
    margin-bottom: 15px;
    background-color: #fafafa;
    transition: background-color 0.2s;
}

.store-card:hover {
    background-color: #f0f0f0;
}

.store-content {
    flex: 1;
}

.store-header {
    margin-bottom: 10px;
}

.store-name-link {
    text-decoration: none;
    color: inherit;
    display: inline-block;
    transition: color 0.2s;
}

.store-name-link:hover {
    color: #c07148;
}

.store-name {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    cursor: pointer;
    transition: color 0.2s;
}

.store-name-link:hover .store-name {
    text-decoration: underline;
}

.store-info {
    color: #666;
    font-size: 13px;
    margin-bottom: 5px;
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

.price-display {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;
    margin-top: 8px;
}

.discounted-price {
    font-size: 1.1rem;
    font-weight: bold;
    color: #d9534f;
}

.original-price {
    font-size: 0.8rem;
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

                // 割引情報マップを取得
                Map<Integer, Boolean> storeDiscountMap =
                    (Map<Integer, Boolean>) request.getAttribute("storeDiscountMap");
                Map<Integer, Integer> storeDiscountRateMap =
                    (Map<Integer, Integer>) request.getAttribute("storeDiscountRateMap");

                // 現在日時を取得（消費期限チェック用）
                Date today = new Date(System.currentTimeMillis());

                // デバッグ出力
                System.out.println("=== search.jsp デバッグ ===");
                System.out.println("itemList: " + (itemList != null ? itemList.size() + "件" : "null"));
                System.out.println("storeList: " + (storeList != null ? storeList.size() + "件" : "null"));
                System.out.println("searchKeyword: " + searchKeyword);
                System.out.println("storeToMerchMap: " + (storeToMerchMap != null ? storeToMerchMap.size() + "店舗" : "null"));
                System.out.println("storeDiscountMap: " + (storeDiscountMap != null ? "あり" : "null"));
            %>

            <!-- ========== 検索結果表示 ========== -->
            <h2 style="text-align:center; margin:30px 0; color:#c07148;">検索結果: "<%= searchKeyword != null ? searchKeyword : "" %>"</h2>

            <!-- 店舗検索結果 -->
            <div class="search-section">
                <div class="section-header">🏪 店舗検索結果</div>

                <% if (storeList != null && !storeList.isEmpty()) { %>
                    <p class="result-count"><%= storeList.size() %>件の店舗が見つかりました</p>

                    <div class="store-list">
                        <% for (Store store : storeList) {
                            // この店舗の割引情報を取得
                            Boolean isDiscountApplied = storeDiscountMap != null ?
                                storeDiscountMap.get(store.getStoreId()) : false;
                            Integer discountRate = storeDiscountRateMap != null ?
                                storeDiscountRateMap.get(store.getStoreId()) : 0;

                            if (isDiscountApplied == null) isDiscountApplied = false;
                            if (discountRate == null) discountRate = 0;
                        %>
                            <div class="store-card">
                                <div class="store-content">
                                    <div class="store-header">
                                        <a href="${pageContext.request.contextPath}/foodloss/StoreMerchandise.action?storeId=<%= store.getStoreId() %>" class="store-name-link">
                                            <div class="store-name"><%= store.getStoreName() %></div>
                                        </a>
                                    </div>

                                    <% if (store.getAddress() != null && !store.getAddress().isEmpty()) { %>
                                    <div class="store-info">
                                        📍 <%= store.getAddress() %>
                                    </div>
                                    <% } %>
                                    <% if (store.getPhone() != null && !store.getPhone().isEmpty()) { %>
                                    <div class="store-info">
                                        📞 <%= store.getPhone() %>
                                    </div>
                                    <% } %>
                                </div>

                                <!-- この店舗の商品を表示 -->
                                <%
                                    List<Merchandise> storeProducts = null;
                                    if (storeToMerchMap != null) {
                                        storeProducts = storeToMerchMap.get(store);
                                    }

                                    // 表示可能な商品をカウント
                                    int storeProductCount = 0;
                                    if (storeProducts != null) {
                                        for (Merchandise m : storeProducts) {
                                            if (m.getStock() > 0) {
                                                Date checkDate = m.getUseByDate();
                                                if (checkDate == null || !checkDate.before(today)) {
                                                    storeProductCount++;
                                                }
                                            }
                                        }
                                    }

                                    if (storeProductCount > 0) {
                                %>
                                    <div style="margin-top:15px; padding-top:15px; border-top:1px solid #ddd;">
                                        <div style="font-weight:bold; margin-bottom:10px; color:#666;">この店舗の商品:</div>

                                        <!-- 割引適用中の通知 -->
                                        <% if (isDiscountApplied && discountRate > 0) { %>
                                            <div style="color:#856404; font-size:1rem; font-weight:bold; margin-bottom:10px;">
                                                🎉 現在、全商品<%= discountRate %>%OFF！
                                            </div>
                                        <% } %>

                                        <div class="merch-list">
                                            <%
                                                int displayedCount = 0;
                                                final int MAX_DISPLAY = 4; // 最大表示件数

                                                for (Merchandise merch : storeProducts) {
                                                    // 既に4個表示したら終了
                                                    if (displayedCount >= MAX_DISPLAY) {
                                                        break;
                                                    }

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

                                                    displayedCount++;
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
                                                        if (isDiscountApplied && discountRate > 0) {
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

                                        <% if (storeProductCount > MAX_DISPLAY) { %>
                                            <div style="text-align:center; margin-top:15px;">
                                                <a href="${pageContext.request.contextPath}/foodloss/StoreMerchandise.action?storeId=<%= store.getStoreId() %>"
                                                   style="display:inline-block; padding:8px 20px; background:#c07148; color:#fff;
                                                          text-decoration:none; border-radius:5px; font-size:14px;">
                                                    すべての商品を見る (<%= storeProductCount %>件)
                                                </a>
                                            </div>
                                        <% } %>
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                <% } else { %>
                    <p class="no-result">該当する店舗はありませんでした。</p>
                <% } %>
            </div>

            <!-- 商品検索結果 -->
            <div class="search-section">
                <div class="section-header">🛒 商品検索結果</div>

                <%
                    // 表示可能な商品をカウント
                    int displayableItemCount = 0;
                    if (itemList != null) {
                        for (Merchandise m : itemList) {
                            if (m.getStock() > 0) {
                                Date checkDate = m.getUseByDate();
                                if (checkDate == null || !checkDate.before(today)) {
                                    displayableItemCount++;
                                }
                            }
                        }
                    }
                %>

                <% if (displayableItemCount > 0) { %>
                    <p class="result-count"><%= displayableItemCount %>件の商品が見つかりました</p>

                    <div class="merch-list">
                        <% for (Merchandise merch : itemList) {
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

                            // この商品の店舗の割引情報を取得
                            Boolean isDiscountApplied = storeDiscountMap != null ?
                                storeDiscountMap.get(merch.getStoreId()) : false;
                            Integer discountRate = storeDiscountRateMap != null ?
                                storeDiscountRateMap.get(merch.getStoreId()) : 0;

                            if (isDiscountApplied == null) isDiscountApplied = false;
                            if (discountRate == null) discountRate = 0;
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
                                    if (isDiscountApplied && discountRate > 0) {
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
                    <p class="no-result">該当する商品はありませんでした。</p>
                <% } %>
            </div>

            <!-- 結果が何もない場合 -->
            <%
                boolean hasDisplayableStores = (storeList != null && !storeList.isEmpty());
                boolean hasNoResults = (displayableItemCount == 0 && !hasDisplayableStores);
            %>

            <% if (hasNoResults) { %>
                <p style="text-align:center; padding:50px; color:#999; font-size:1.2rem;">
                    「<%= searchKeyword != null ? searchKeyword : "" %>」に一致する店舗・商品は見つかりませんでした。
                </p>
            <% } %>

            <p style="text-align:center; margin-top:30px;">
                <a href="${pageContext.request.contextPath}/foodloss/Menu.action"
                   style="display:inline-block; padding:12px 30px; background:#c07148; color:#fff;
                          text-decoration:none; border-radius:8px; font-weight:bold;">
                    ホームに戻る
                </a>
            </p>

        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="footer.jsp" />

</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>