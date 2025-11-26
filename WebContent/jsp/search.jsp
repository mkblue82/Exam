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

        <!-- 🔻 エラーメッセージ削除（赤枠が出ない） -->

        <!-- 🔻 検索結果がない場合のみ表示 -->
        <c:if test="${empty shopMerchMap}">
            <p class="no-results">検索条件に一致する商品・店舗が見つかりませんでした。</p>
        </c:if>

        <!-- 🔻 検索結果がある場合 -->
        <c:if test="${not empty shopMerchMap}">
            <p class="result-count">
                <i class="fa fa-check-circle"></i>
                ${shopMerchMap.size()}件の店舗が見つかりました
            </p>

            <!-- 店舗別に商品表示 -->
            <c:forEach items="${shopMerchMap}" var="entry">
                <c:set var="store" value="${entry.key}" />
                <c:set var="merchList" value="${entry.value}" />

                <section class="shop-section">
                    <h3><i class="fa fa-store"></i> <c:out value="${store.storeName}" /></h3>

                    <div class="product-grid">
                        <c:forEach var="merchandise" items="${merchList}">
                            <div class="product-card">
                                <div class="product-image">
                                    <img src="${pageContext.request.contextPath}/getMerchandiseImage?id=${merchandise.merchandiseId}"
                                         alt="<c:out value='${merchandise.merchandiseName}' />"
                                         onerror="this.src='${pageContext.request.contextPath}/images/no-image.png'">
                                </div>

                                <h4><c:out value="${merchandise.merchandiseName}" /></h4>

                                <div class="product-info">
                                    <p class="price">¥ <fmt:formatNumber value="${merchandise.price}" pattern="#,###" /></p>
                                    <p class="stock"><i class="fa fa-box"></i> 在庫: ${merchandise.stock}</p>

                                    <c:if test="${not empty merchandise.useByDate}">
                                        <p class="expiry-date"><i class="fa fa-calendar"></i>
                                            <fmt:formatDate value="${merchandise.useByDate}" pattern="yyyy/MM/dd" />まで
                                        </p>
                                    </c:if>

                                    <c:if test="${not empty merchandise.merchandiseTag}">
                                        <p class="tag"><span class="tag-badge">
                                            <i class="fa fa-tag"></i> <c:out value="${merchandise.merchandiseTag}" />
                                        </span></p>
                                    </c:if>
                                </div>

                                <a href="${pageContext.request.contextPath}/foodloss/ProductDetail.action?id=${merchandise.merchandiseId}" class="btn">
                                    詳細を見る
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </section>
            </c:forEach>
        </c:if>
    </main>

    <!-- CSS（元のまま） -->
    <style>
        main { padding: 20px; max-width: 1200px; margin: 0 auto; }
        h2 { color: #c85a3b; margin-bottom: 20px; font-size: 24px; }
        .result-count { color: #666; margin-bottom: 30px; font-size: 16px; }
        .result-count i { color: #4CAF50; }
        .no-results { text-align: center; padding: 50px; color: #999; font-size: 18px; }
        .shop-section { margin-bottom: 50px; background: #fff; padding: 20px; border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .shop-section h3 { background: #f5f5f5; padding: 15px; border-left: 5px solid #c85a3b;
                           margin: -20px -20px 20px -20px; border-radius: 8px 8px 0 0; font-size: 20px; }
        .product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; }
        .product-card { border: 1px solid #ddd; border-radius: 8px; padding: 15px; text-align: center;
                        transition: .3s; background: #fff; }
        .product-card:hover { transform: translateY(-5px); box-shadow: 0 6px 12px rgba(0,0,0,0.15); }
        .product-image { width: 100%; height: 150px; overflow: hidden; border-radius: 5px; background: #f5f5f5; margin-bottom: 10px; }
        .product-image img { width: 100%; height: 100%; object-fit: cover; }
        .price { color: #c85a3b; font-size: 22px; font-weight: bold; }
        .btn { display: inline-block; background: #c85a3b; color: white; padding: 10px 24px;
               border-radius: 5px; text-decoration: none; margin-top: 10px; font-weight: bold; }
        .btn:hover { background: #a84830; }
    </style>
</body>
</html>