<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>商品登録</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif;
            background: #f5f5f5;
            min-height: 100vh;
            padding: 20px;
        }

        .register-wrapper {
            width: 100%;
            max-width: 600px;
            margin: 3rem auto;
        }

        .register-container {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 3rem 2.5rem;
        }

        .register-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .register-header h1 {
            color: #c77c4a;
            font-size: 2rem;
            font-weight: 700;
            margin: 0 0 1rem 0;
        }

        .register-header::after {
            content: '';
            display: block;
            width: 70%;
            height: 3px;
            background: #c77c4a;
            margin: 0 auto;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #333;
            font-size: 1rem;
        }

        .form-group label span {
            color: red;
        }

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"],
        .form-group input[type="file"] {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
            background: #fff;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c77c4a;
        }

        .image-preview {
            margin-top: 1rem;
            text-align: center;
        }

        .image-preview img {
            max-width: 100%;
            max-height: 200px;
            border-radius: 5px;
            border: 2px solid #ddd;
        }

        .btn-register {
            width: 100%;
            padding: 1rem;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
            background: #c77c4a;
            color: #fff;
            margin-top: 1rem;
        }

        .btn-register:hover {
            background: #b56c3a;
        }

        .back-link {
            text-align: center;
            margin-top: 1.5rem;
        }

        .back-link a {
            color: #666;
            text-decoration: none;
            font-size: 0.9rem;
        }

        .back-link a:hover {
            color: #c77c4a;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
            font-weight: bold;
            text-align: center;
        }

        @media screen and (max-width: 600px) {
            .register-wrapper {
                margin: 20px;
            }
            .register-container {
                padding: 2rem 1.5rem;
            }
            .register-header h1 {
                font-size: 1.5rem;
            }
        }
    </style>
    <script>
        function previewImage(input) {
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('preview').src = e.target.result;
                    document.getElementById('imagePreview').style.display = 'block';
                }
                reader.readAsDataURL(input.files[0]);
            }
        }
    </script>
</head>
<body>
<div id="container">

    <%-- ヘッダー読み込 --%>
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="register-wrapper">
            <div class="register-container">
                <div class="register-header">
                    <h1>商品登録</h1>
                </div>

                <%-- エラーメッセージ表示 --%>
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="error-message">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>

                <%-- 商品登録フォーム --%>
                <form action="${pageContext.request.contextPath}/foodloss/MerchandiseRegisterExecute.action"
                      method="post"
                      enctype="multipart/form-data"
                      id="merchandiseRegisterForm">

                    <%-- 店舗ID（セッションから取得） --%>
                    <input type="hidden" name="storeId" value="${sessionScope.store.storeId}">

                    <div class="form-group">
                        <label for="merchandiseName">商品名 <span>*</span></label>
                        <input type="text"
                               id="merchandiseName"
                               name="merchandiseName"
                               required
                               maxlength="100"
                               placeholder="例: トマトジュース"
                               value="${param.merchandiseName != null ? param.merchandiseName : ''}">
                    </div>

                    <div class="form-group">
                        <label for="price">価格（円） <span>*</span></label>
                        <input type="number"
                               id="price"
                               name="price"
                               required
                               min="0"
                               max="999999"
                               step="1"
                               placeholder="例: 500"
                               value="${param.price != null ? param.price : ''}">
                    </div>

                    <div class="form-group">
                        <label for="quantity">個数 <span>*</span></label>
                        <input type="number"
                               id="quantity"
                               name="quantity"
                               required
                               min="1"
                               max="9999"
                               value="${param.quantity != null ? param.quantity : ''}">
                    </div>

                    <div class="form-group">
                        <label for="expirationDate">消費期限 <span>*</span></label>
                        <input type="date"
                               id="expirationDate"
                               name="expirationDate"
                               required
                               value="${param.expirationDate != null ? param.expirationDate : ''}">
                    </div>

                    <div class="form-group">
                        <label for="employeeNumber">社員番号 <span>*</span></label>
                        <input type="text"
                               id="employeeNumber"
                               name="employeeNumber"
                               required
                               maxlength="20"
                               placeholder="例:12345"
                               value="${param.employeeNumber != null ? param.employeeNumber : ''}">
                    </div>

                    <div class="form-group">
                        <label for="tags">タグ</label>
                        <input type="text"
                               id="tags"
                               name="tags"
                               maxlength="200"
                               placeholder="例: 野菜, 新鮮, セール"
                               value="${param.tags != null ? param.tags : ''}">
                    </div>

                    <div class="form-group">
                        <label for="merchandiseImage">画像 <span>*</span></label>
                        <input type="file"
                               id="merchandiseImage"
                               name="merchandiseImage"
                               accept="image/*"
                               required
                               onchange="previewImage(this)">
                        <div id="imagePreview" class="image-preview" style="display:none;">
                            <img id="preview" src="" alt="画像プレビュー">
                        </div>
                    </div>

                    <button type="submit" class="btn-register">登録</button>
                </form>

                <div class="back-link">
                    <a href="${pageContext.request.contextPath}/store_jsp/main_store.jsp">← 戻る</a>
                </div>
            </div>
        </div>
    </main>

    <%-- フッター読み込み --%>
    <jsp:include page="/jsp/footer.jsp" />

</div>
</body>
</html>