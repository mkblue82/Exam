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
  <h1 id="logo"><a href="index.jsp"><img src="images/logo.png" alt="Food Loss Reduction System"></a></h1>
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
      <li><a href="foods.jsp">食べ物<span>Foods</span></a></li>
      <li><a href="goods.jsp">雑貨<span>Goods</span></a></li>
      <li><a href="open.jsp">出店募集<span>Open Shop</span></a></li>
      <li><a href="faq.jsp">よくある質問<span>FAQ</span></a></li>
    </ul>
  </nav>
</div>
<!--/#menubar-->

<!-- スライドショー -->
<aside class="mainimg-slick">
  <div><img src="images/1.jpg" alt=""></div>
  <div><img src="images/2.jpg" alt=""></div>
  <div><img src="images/3.jpg" alt=""></div>
</aside>

<section>
  <h2 class="ribon"><span>今月のお買い得情報</span></h2>

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
  <h2>今月の出店店舗</h2>

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

<section>
  <h2>お知らせ</h2>
  <dl class="new">
    <c:forEach var="news" items="${newsList}">
      <dt>${news.date}<span>${news.category}</span></dt>
      <dd>${news.content}</dd>
    </c:forEach>
  </dl>
</section>

</div>
<!--/.main-contents-->

<div class="sub-contents">
  <p class="btn1"><a href="login_user.jsp">ログイン</a></p>

  <h3>カテゴリ</h3>
  <nav>
    <ul class="submenu">
      <li><a href="foods.jsp">食べ物</a></li>
      <li><a href="goods.jsp">雑貨</a></li>
    </ul>
  </nav>
</div>

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
