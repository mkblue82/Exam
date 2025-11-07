<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>新規店舗申請 - Sample Online Mall</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }

        .register-container {
            max-width: 500px;
            margin: 80px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }

        .register-container h1 {
            color: #c07148;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
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

        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="tel"],
        .form-group input[type="password"],
        .form-group input[type="file"] {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
            box-sizing: border-box;
            transition: 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c07148;
            box-shadow: 0 0 0 3px rgba(192, 113, 72, 0.1);
        }

        .btn-submit,
        .btn-cancel {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            margin-bottom: 0.8rem;
        }

        .btn-submit {
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
        }

        .btn-submit:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-cancel {
            background: #fff;
            color: #c07148;
            border: 2px solid #c07148;
        }

        .btn-cancel:hover {
            background: #c07148;
            color: #fff;
        }

        @media screen and (max-width: 600px) {
            .register-container {
                margin: 40px 20px;
                padding: 1.5rem;
            }

            .register-container h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
	<div class="register-container">
	    <h1>新規店舗申請</h1>

	    <c:if test="${not empty errorMessage}">
	        <div class="error-message" role="alert">
	            <c:out value="${errorMessage}"/>
	        </div>
	    </c:if>

	    <!-- enctypeでファイルアップロードを許可 -->
	    <form action="${pageContext.request.contextPath}/StoreRegisterServlet"
	          method="post" enctype="multipart/form-data" id="storeRegisterForm">

	        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

	        <div class="form-group">
	            <label for="storeName">店舗名</label>
	            <input type="text" id="storeName" name="storeName" required
	                   placeholder="例：Sample Bakery"
	                   value="${param.storeName}">
	        </div>

	        <div class="form-group">
	            <label for="address">店舗住所</label>
	            <input type="text" id="address" name="address" required
	                   placeholder="例：東京都新宿区〇〇1-2-3"
	                   value="${param.address}">
	        </div>

	        <div class="form-group">
	            <label for="phone">店舗電話番号</label>
	            <input type="tel" id="phone" name="phone" required
	                   placeholder="例：03-1234-5678"
	                   value="${param.phone}">
	        </div>

	        <div class="form-group">
	            <label for="email">店舗メールアドレス</label>
	            <input type="email" id="email" name="email" required
	                   placeholder="例：store@mail.com"
	                   value="${param.email}">
	        </div>

	        <div class="form-group">
	            <label for="password">パスワード</label>
	            <input type="password" id="password" name="password" required minlength="8"
	                   placeholder="8文字以上で入力してください">
	        </div>

	        <div class="form-group">
	            <label for="permitFile">営業許可書（画像またはPDF）</label>
	            <input type="file" id="permitFile" name="permitFile"
	                   accept=".jpg,.jpeg,.png,.pdf" required>
	        </div>

	        <button type="submit" class="btn-submit">申請する</button>
	        <button type="button" class="btn-cancel"
	                onclick="location.href='${pageContext.request.contextPath}/jsp/login_store.jsp'">ログインに戻る</button>
	    </form>
	</div>
	 <jsp:include page="footer.jsp" />
</body>
</html>
