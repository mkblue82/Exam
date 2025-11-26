<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header>
  <h1 id="logo"><span>タイトル募集</span></h1>

  <!-- ログアウトボタン -->
  <form action="${pageContext.request.contextPath}/foodloss/Logout.action" method="post" id="logout-form">
    <button type="submit" class="logout-btn">ログアウト</button>
  </form>
</header>

<!-- メニューバー -->
<div id="menubar">
  <nav>
    <ul>
      <li><a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホーム<span>Home</span></a></li>
      <li><a href="${pageContext.request.contextPath}/foodloss/StoreDetail.action">店舗詳細<span>Store</span></a></li>
    </ul>
  </nav>
</div>
