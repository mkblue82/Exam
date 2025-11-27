<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header>
  <h1 id="logo"><span>タイトル募集</span></h1>
  <!-- 検索フォーム -->
  <form action="${pageContext.request.contextPath}/foodloss/Search.action" method="get" id="search-form">
    <input type="text" name="keyword" placeholder="商品・キーワード・店舗を検索..." required>
    <button type="submit"><i class="fa fa-search"></i></button>
  </form>
</header>
<!-- メニューバー -->
<div id="menubar">
  <nav>
    <ul>
      <li><a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホーム<span>Home</span></a></li>
      <li><a href="${pageContext.request.contextPath}/foodloss/MyPage.action">マイページ<span>MyPage</span></a></li>
      <li><a href="${pageContext.request.contextPath}/foodloss/BookingUserList.action">予約リスト<span>Reservation List</span></a></li>
      <li><a href="${pageContext.request.contextPath}/foodloss/NotificationSetting.action">通知設定<span>Notification Settings</span></a></li>
    </ul>
  </nav>
</div>