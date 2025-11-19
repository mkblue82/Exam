<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品登録</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .register-container {
            max-width: 600px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
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

        .form-group label span {
            color: red;
        }

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"],
        .form-group input[type="file"] {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
            transition: 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c07148;
            box-shadow: 0 0 0 3px rgba(192, 113, 72, 0.1);
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

        .btn-submit,
        .btn-cancel {
            width: 100%;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: 0.3s;
            text-decoration: none;
            display: block;
            text-align: center;
        }

        .btn-submit {
            padding: 1.2rem;
            font-size: 1.1rem;
            font-weight: bold;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            margin-bottom: 0.8rem;
        }

        .btn-submit:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-cancel {
            padding: 0.6rem;
            font-size: 0.9rem;
            font-weight: normal;
            background: #fff;
            color: #c07148;
            border: 1px solid #c07148;
        }

        .btn-cancel:hover {
            background: #c07148;
            color: #fff;
        }

        @media screen and (max-width: 600px) {
            .register-container {
                margin: 40px 10px;
                padding: 1.5rem;
            }

            .register-container h1 {
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
    <%-- ヘッダー読み込み --%>
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="register-container">
                <h1>商品登録</h1>

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
                               placeholder="例: 10"
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
                               placeholder="例: 12345"
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
                        <label for="merchandiseImage">商品画像 <span>*</span></label>
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

                    <button type="submit" class="btn-submit">登録</button>
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="btn-cancel">戻る</a>
                </form>
            </div>
        </div>
    </main>

    <%-- フッター読み込み --%>
    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>