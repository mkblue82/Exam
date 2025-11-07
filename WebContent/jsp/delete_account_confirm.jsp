<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>アカウント削除確認</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .delete-account-confirm-container {
            max-width: 450px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .delete-account-confirm-container h1 {
            color: #c07148;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 1.8rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
        }

        .confirm-message {
            background: #fff3cd;
            color: #856404;
            padding: 1.5rem;
            border-radius: 5px;
            margin-bottom: 2rem;
            border-left: 4px solid #ffc107;
            font-size: 1rem;
            line-height: 1.8;
            text-align: center;
        }

        .confirm-message strong {
            display: block;
            color: #c62828;
            font-size: 1.1rem;
            margin-bottom: 0.5rem;
        }

        .button-group {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }

        .btn-confirm-delete,
        .btn-cancel {
            flex: 1;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
        }

        .btn-confirm-delete {
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
        }

        .btn-confirm-delete:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-cancel {
            background: #6c757d;
            color: #fff;
            box-shadow: 0 3px 10px rgba(108, 117, 125, 0.3);
        }

        .btn-cancel:hover {
            background: #5a6268;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(108, 117, 125, 0.4);
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ヘッダー読み込み -->
    <jsp:include page="header.jsp" />

    <!-- アカウント削除確認コンテンツ -->
    <main class="column">
        <div class="main-contents">
            <div class="delete-account-confirm-container">
                <h1>削除確認</h1>

                <div class="confirm-message">
                    <strong>本当にアカウントを削除しますか？</strong>
                    この操作は取り消せません。<br>
                    すべてのデータが削除されます。
                </div>

                <form method="POST" action="${pageContext.request.contextPath}/foodloss/DeleteAccount.action" id="confirmForm">
                    <input type="hidden" name="action" value="execute">

                    <div class="button-group">
                        <button type="button" class="btn-cancel" onclick="history.back()">キャンセル</button>
                        <button type="submit" class="btn-confirm-delete">削除する</button>
                    </div>
                </form>
            </div>
        </div>
    </main>

    <!-- フッター読み込み -->
    <jsp:include page="footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>
</body>
</html>
