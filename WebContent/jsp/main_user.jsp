<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, bean.Store, bean.Merchandise, bean.MerchandiseImage" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Comparator" %>

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

.store-title a {
    text-decoration:none;
    color:#c07148;
    transition: color 0.2s, opacity 0.2s;
}

.store-title a:hover {
    color:#a85d38;
    opacity: 0.8;
    text-decoration: underline;
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
</style>
</head>

<body>
<div id="container">

    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />

    <main class="column">
        <div class="main-contents">

            <%
                // 通常の店舗ごとの商品マップを取得
                Map<Store, List<Merchandise>> shopMerchMap =
                    (Map<Store, List<Merchandise>>) request.getAttribute("shopMerchMap");

                // 割引情報マップを取得
                Map<Integer, Boolean> storeDiscountMap =
                    (Map<Integer, Boolean>) request.getAttribute("storeDiscountMap");
                Map<Integer, Integer> storeDiscountRateMap =
                    (Map<Integer, Integer>) request.getAttribute("storeDiscountRateMap");

                // 現在日時を取得（消費期限チェック用）
                final Date today = new Date(System.currentTimeMillis());

                // デバッグ出力
                System.out.println("=== main_user.jsp デバッグ ===");
                System.out.println("shopMerchMap: " + (shopMerchMap != null ? "あり" : "null"));
                System.out.println("storeDiscountMap: " + (storeDiscountMap != null ? "あり" : "null"));
                System.out.println("今日の日付: " + today);
            %>

            <% if (shopMerchMap != null) { %>
                <!-- ========== 通常の店舗ごと表示 ========== -->
                <h2 style="text-align:center; margin:30px 0; color:#c07148;">出店店舗と商品一覧</h2>

                <%
                    // ========== 店舗をソート：商品がある店舗を上に ==========
                    List<Map.Entry<Store, List<Merchandise>>> sortedEntries = new ArrayList<>(shopMerchMap.entrySet());

                    Collections.sort(sortedEntries, new Comparator<Map.Entry<Store, List<Merchandise>>>() {
                        @Override
                        public int compare(Map.Entry<Store, List<Merchandise>> e1, Map.Entry<Store, List<Merchandise>> e2) {
                            List<Merchandise> list1 = e1.getValue();
                            List<Merchandise> list2 = e2.getValue();

                            // 店舗1の表示可能な商品数をカウント
                            int count1 = 0;
                            if (list1 != null) {
                                for (Merchandise m : list1) {
                                    if (m.getStock() > 0) {
                                        Date checkDate = m.getUseByDate();
                                        if (checkDate == null || !checkDate.before(today)) {
                                            count1++;
                                        }
                                    }
                                }
                            }

                            // 店舗2の表示可能な商品数をカウント
                            int count2 = 0;
                            if (list2 != null) {
                                for (Merchandise m : list2) {
                                    if (m.getStock() > 0) {
                                        Date checkDate = m.getUseByDate();
                                        if (checkDate == null || !checkDate.before(today)) {
                                            count2++;
                                        }
                                    }
                                }
                            }

                            // 商品数が多い順（降順）
                            return Integer.compare(count2, count1);
                        }
                    });
                %>

                <% for (Map.Entry<Store, List<Merchandise>> entry : sortedEntries) {
                    Store store = entry.getKey();
                    List<Merchandise> merchList = entry.getValue();

                    // この店舗の割引情報を取得
                    Boolean isDiscountApplied = storeDiscountMap != null ?
                        storeDiscountMap.get(store.getStoreId()) : false;
                    Integer discountRate = storeDiscountRateMap != null ?
                        storeDiscountRateMap.get(store.getStoreId()) : 0;

                    if (isDiscountApplied == null) isDiscountApplied = false;
                    if (discountRate == null) discountRate = 0;
                %>

                <div class="store-box">

                    <!-- 店舗名（クリックで店舗商品一覧へ） -->
                    <div class="store-title">
                        <a href="StoreMerchandise.action?storeId=<%= store.getStoreId() %>">
                           <%= store.getStoreName() %>
                        </a>
                    </div>

                    <% if (merchList != null && !merchList.isEmpty()) {
                        // ========== 表示可能な商品をカウント ==========
                        int displayCount = 0;
                        for (Merchandise m : merchList) {
                            if (m.getStock() > 0) {
                                Date checkDate = m.getUseByDate();
                                if (checkDate == null || !checkDate.before(today)) {
                                    displayCount++;
                                }
                            }
                        }
                    %>

                        <% if (displayCount > 0) { %>
                        <div class="merch-list">

                        <% for (Merchandise merch : merchList) {
                            // 在庫0の商品はスキップ
                            if (merch.getStock() == 0) {
                                continue;
                            }

                            // ========== 消費期限チェック ==========
                            Date useByDate = merch.getUseByDate();
                            if (useByDate != null && useByDate.before(today)) {
                                // 消費期限が切れている場合はスキップ
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
                                <!-- 期限間近の警告バッジ -->
                                <% if (isExpiringSoon) { %>
                                    <div class="expiry-badge">🔥 まもなく期限切れ</div>
                                <% } %>

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

                                <!-- 商品名 -->
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

                        <p>この店舗の商品はありません。</p>

                        <% } %>

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