<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header>
  <h1 id="logo"><span>タイトル募集</span></h1>
  <!-- ログアウトボタン -->
  <form action="${pageContext.request.contextPath}/foodloss/Logout.action" method="post"
      style="text-align: right; margin: 10px 20px 0 0;">
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
<style>
.logout-btn {
  padding: 12px 40px;
  background-color: #ccc;
  border: none;
  border-radius: 5px;
  color: #333;
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-family: inherit;
}
.logout-btn:hover {
  background-color: #c07148;
  color: #fff;
  transform: translateY(-3px);
}
</style>
