<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>ログイン選択</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
/* 背景 */
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
header {
  background-color: rgba(255, 255, 255, 0.85);
  padding: 20px;
  text-align: center;
  flex-shrink: 0;
}
#container {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
}
main {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 100px;
  padding: 50px 20px 200px;
  flex: 1;
}
/* カードボックス */
.login-box {
    background: #fff;
    padding: 30px;
    width: 260px;
    border-radius: 20px;
    text-align: center;
    box-shadow: 0 4px 18px rgba(0,0,0,0.18);
    transition: transform 0.3s;
}
.login-box:hover {
    transform: translateY(-4px);
}
/* ログインボタン（大） */
.btn-main {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 120px;
    background-color: #c07148;
    color: #fff;
    text-decoration: none;
    border-radius: 12px;
    font-size: 1.3rem;
    font-weight: bold;
    line-height: 1.5;
    transition: 0.3s;
}
.btn-main:hover {
    background-color: #a85d38;
}
/* 下のリンク（加盟店登録 / 新規登録） */
.sub-link {
    margin-top: 16px;
    display: block;
    color: #005bbb;
    text-decoration: none;
    font-size: 1rem;
    font-weight: bold;
}
.sub-link:hover {
    text-decoration: underline;
}
</style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <header>
	   <h1 id="logo"><span>タイトル募集</span></h1>
	</header>
    <!-- メインコンテンツ -->
    <main>
        <!-- 店舗ログイン -->
        <div class="login-box">
            <a href="${pageContext.request.contextPath}/foodloss/Login_Store.action" class="btn-main">
                店舗用<br>ログイン
            </a>
            <a href="${pageContext.request.contextPath}/store_jsp/signup_store.jsp" class="sub-link">
                加盟店登録
            </a>
        </div>
        <!-- ユーザーログイン -->
        <div class="login-box">
            <a href="${pageContext.request.contextPath}/foodloss/Login.action" class="btn-main">
                ユーザー用	<br>ログイン
            </a>
            <a href="${pageContext.request.contextPath}/foodloss/SignupUser.action" class="sub-link">
                新規登録
            </a>
        </div>
    </main>
    <jsp:include page="footer.jsp" />
</div>
</body>
</html>