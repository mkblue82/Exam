<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>店舗ログイン</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .login-container {
            background: white;
            padding: 50px 40px;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
            width: 450px;
            max-width: 90%;
        }

        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 40px;
            font-size: 32px;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 25px;
            position: relative;
        }

        label {
            display: block;
            margin-bottom: 10px;
            color: #555;
            font-weight: 500;
            font-size: 14px;
        }

        .input-wrapper {
            position: relative;
            display: flex;
            align-items: center;
        }

        .input-number {
            position: absolute;
            right: 15px;
            width: 28px;
            height: 28px;
            background: #667eea;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            font-weight: 600;
            pointer-events: none;
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 14px 50px 14px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 15px;
            transition: all 0.3s;
            background: #fafafa;
        }

        input[type="text"]:focus,
        input[type="password"]:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .button-group {
            margin-top: 35px;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        button {
            width: 100%;
            padding: 14px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-button {
            background: #1E90FF;
            color: white;
        }

        .login-button:hover {
            background: #1873CC;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(30, 144, 255, 0.3);
        }

        .application-button {
            background: #1E90FF;
            color: white;
        }

        .application-button:hover {
            background: #1873CC;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(30, 144, 255, 0.3);
        }

        .button-number {
            position: absolute;
            right: 15px;
            width: 28px;
            height: 28px;
            background: rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            font-weight: 600;
        }

        .error-message {
            background-color: #fee;
            color: #c33;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 25px;
            text-align: center;
            font-size: 14px;
            border: 1px solid #fcc;
        }

        .store-badge {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 8px 20px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            display: inline-block;
            margin-bottom: 20px;
        }

        .header-section {
            text-align: center;
        }

        @media (max-width: 480px) {
            .login-container {
                padding: 40px 30px;
            }

            h1 {
                font-size: 28px;
            }
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="header-section">
            <div class="store-badge">店舗用</div>
            <h1>ログイン</h1>
        </div>

        <% if(request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <form action="storeLogin" method="post">
            <!-- ①ID -->
            <div class="form-group">
                <label for="store_id">ID</label>
                <div class="input-wrapper">
                    <input type="text"
                           id="store_id"
                           name="store_id"
                           required
                           placeholder="店舗IDを入力"
                           autocomplete="username">
                    <span class="input-number">①</span>
                </div>
            </div>

            <!-- ②パスワード -->
            <div class="form-group">
                <label for="password">パスワード</label>
                <div class="input-wrapper">
                    <input type="password"
                           id="password"
                           name="password"
                           required
                           placeholder="パスワードを入力"
                           autocomplete="current-password">
                    <span class="input-number">②</span>
                </div>
            </div>

            <div class="button-group">
                <!-- ③ログインボタン -->
                <button type="submit" name="login_button" class="login-button">
                    ログイン
                    <span class="button-number">③</span>
                </button>

                <!-- ④新規店舗申請ボタン -->
                <button type="button" name="application" class="application-button" onclick="location.href='storeApplication.jsp'">
                    新規店舗申請
                    <span class="button-number">④</span>
                </button>
            </div>
        </form>
    </div>
</body>
</html>