<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.User" %>
<%
    // セッションチェック
    HttpSession userSession = request.getSession(false);
    User user = (User) (userSession != null ? userSession.getAttribute("user") : null);

    if (userSession == null || user == null) {
        // セッションがない場合は共通エラーページへリダイレクト
        request.setAttribute("errorMessage", "セッションが切れています。ログアウトされたか、長時間操作がありませんでした。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }


    // キャッシュ制御（ブラウザバック対策）
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>マイページ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .main-content {
            max-width: 600px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .point-section h2 {
            color: #c07148;
            text-align: center;
            font-size: 1.8rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
            margin-bottom: 2rem;
        }
        .point-section p {
            font-size: 2.4rem;
            font-weight: bold;
            color: #c07148;
            margin-bottom: 60px;
            text-align: center;
        }
        .button-group {
            display: flex;
            justify-content: center;
            gap: 40px;
            flex-wrap: wrap;
            margin-top: 40px;
        }
        .button-group a {
            display: block;
            width: 220px;
            padding: 15px;
            text-align: center;
            background-color: #ccc;
            border-radius: 8px;
            text-decoration: none;
            color: #333;
            font-weight: bold;
            transition: background 0.3s;
        }
        .button-group a:hover {
            background-color: #c07148;
            color: #fff;
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />
    <!-- メインエリア -->
    <main class="column">
        <div class="main-contents">
            <div class="main-content">
                <div class="point-section">
                    <h2>所有ポイント</h2>
                    <p><%= user.getPoint() %> P</p>
                </div>
                <div class="button-group">
                    <a href="${pageContext.request.contextPath}/jsp/edit_info_user.jsp">情報変更</a>
                    <a href="${pageContext.request.contextPath}/foodloss/Logout.action">ログアウト</a>
                    <a href="${pageContext.request.contextPath}/foodloss/DeleteAccount.action">アカウント削除</a>
                </div>
            </div>
        </div>
    </main>
    <!-- フッター -->
    <jsp:include page="footer.jsp" />
</div>
<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<!-- ブラウザバック対策 -->
<script>
    // ページ読み込み時に履歴を操作
    history.pushState(null, null, location.href);

    // ブラウザの戻るボタンが押された時の処理
    window.addEventListener('popstate', function(event) {
        // 履歴を再び追加
        history.pushState(null, null, location.href);
        // 共通エラーページへ遷移
        location.href = '${pageContext.request.contextPath}/error.jsp?error=セッションが切れています';
    });
</script>
</body>
</html>