<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="bean.Application" %>
<%
    Application app = (Application) request.getAttribute("approvedStore");
    String storeName = (app != null) ? app.getStoreName() : "店舗";
    String storeEmail = (app != null) ? app.getStoreEmail() : "";
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>承認完了 - WASSHOI運営</title>
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
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            max-width: 600px;
            width: 100%;
            padding: 50px 40px;
            text-align: center;
        }

        .success-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #4CAF50, #45a049);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 30px;
            animation: scaleIn 0.5s ease-out;
            box-shadow: 0 10px 30px rgba(76, 175, 80, 0.4);
        }

        .success-icon::after {
            content: "✓";
            color: white;
            font-size: 60px;
            font-weight: bold;
        }

        @keyframes scaleIn {
            from {
                transform: scale(0) rotate(-180deg);
                opacity: 0;
            }
            to {
                transform: scale(1) rotate(0deg);
                opacity: 1;
            }
        }

        h1 {
            color: #333;
            font-size: 32px;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .message {
            color: #666;
            font-size: 18px;
            line-height: 1.8;
            margin-bottom: 40px;
        }

        .store-name {
            color: #667eea;
            font-weight: bold;
            font-size: 20px;
        }

        .info-box {
            background: #f5f5f5;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 30px;
            text-align: left;
        }

        .info-box h3 {
            color: #667eea;
            font-size: 18px;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }

        .info-box h3::before {
            content: "📧";
            margin-right: 10px;
            font-size: 24px;
        }

        .info-box p {
            color: #555;
            line-height: 1.6;
            margin: 8px 0;
        }

        .email-address {
            color: #667eea;
            font-weight: 600;
            background: white;
            padding: 8px 15px;
            border-radius: 6px;
            display: inline-block;
            margin-top: 10px;
        }

        .action-list {
            background: #e3f2fd;
            border-left: 4px solid #2196F3;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            text-align: left;
        }

        .action-list h3 {
            color: #1976D2;
            font-size: 16px;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }

        .action-list h3::before {
            content: "✅";
            margin-right: 10px;
        }

        .action-list ul {
            list-style: none;
            padding: 0;
        }

        .action-list li {
            color: #1565C0;
            padding: 8px 0;
            padding-left: 25px;
            position: relative;
        }

        .action-list li::before {
            content: "•";
            position: absolute;
            left: 10px;
            font-size: 20px;
        }

        .btn-container {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 15px 35px;
            border: none;
            border-radius: 25px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
            min-width: 160px;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
        }

        .btn-secondary {
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
        }

        .btn-secondary:hover {
            background: #f0f0f0;
            transform: translateY(-2px);
        }

        @media screen and (max-width: 600px) {
            .container {
                padding: 30px 20px;
            }

            h1 {
                font-size: 26px;
            }

            .btn-container {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="success-icon"></div>

        <h1>店舗承認完了！</h1>

        <div class="message">
            <span class="store-name"><%= storeName %></span> の店舗登録を承認しました。
        </div>

        <div class="info-box">
            <h3>店舗へのメール送信</h3>
            <p>承認完了の通知メールを以下のアドレスに送信しました：</p>
            <div class="email-address"><%= storeEmail %></div>
            <p style="margin-top: 15px; font-size: 14px; color: #888;">
                店舗はメールに記載されたログイン情報で<br>
                システムにログインできるようになります。
            </p>
        </div>

        <div class="action-list">
            <h3>完了した処理</h3>
            <ul>
                <li>店舗情報をデータベースに登録</li>
                <li>申請ステータスを「承認済み」に更新</li>
                <li>店舗にログイン情報を含む承認メールを送信</li>
            </ul>
        </div>

        <div class="btn-container">
            <a href="<%= request.getContextPath() %>/jsp/index.jsp" class="btn btn-primary">
                トップページへ
            </a>

        </div>
    </div>
</body>
</html>