<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header>
  <h1 id="logo"><span>タイトル募集</span></h1>

  <!-- 検索フォーム -->
  <form action="${pageContext.request.contextPath}/search_management.jsp" method="get" id="search-form">
	  <input type="text" name="keyword" placeholder="社員名または商品名を入力" required>
	  <button type="submit"><i class="fa fa-search"></i></button>
  </form>


</header>

<!-- メニューバー -->
<div id="menubar">
  <nav>
    <ul>
      <li><a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホーム<span>Home</span></a></li>
      <li><a href="${pageContext.request.contextPath}/foodloss/StoreDetail.action">店舗詳細<span>STORE</span></a></li>
    </ul>
  </nav>
</div>

