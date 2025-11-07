<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header>
  <h1 id="logo"><span>タイトル募集</span></h1>

  <!-- 検索フォーム -->
  <form action="${pageContext.request.contextPath}/jsp/search.jsp" method="get" id="search-form">
    <input type="text" name="keyword" placeholder="商品・店舗を検索..." required>
    <button type="submit"><i class="fa fa-search"></i></button>
  </form>

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
      <li class="current"><a href="${pageContext.request.contextPath}/jsp/main.jsp">ホーム<span>Home</span></a></li>
      <li><a href="${pageContext.request.contextPath}/jsp/foods.jsp">マイページ<span>MyPage</span></a></li>
      <li><a href="${pageContext.request.contextPath}/jsp/goods.jsp">予約リスト<span>Reservation List</span></a></li>
      <li><a href="${pageContext.request.contextPath}/jsp/open.jsp">通知設定<span>Notification Settings</span></a></li>
    </ul>
  </nav>
</div>
<!--/#menubar-->
