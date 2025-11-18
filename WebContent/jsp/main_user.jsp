<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>フードロス削減システム</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div id="container">

    <jsp:include page="header_user.jsp" />

    <main class="column">
        <div class="main-contents">
            <section>
                <h2>出店店舗と販売中の商品</h2>
                <div class="list-grid1">

                    <c:forEach var="entry" items="${shopMerchMap}">
                        <c:set var="shop" value="${entry.key}" />
                        <c:set var="merchList" value="${entry.value}" />

                        <div class="list">
                            <figure>
                                <a href="shop_detail.jsp?id=${shop.storeId}">
                                    <c:choose>
                                        <c:when test="${not empty shop.imageFileName}">
                                            <img src="images/${shop.imageFileName}" alt="${shop.storeName}">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="images/noimage.png" alt="no image">
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                            </figure>
                            <div class="text">
                                <h4><a href="shop_detail.jsp?id=${shop.storeId}">${shop.storeName}</a></h4>
                                <p>${shop.description}</p>
                            </div>
                            <c:if test="${shop.newArrival}">
                                <span class="newicon">NEW</span>
                            </c:if>

                            <!-- 店舗ごとの販売商品 -->
                            <div class="shop-merchandise">
                                <c:if test="${not empty merchList}">
                                    <h5>販売中の商品</h5>
                                    <div class="thumbnail-slide">
                                        <c:forEach var="merchandise" items="${merchList}">
                                            <div class="item">
                                                <a href="item.jsp?id=${merchandise.merchandiseId}">
                                                    <c:choose>
                                                        <c:when test="${not empty merchandise.imageList}">
                                                            <img src="images/${merchandise.imageList[0].fileName}"
                                                                 alt="${merchandise.merchandiseName}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="images/noimage.png" alt="no image">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </a>
                                                <div class="text">
                                                    <h6><a href="item.jsp?id=${merchandise.merchandiseId}">
                                                        ${merchandise.merchandiseName}</a></h6>
                                                    <p>価格: ${merchandise.price}円</p>
                                                    <p>在庫: ${merchandise.stock}個</p>
                                                    <p>消費期限: <fmt:formatDate value="${merchandise.useByDate}" pattern="yyyy/MM/dd"/></p>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${empty merchList}">
                                    <p>現在販売中の商品はありません。</p>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>

                    <c:if test="${empty shopMerchMap}">
                        <p>現在出店中の店舗はありません。</p>
                    </c:if>
                </div>
            </section>
        </div>
    </main>

    <jsp:include page="footer.jsp" />
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>
</body>
</html>
