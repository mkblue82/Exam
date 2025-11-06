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

<header>
  <h1 id="logo"><a href="index.jsp"><alt="タイトル募集"></a></h1>
  <div id="f-size">
    <p>文字サイズ</p>
    <ul>
      <li id="f-small"></li>
      <li id="f-large"></li>
    </ul>
  </div>
</header>

<!-- メニューバー -->
<div id="menubar">
  <nav>
    <ul>
      <li class="current"><a href="index.jsp">ホーム<span>Home</span></a></li>
      <li><a href="foods.jsp">マイページ<span>MyPage</span></a></li>
      <li><a href="goods.jsp">予約リスト<span>Reservation List</span></a></li>
      <li><a href="open.jsp">通知設定<span>Notification Settings</span></a></li>
    </ul>
  </nav>
</div>
<!--/#menubar-->





<section>
  <h2 class="ribon"><span>販売中の商品</span></h2>

  <div class="thumbnail-slide">
    <c:forEach var="product" items="${productList}">
      <a href="item.jsp?id=${product.id}">
        <img src="images/${product.image}" alt="${product.name}">
      </a>
    </c:forEach>
  </div>
</section>

<main class="column">

<div class="main-contents">

<section>
  <h2>出店店舗</h2>

  <div class="list-grid1">
    <c:forEach var="shop" items="${shopList}">
      <div class="list">
        <figure>
          <a href="shop_detail.jsp?id=${shop.id}">
            <img src="images/${shop.image}" alt="${shop.name}">
          </a>
        </figure>
        <div class="text">
          <h4><a href="shop_detail.jsp?id=${shop.id}">${shop.name}</a></h4>
          <p>${shop.description}</p>
        </div>
        <c:if test="${shop.newArrival}">
          <span class="newicon">NEW</span>
        </c:if>
      </div>
    </c:forEach>
  </div>
</section>



</main>

<footer>
  <div id="copyright">
    <small>Copyright &copy; フードロス削減システム All Rights Reserved.</small>
  </div>
</footer>

</div>
<!--/#container-->
<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>
</body>
</html>