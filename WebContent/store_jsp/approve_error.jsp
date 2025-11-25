<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage == null) {
        errorMessage = "予期しないエラーが発生しました。";
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>承認エラー - WASSHOI運営</title>
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

        .error-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #f44336, #e91e63);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 30px;
            animation: shake 0.5s ease-out;
            box-shadow: 0 10px 30px rgba(244, 67, 54, 0.4);
        }

        .error-icon::after {
            content: "✕";
            color: white;
            font-size: 60px;
            font-weight: bold;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            10%, 30%, 50%, 70%, 90% { transform: translateX(-10px); }
            20%, 40%, 60%, 80% { transform: translateX(10px); }
        }

        h1 {
            color: #d32f2f;
            font-size: 32px;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .message {
            color: #666;
            font-size: 18px;
            line-height: 1.8;
            margin-bottom: 30px;
        }

        .error-box {
            background: #ffebee;
            border-left: 4px solid #f44336;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            text-align: left;
        }

        .error-box h3 {
            color: #c62828;
            font-size: 16px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
        }

        .error-box h3::before {
            content: "⚠️";
            margin-right: 10px;
            font-size: 20px;
        }

        .error-box p {
            color: #d32f2f;
            line-height: 1.6;
            font-weight: 500;
        }

        .help-box {
            background: #fff3e0;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 30px;
            text-align: left;
        }

        .help-box h3 {
            color: #f57c00;
            font-size: 16px;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }

        .help-box h3::before {
            content: "💡";
            margin-right: 10px;
        }

        .help-box ul {
            list-style: none;
            padding: 0;
        }

        .help-box li {
            color: #e65100;
            padding: 8px 0;
            padding-left: 25px;
            position: relative;
            line-height: 1.5;
        }

        .help-box li::before {
            content: "→";
            position: absolute;
            left: 5px;
            font-weight: bold;
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
        <div class="error-icon"></div>

        <h1>承認処理に失敗しました</h1>

        <div class="message">
            申し訳ございません。店舗承認処理中にエラーが発生しました。
        </div>

        <div class="error-box">
            <h3>エラー内容</h3>
            <p><%= errorMessage %></p>
        </div>

        <div class="help-box">
            <h3>考えられる原因</h3>
            <ul>
                <li>無効または期限切れの承認リンク</li>
                <li>既に承認済みの申請</li>
                <li>申請データが削除されている</li>
                <li>同じ電話番号またはメールアドレスが既に登録済み</li>
                <li>データベース接続エラー</li>
            </ul>
        </div>

        <div class="btn-container">
            <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-primary">
                トップページへ
            </a>
            <a href="<%= request.getContextPath() %>/admin_jsp/pending_applications.jsp" class="btn btn-secondary">
                申請一覧へ
            </a>
        </div>
    </div>
</body>
</html>