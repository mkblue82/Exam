<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>情報変更完了</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.main-content {
    max-width: 600px;
    margin: 60px auto;
    padding: 2.5rem;
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    text-align: center;
}
.main-content h2 {
    font-size: 1.8rem;
    color: #c07148;
    margin-bottom: 20px;
}
.main-content p {
    font-size: 1.2rem;
    color: #555;
    margin-bottom: 50px;
}
.button-group {
    display: flex;
    justify-content: center;
    gap: 15px;
    flex-wrap: wrap;
    margin-top: 40px;
}
.button-group a {
    padding: 12px 40px;
    background-color: #ccc;
    border-radius: 5px;
    text-decoration: none;
    color: #333;
    font-weight: bold;
    font-size: 16px;
    transition: all 0.3s;
    font-family: inherit;
}
.button-group a:hover {
    background-color: #c07148;
    color: #fff;
    transform: translateY(-3px);
}
</style>
</head>
<body>
<div id="container">
  <!-- ✅ 共通ヘッダー -->
  <jsp:include page="header_user.jsp" />
  <!-- ✅ メインエリア -->
  <main class="column">
    <div class="main-contents">
      <div class="main-content">
        <h2>情報変更が完了しました</h2>
        <p>ユーザー情報の更新が正常に完了しました。</p>
        <div class="button-group">
          <a href="${pageContext.request.contextPath}/jsp/mypage.jsp">マイページに戻る</a>
          <a href="${pageContext.request.contextPath}/jsp/main_user.jsp">トップページへ</a>
        </div>
      </div>
    </div>
  </main>
  <!-- ✅ 共通フッター -->
  <jsp:include page="footer.jsp" />
</div>
<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>
</body>
</html>
