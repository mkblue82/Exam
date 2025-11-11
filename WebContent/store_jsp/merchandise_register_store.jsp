<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>商品登録</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .register-wrapper {
            max-width: 600px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .register-wrapper h1 {
            color: #c07148;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 1.8rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
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

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"],
        .form-group input[type="file"] {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 1rem;
            background: #fff;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c07148;
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

        .button-group {
            display: flex;
            flex-direction: column;
            gap: 1rem;
            margin-top: 2rem;
        }

        .btn {
            padding: 0.8rem 2rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            text-decoration: none;
            transition: 0.3s;
            font-weight: bold;
            width: 100%;
        }

        .btn-submit {
            background-color: #c07148;
            color: #fff;
        }

        .btn-submit:hover {
            background-color: #a85d38;
        }

        .btn-cancel {
            background-color: #ccc;
            color: #333;
        }

        .btn-cancel:hover {
            background-color: #bbb;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
        }

        @media screen and (max-width: 600px) {
            .register-wrapper {
                margin: 20px;
                padding: 1.5rem;
            }
            .register-wrapper h1 {
                font-size: 1.3rem;
            }
        }
    </style>
</head>
<body>
<div id="container">
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="register-wrapper">
                <h1>商品登録</h1>

                <%-- エラーメッセージ表示 --%>
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="error-message">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/merchandise_register_store"
                      method="post"
                      enctype="multipart/form-data"
                      id="merchandiseRegisterForm">

                    <%-- CSRFトークン --%>
                    <input type="hidden" name="csrfToken" value="${csrfToken}">

                    <div class="form-group">
                        <label for="merchandiseName">商品名 <span style="color: red;">*</span></label>
                        <input type="text"
                               id="merchandiseName"
                               name="merchandiseName"
                               required
                               maxlength="100"
                               value="${param.merchandiseName != null ? param.merchandiseName : ''}">
                    </div>

                    <div class="form-group">
                        <label for="quantity">個数 <span style="color: red;">*</span></label>
                        <input type="number"
                               id="quantity"
                               name="quantity"
                               required
                               min="1"
                               max="9999"
                               value="${param.quantity != null ? param.quantity : ''}">
                    </div>

                    <div class="form-group">
                        <label for="expirationDate">消費期限 <span style="color: red;">*</span></label>
                        <input type="date"
                               id="expirationDate"
                               name="expirationDate"
                               required
                               value="${param.expirationDate != null ? param.expirationDate : ''}">
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
                        <label for="merchandiseImage">画像 <span style="color: red;">*</span></label>
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

                    <div class="button-group">
                        <button type="submit" class="btn btn-submit">登録する</button>
                        <button type="button" class="btn btn-cancel"
                                onclick="location.href='${pageContext.request.contextPath}/jsp/store_menu.jsp'">
                            キャンセル
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </main>

    <jsp:include page="/jsp/footer.jsp" />
</div>

<script>
    function previewImage(input) {
        const preview = document.getElementById('preview');
        const previewContainer = document.getElementById('imagePreview');

        if (input.files && input.files[0]) {
            const reader = new FileReader();

            reader.onload = function(e) {
                preview.src = e.target.result;
                previewContainer.style.display = 'block';
            }

            reader.readAsDataURL(input.files[0]);
        } else {
            previewContainer.style.display = 'none';
        }
    }

    document.getElementById('merchandiseRegisterForm').addEventListener('submit', function(e) {
        const expirationDate = new Date(document.getElementById('expirationDate').value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (expirationDate < today) {
            e.preventDefault();
            alert('消費期限は今日以降の日付を指定してください。');
            return false;
        }

        const fileInput = document.getElementById('merchandiseImage');
        if (fileInput.files.length > 0) {
            const fileSize = fileInput.files[0].size;
            const maxSize = 5 * 1024 * 1024; // 5MB

            if (fileSize > maxSize) {
                e.preventDefault();
                alert('画像ファイルのサイズは5MB以下にしてください。');
                return false;
            }
        }
    });
</script>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
</body>
</html>