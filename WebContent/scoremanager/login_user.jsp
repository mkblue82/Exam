<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ログイン</title>
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
        
        .registration-button {
            background: #1E90FF;
            color: white;
        }
        
        .registration-button:hover {
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
        <h1>ログイン</h1>
        
        <% if(request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        
        <form action="login" method="post">
            <!-- ①メールアドレス -->
            <div class="form-group">
                <label for="email_address">メールアドレス</label>
                <div class="input-wrapper">
                    <input type="text" 
                           id="email_address" 
                           name="email_address" 
                           required 
                           placeholder="example@email.com"
                           autocomplete="email">
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
                
                <!-- ④新規登録ボタン -->
                <button type="button" name="registration_button" class="registration-button" onclick="location.href='registration.jsp'">
                    新規作成
                    <span class="button-number">④</span>
                </button>
            </div>
        </form>
    </div>
</body>
</html>