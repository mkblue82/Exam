<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ログアウト完了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }

        .logout-complete-container {
            max-width: 450px;
            margin: 80px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            text-align: center;
        }

        .logout-complete-container h1 {
            color: #c07148;
            margin-bottom: 1.5rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
            text-align: center;
        }

        .logout-message {
            color: #555;
            font-size: 1rem;
            margin-bottom: 2rem;
            line-height: 1.6;
        }



        .btn-login {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
        }

        .btn-login:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        @media screen and (max-width: 600px) {
            .logout-complete-container {
                margin: 40px 20px;
                padding: 1.5rem;
            }

            .logout-complete-container h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="logout-complete-container">
        <h1>ログアウト完了</h1>
        <p class="logout-message">ログアウトしました。<br>また次回のご利用をお待ちしております。</p>
        <form method="GET" action="${pageContext.request.contextPath}/login">
            <button type="submit" class="btn-login">TOP画面へ</button>
        </form>
    </div>
</body>
</html>
