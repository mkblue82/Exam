<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Store" %>
<%
    Store store = (Store) request.getAttribute("store");
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (store == null) {
        response.sendRedirect(request.getContextPath() + "/foodloss/Menu.action");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>店舗情報編集</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
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

    .required {
        color: #c00;
        margin-left: 5px;
    }

    .button-container {
        display: flex;
        gap: 15px;
        justify-content: center;
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
        display: inline-block;
        font-family: inherit;
        font-weight: bold;
    }

    .btn-primary {
        background: #c07148;
        color: #fff;
        box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
    }

    .btn-primary:hover {
        background: #a85d38;
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
    }

    .btn-secondary {
        background-color: #ccc;
        color: #333;
        transition: all 0.3s;
    }

    .btn-secondary:hover {
        background-color: #c07148;
        color: #fff;
        transform: translateY(-3px);
    }

    .note {
        font-size: 0.9rem;
        color: #666;
        margin-top: 0.3rem;
    }

    @media screen and (max-width: 600px) {
        .edit-container {
            margin: 20px;
            padding: 1.5rem;
        }

        .button-container {
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
    <!-- ヘッダー読み込み -->
    <jsp:include page="/store_jsp/header_store.jsp" />

    <!-- メインコンテンツ -->
    <main class="column">
        <div class="main-contents">
            <div class="edit-container">
                <h2>店舗情報編集</h2>

                <% if (errorMessage != null) { %>
                <div class="error-message">
                    <%= errorMessage %>
                </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/foodloss/StoreEditExecute.action" method="post"enctype="multipart/form-data">

                    <input type="hidden" name="storeId" value="<%= store.getStoreId() %>">

                    <div class="form-group">
                        <label for="storeName">店舗名<span class="required">*</span></label>
                        <input type="text" id="storeName" name="storeName"
                               value="<%= store.getStoreName() != null ? store.getStoreName() : "" %>"
                               required maxlength="100">
                    </div>

                    <div class="form-group">
                        <label for="address">住所</label>
                        <input type="text" id="address" name="address"
                               value="<%= store.getAddress() != null ? store.getAddress() : "" %>"
                               maxlength="200">
                        <div class="note">例：東京都渋谷区〇〇1-2-3</div>
                    </div>

                    <div class="form-group">
                        <label for="phone">電話番号</label>
                        <input type="tel" id="phone" name="phone"
                               value="<%= store.getPhone() != null ? store.getPhone() : "" %>"
                               maxlength="20" pattern="[0-9\-]+">
                        <div class="note">例：03-1234-5678</div>
                    </div>

                    <div class="form-group">
                        <label for="email">メールアドレス<span class="required">*</span></label>
                        <input type="email" id="email" name="email"
                               value="<%= store.getEmail() != null ? store.getEmail() : "" %>"
                               required maxlength="100">
                    </div>

                    <div class="form-group">
					    <label for="password">新しいパスワード</label>
					    <input type="password" id="password" name="password"
					           maxlength="100" placeholder="変更する場合のみ入力">
					    <div class="note">※空欄の場合はパスワードは変更されません</div>
					</div>

					<div class="form-group">
					    <label for="passwordConfirm">パスワード確認</label>
					    <input type="password" id="passwordConfirm" name="passwordConfirm"
					           maxlength="100" placeholder="確認のため再入力">
					</div>

					<div class="form-group">
					    <label for="permitFile">営業許可証（再アップロード）</label>
					    <input type="file" id="permitFile" name="permitFile"
					           accept=".pdf,.jpg,.jpeg,.png">
					    <div class="note">
					        ※ 未登録の場合、または差し替える場合に選択してください<br>
					        ※ 再アップロードすると再審査となります
					    </div>
					</div>

                    <div class="button-container">
                        <button type="submit" class="btn btn-primary">更新する</button>
                        <a href="${pageContext.request.contextPath}/foodloss/StoreDetail.action" class="btn btn-secondary">
                            キャンセル
                        </a>
                    </div>

                </form>

            </div>
        </div>
    </main>

    <!-- フッター読み込み -->
    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
