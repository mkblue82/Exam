	<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<!DOCTYPE html>
	<html lang="ja">
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <title>登録情報の変更</title>
	    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

	    <style>
	        /* ======= ユーザー情報変更ページ ======= */
	        .edit-container {
	            max-width: 700px;
	            margin: 40px auto;
	            padding: 2rem;
	            background: #fff;
	            border-radius: 10px;
	            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
	        }

	        .edit-container h2 {
	            color: #c07148;
	            text-align: center;
	            font-size: 1.8rem;
	            margin-bottom: 2rem;
	            border-bottom: 2px solid #c07148;
	            padding-bottom: 1rem;
	        }

	        .error-message {
	            background-color: #fee;
	            color: #c00;
	            padding: 12px;
	            border-radius: 5px;
	            margin-bottom: 20px;
	            border: 1px solid #fcc;
	        }

	        .form-group {
	            margin-bottom: 1.5rem;
	        }

	        .form-group label {
	            display: block;
	            font-weight: bold;
	            color: #333;
	            margin-bottom: 0.5rem;
	        }

	        .form-group input[type="text"],
	        .form-group input[type="email"],
	        .form-group input[type="tel"],
	        .form-group input[type="password"] {
	            width: 100%;
	            padding: 12px;
	            border: 1px solid #ddd;
	            border-radius: 5px;
	            font-size: 16px;
	            box-sizing: border-box;
	            transition: border-color 0.3s;
	        }

	        .form-group input:focus {
	            outline: none;
	            border-color: #c07148;
	            box-shadow: 0 0 5px rgba(192, 113, 72, 0.3);
	        }

	        .note {
	            font-size: 0.9rem;
	            color: #666;
	            margin-top: 0.3rem;
	        }

	        .button-group {
	            display: flex;
	            justify-content: center;
	            gap: 15px;
	            margin-top: 30px;
	        }

	        .btn {
	            padding: 12px 40px;
	            border: none;
	            border-radius: 5px;
	            font-size: 16px;
	            cursor: pointer;
	            transition: all 0.3s;
	            text-decoration: none;
	            font-family: inherit;
	            font-weight: bold;
	        }

	        .btn-primary {
	            background: #c07148;
	            color: #fff;
	        }

	        .btn-primary:hover {
	            background: #a85d38;
	            transform: translateY(-2px);
	        }

	        .btn-secondary {
	            background-color: #ccc;
	            color: #333;
	        }

	        .btn-secondary:hover {
	            background-color: #c07148;
	            color: #fff;
	            transform: translateY(-3px);
	        }

	        @media screen and (max-width: 600px) {
	            .edit-container {
	                margin: 20px;
	                padding: 1.5rem;
	            }

	            .button-group {
	                flex-direction: column;
	            }

	            .btn {
	                width: 100%;
	            }
	        }
	    </style>
	</head>

	<body>
	<div id="container">

	    <!-- 共通ヘッダー -->
	    <jsp:include page="header_user.jsp" />

	    <main class="column">
	        <div class="main-contents">
	            <div class="edit-container">

	                <h2>登録情報の変更</h2>

	                <%-- エラーメッセージ表示（店舗編集と統一） --%>
	                <%
	                    String errorMessage = (String) request.getAttribute("error");
	                    if (errorMessage != null) {
	                %>
	                <div class="error-message">
	                    <%= errorMessage %>
	                </div>
	                <% } %>

	                <form action="${pageContext.request.contextPath}/foodloss/EditInfoExecute.action"
	                      method="post">

	                    <div class="form-group">
	                        <label for="name">ユーザー名</label>
	                        <input type="text" id="name" name="name"
	                               value="${user.name}" required>
	                    </div>

	                    <div class="form-group">
	                        <label for="email">メールアドレス</label>
	                        <input type="email" id="email" name="email"
	                               value="${user.email}" required>
	                    </div>

	                    <div class="form-group">
	                        <label for="phone">電話番号</label>
	                        <input type="tel" id="phone" name="phone"
	                               value="${user.phone}"
	                               placeholder="09012345678"
	                               pattern="[0-9]{10,11}">
	                        <div class="note">※ハイフンなしで入力してください</div>
	                    </div>

	                    <div class="form-group">
	                        <label for="password">新しいパスワード</label>
	                        <input type="password" id="password" name="password"
	                               placeholder="変更しない場合は空欄">
	                        <div class="note">※空欄の場合はパスワードは変更されません</div>
	                    </div>

	                    <div class="form-group">
	                        <label for="passwordConfirm">パスワード（確認）</label>
	                        <input type="password" id="passwordConfirm"
	                               name="passwordConfirm"
	                               placeholder="確認のため再入力してください">
	                    </div>

	                    <div class="button-group">
	                        <button type="submit" class="btn btn-primary">変更を保存</button>
	                        <a href="${pageContext.request.contextPath}/jsp/mypage.jsp"
	                           class="btn btn-secondary">戻る</a>
	                    </div>

	                </form>
	            </div>
	        </div>
	    </main>

	    <!-- 共通フッター -->
	    <jsp:include page="footer.jsp" />

	</div>

	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/main.js"></script>
	</body>
	</html>
