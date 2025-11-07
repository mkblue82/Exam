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
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* メインコンテンツ中央寄せ */
main {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  gap: 80px;
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
  background-color: #d47b44;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
}
.login-box a.btn:hover {
  background-color: #b86532;
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

/* ヘッダーとフッター */
header, footer {
  text-align: center;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 10px;
}
</style>
</head>

<body>

<header>
  <h1>ログイン選択</h1>
</header>

<main>
  <div class="login-box">
    <a href="login_store.jsp" class="btn">店舗用ログイン</a>
  </div>

  <div class="login-box">
    <a href="login_user.jsp" class="btn">ユーザー用ログイン</a>
  </div>
</main>

<footer>
  <small>Copyright &copy; フードロス削減システム All Rights Reserved.</small>
</footer>

</body>
</html>
