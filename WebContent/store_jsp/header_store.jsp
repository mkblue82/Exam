<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header>
  <h1 id="logo"><span>タイトル募集</span></h1>

  <!-- 検索フォーム -->
  <form action="${pageContext.request.contextPath}/jsp/search.jsp" method="get" id="search-form">
    <input type="text" name="keyword" placeholder="商品・店舗を検索..." required>
    <button type="submit"><i class="fa fa-search"></i></button>
  </form>

</header>

<!-- メニューバー -->
<div id="menubar">
  <nav>
    <ul>
      <li><a href="${pageContext.request.contextPath}/jsp/main_store.jsp">ホーム<span>Home</span></a></li>
      <li><a href="${pageContext.request.contextPath}/jsp/mypage.jsp"><span>STOREページ</span></a></li>
    </ul>
  </nav>
</div>

