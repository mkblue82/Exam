<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>検索結果 - タイトル募集</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <header>
        <h1 id="logo"><span>タイトル募集</span></h1>
        <!-- 検索フォーム -->
        <form action="${pageContext.request.contextPath}/foodloss/Search.action" method="get" id="search-form">
            <input type="text" name="keyword" placeholder="商品・店舗を検索..." value="${keyword}" required>
            <button type="submit"><i class="fa fa-search"></i></button>
        </form>
    </header>

    <!-- メニューバー -->
    <div id="menubar">
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホーム<span>Home</span></a></li>
                <li><a href="${pageContext.request.contextPath}/foodloss/MyPage.action">マイページ<span>MyPage</span></a></li>
                <li><a href="${pageContext.request.contextPath}/jsp/booking_list.jsp">予約リスト<span>Reservation List</span></a></li>
                <li><a href="${pageContext.request.contextPath}/foodloss/NotificationSetting.action">通知設定<span>Notification Settings</span></a></li>
            </ul>
        </nav>
    </div>

    <main>
        <h2>検索結果: "<c:out value="${keyword}" />"</h2>

        <!-- エラーメッセージ -->
        <c:if test="${not empty error}">
            <p class="error-message">
                <i class="fa fa-exclamation-circle"></i> <c:out value="${error}" />
            </p>
        </c:if>

        <!-- 検索結果がない場合 -->
        <c:if test="${empty searchResults and empty error}">
            <p class="no-results">検索条件に一致する商品・店舗が見つかりませんでした。</p>
        </c:if>

        <!-- 検索結果がある場合 -->
        <c:if test="${not empty searchResults}">
            <p class="result-count">
                <i class="fa fa-check-circle"></i> ${searchResults.size()}件の結果が見つかりました
            </p>

            <!-- 店舗別に表示 -->
            <c:forEach var="result" items="${searchResults}">
                <section class="shop-section">
                    <h3>
                        <i class="fa fa-store"></i>
                        <c:out value="${result.storeName}" />
                    </h3>

                    <div class="product-grid">
                        <c:forEach var="merchandise" items="${result.merchandises}">
                            <div class="product-card">
                                <!-- デフォルト画像（商品画像フィールドがない場合） -->
                                <div class="product-image">
                                    <img src="${pageContext.request.contextPath}/images/default-product.jpg"
                                         alt="<c:out value='${merchandise.merchandiseName}' />"
                                         onerror="this.src='${pageContext.request.contextPath}/images/no-image.png'">
                                </div>

                                <h4><c:out value="${merchandise.merchandiseName}" /></h4>

                                <div class="product-info">
                                    <p class="price">¥ <fmt:formatNumber value="${merchandise.price}" pattern="#,###" /></p>

                                    <!-- 在庫数表示 -->
                                    <p class="stock">
                                        <i class="fa fa-box"></i> 在庫: ${merchandise.stock}
                                    </p>

                                    <!-- 消費期限表示 -->
                                    <c:if test="${not empty merchandise.useByDate}">
                                        <p class="expiry-date">
                                            <i class="fa fa-calendar"></i>
                                            <fmt:formatDate value="${merchandise.useByDate}" pattern="yyyy/MM/dd" />まで
                                        </p>
                                    </c:if>

                                    <!-- タグ表示 -->
                                    <c:if test="${not empty merchandise.merchandiseTag}">
                                        <p class="tag">
                                            <span class="tag-badge">
                                                <i class="fa fa-tag"></i> <c:out value="${merchandise.merchandiseTag}" />
                                            </span>
                                        </p>
                                    </c:if>

                                    <!-- 割引率表示（店舗に設定されている場合） -->
                                    <c:if test="${result.store.discountRate > 0}">
                                        <span class="discount">${result.store.discountRate}% OFF</span>
                                    </c:if>

                                    <!-- 予約状態表示 -->
                                    <c:if test="${merchandise.bookingStatus}">
                                        <span class="booking-status">予約済み</span>
                                    </c:if>
                                </div>

                                <a href="${pageContext.request.contextPath}/foodloss/ProductDetail.action?id=${merchandise.merchandiseId}"
                                   class="btn">詳細を見る</a>
                            </div>
                        </c:forEach>
                    </div>
                </section>
            </c:forEach>
        </c:if>
    </main>

    <style>
        main {
            padding: 20px;
            max-width: 1200px;
            margin: 0 auto;
        }

        h2 {
            color: #c85a3b;
            margin-bottom: 20px;
            font-size: 24px;
        }

        .result-count {
            color: #666;
            margin-bottom: 30px;
            font-size: 16px;
        }

        .result-count i {
            color: #4CAF50;
        }

        .no-results {
            text-align: center;
            padding: 50px;
            color: #999;
            font-size: 18px;
        }

        .error-message {
            background: #ffebee;
            border-left: 4px solid #f44336;
            padding: 15px;
            margin-bottom: 20px;
            color: #c62828;
        }

        .shop-section {
            margin-bottom: 50px;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .shop-section h3 {
            background: #f5f5f5;
            padding: 15px;
            border-left: 5px solid #c85a3b;
            margin: -20px -20px 20px -20px;
            border-radius: 8px 8px 0 0;
            font-size: 20px;
        }

        .shop-section h3 i {
            color: #c85a3b;
            margin-right: 8px;
        }

        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 20px;
        }

        .product-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
            transition: all 0.3s;
            background: white;
        }

        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        }

        .product-image {
            width: 100%;
            height: 150px;
            overflow: hidden;
            border-radius: 5px;
            margin-bottom: 10px;
            background: #f5f5f5;
        }

        .product-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .product-card h4 {
            margin: 10px 0;
            font-size: 16px;
            min-height: 40px;
            line-height: 1.4;
        }

        .product-info {
            text-align: left;
            margin: 10px 0;
        }

        .price {
            color: #c85a3b;
            font-size: 22px;
            font-weight: bold;
            margin: 10px 0;
            text-align: center;
        }

        .stock {
            font-size: 14px;
            color: #666;
            margin: 5px 0;
        }

        .expiry-date {
            font-size: 13px;
            color: #ff6b6b;
            margin: 5px 0;
        }

        .tag {
            margin: 10px 0;
        }

        .tag-badge {
            display: inline-block;
            background: #e3f2fd;
            color: #1976d2;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
        }

        .discount {
            background: #ff6b6b;
            color: white;
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: bold;
            display: inline-block;
            margin: 5px 0;
        }

        .booking-status {
            background: #ffa726;
            color: white;
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 12px;
            display: inline-block;
            margin: 5px 0;
        }

        .btn {
            display: inline-block;
            background: #c85a3b;
            color: white;
            padding: 10px 24px;
            border-radius: 5px;
            text-decoration: none;
            margin-top: 10px;
            transition: background 0.3s;
            font-weight: bold;
        }

        .btn:hover {
            background: #a84830;
        }

        /* レスポンシブ対応 */
        @media (max-width: 768px) {
            .product-grid {
                grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                gap: 15px;
            }

            .shop-section h3 {
                font-size: 18px;
            }
        }
    </style>
</body>
</html>