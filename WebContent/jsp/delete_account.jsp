<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>アカウント削除</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }

        .delete-account-container {
            max-width: 450px;
            margin: 80px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }

        .delete-account-container h1 {
            color: #c07148;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
        }

        .warning-message {
            background: #fff3cd;
            color: #856404;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #ffc107;
            font-size: 0.9rem;
            line-height: 1.6;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
            font-size: 0.9rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
            color: #555;
        }

        .form-group input[type="password"] {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
            transition: 0.3s;
            box-sizing: border-box;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c07148;
            box-shadow: 0 0 0 3px rgba(192, 113, 72, 0.1);
        }

        .button-group {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }

        .btn-delete,
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

        .btn-delete {
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
        }

        .btn-delete:hover {
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

        @media screen and (max-width: 600px) {
            .delete-account-container {
                margin: 40px 20px;
                padding: 1.5rem;
            }

            .delete-account-container h1 {
                font-size: 1.5rem;
            }

            .button-group {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="delete-account-container">
        <h1>アカウント削除</h1>

        <form method="POST" action="${pageContext.request.contextPath}/delete-account" id="deleteForm">
            <input type="hidden" name="action" value="verify">

            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password"
                       id="password"
                       name="password"
                       required
                       minlength="8"
                       placeholder="パスワードを入力"
                       aria-required="true">
            </div>

            <div class="button-group">
                <button type="button" class="btn-cancel" onclick="location.href='${pageContext.request.contextPath}/mypage'">キャンセル</button>
                <button type="submit" class="btn-delete">次へ</button>
            </div>
        </form>
    </div>
</body>
</html>
