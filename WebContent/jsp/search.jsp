<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>検索結果 - タイトル募集</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <h1 id="logo"><span>タイトル募集</span></h1>
        <!-- 検索フォーム -->
        <form action="${pageContext.request.contextPath}/foodloss/Search.action" method="get" id="search-form">
            <input type="text" name="keyword" placeholder="商品・店舗を検索..." value="${param.keyword}" required>
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
        <h2>検索結果: "${param.keyword}"</h2>

        <c:if test="${empty searchResults}">
            <p class="no-results">検索条件に一致する商品・店舗が見つかりませんでした。</p>
        </c:if>

        <c:if test="${not empty searchResults}">
            <p class="result-count">${searchResults.size()}件の結果が見つかりました</p>

            <!-- 店舗別に表示 -->
            <c:forEach var="shop" items="${searchResults}">
                <section class="shop-section">
                    <h3>${shop.shopName}</h3>

                    <div class="product-grid">
                        <c:forEach var="product" items="${shop.products}">
                            <div class="product-card">
                                <img src="${pageContext.request.contextPath}/images/${product.image}"
                                     alt="${product.name}">
                                <h4>${product.name}</h4>
                                <p class="price">¥ ${product.price}</p>
                                <c:if test="${product.discountRate > 0}">
                                    <span class="discount">${product.discountRate}% OFF</span>
                                </c:if>
                                <a href="${pageContext.request.contextPath}/foodloss/ProductDetail.action?id=${product.id}"
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
        }

        .result-count {
            color: #666;
            margin-bottom: 30px;
        }

        .no-results {
            text-align: center;
            padding: 50px;
            color: #999;
            font-size: 18px;
        }

        .shop-section {
            margin-bottom: 50px;
        }

        .shop-section h3 {
            background: #f5f5f5;
            padding: 15px;
            border-left: 5px solid #c85a3b;
            margin-bottom: 20px;
        }

        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
        }

        .product-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
            transition: transform 0.3s;
        }

        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }

        .product-card img {
            width: 100%;
            height: 150px;
            object-fit: cover;
            border-radius: 5px;
        }

        .product-card h4 {
            margin: 10px 0;
            font-size: 16px;
        }

        .price {
            color: #c85a3b;
            font-size: 20px;
            font-weight: bold;
            margin: 10px 0;
        }

        .discount {
            background: #ff6b6b;
            color: white;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 12px;
        }

        .btn {
            display: inline-block;
            background: #c85a3b;
            color: white;
            padding: 8px 20px;
            border-radius: 5px;
            text-decoration: none;
            margin-top: 10px;
        }

        .btn:hover {
            background: #a84830;
        }
    </style>
</body>
</html>