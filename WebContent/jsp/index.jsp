　　<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>ログイン選択</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
/* 背景設定 */
body {
  background-image: url("${pageContext.request.contextPath}/images/food_background.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  margin: 0;
}
/* ヘッダー */
header {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 20px;
  text-align: center;
  flex-shrink: 0;
}
/* コンテナ */
#container {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: calc(100vh - 0px);
}
/* メインコンテンツ */
main {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 80px;
  padding: 0 20px;
  flex: 1;
  margin-top: -150px;
}
/* 各ログインボックス */
.login-box {
  background: rgba(255, 255, 255, 0.85);
  padding: 40px 60px;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.2);
  transition: transform 0.2s;
}
.login-box:hover {
  transform: translateY(-4px);
}
/* ログインボタン */
.login-box a.btn {
  display: inline-block;
  padding: 15px 40px;
  background-color: #c07148;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
}
.login-box a.btn:hover {
  background-color: #a85d38;
}
/* 下のリンク（加盟店登録、新規登録） */
.login-box a.sub-link {
  display: block;
  margin-top: 15px;
  color: #0073e6;
  text-decoration: none;
}
.login-box a.sub-link:hover {
  text-decoration: underline;
}
</style>
</head>
<body>
<div id="container">
  <header>
    <h1 id="logo"><span>タイトル募集</span></h1>
  </header>
  <main>
    <div class="login-box">
      <a href="login_store.jsp" class="btn">店舗用ログイン</a>
    </div>
    <div class="login-box">
      <a href="${pageContext.request.contextPath}/foodloss/Login.action" class="btn">ユーザー用ログイン</a>
    </div>
  </main>
  <!-- フッター読み込み -->
  <jsp:include page="footer.jsp" />
</div>
<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>