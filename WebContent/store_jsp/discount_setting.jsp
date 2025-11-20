<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // セッションチェック（店舗側）


    // キャッシュ制御
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>割引設定</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .discount-setting-container {
            max-width: 450px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
            margin-top: 150px;
        }
        .discount-setting-container h1 {
            color: #c07148;
            margin-bottom: 1.5rem;
            font-size: 1.8rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
            text-align: center;
        }
        .form-group {
            margin-bottom: 1.5rem;
            text-align: left;
        }
        .form-row {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 15px;
            margin-bottom: 10px;
        }
        .form-row label {
            color: #555;
            font-size: 1rem;
            font-weight: 500;
            min-width: 120px;
            text-align: right;
        }
        .input-wrapper {
            position: relative;
            width: 180px;
        }
        .form-row input[type="number"] {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 1rem;
            transition: 0.3s;
        }
        .form-row input[type="number"]:focus {
            outline: none;
            border-color: #c07148;
        }
        .unit {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #666;
            font-size: 0.9rem;
            pointer-events: none;
        }
        .error-message {
            color: #e74c3c;
            font-size: 0.85rem;
            margin-top: 5px;
            text-align: center;
            display: none;
        }
        .error-message.show {
            display: block;
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
            display: none;
        }
        .success-message.show {
            display: block;
        }
        .btn-submit {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            margin-top: 1rem;
        }
        .btn-submit:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_store.jsp" />

    <!-- メインエリア -->
    <main class="column">
        <div class="main-contents">
            <div class="discount-setting-container">
                <h1>割引設定</h1>

                <!-- 成功メッセージ -->
                <div id="successMessage" class="success-message">
                    割引設定が完了しました。
                </div>

              <form id="discountForm" method="post" action="${pageContext.request.contextPath}/foodloss/DiscountSetting.action">
                    <!-- 時間入力 -->
                    <div class="form-group">
                        <div class="form-row">
                            <label for="time">時間</label>
                            <div class="input-wrapper">
                                <input type="number"
                                       id="time"
                                       name="time"
                                       min="0"
                                       max="23"
                                       placeholder="例: 18"
                                       required>
                                <span class="unit">時</span>
                            </div>
                        </div>
                        <div class="error-message" id="timeError">時間は0〜23の範囲で入力してください</div>
                    </div>

                    <!-- 割引パーセント入力 -->
                    <div class="form-group">
                        <div class="form-row">
                            <label for="discount">値引パーセント</label>
                            <div class="input-wrapper">
                                <input type="number"
                                       id="discount"
                                       name="discount"
                                       min="1"
                                       max="100"
                                       placeholder="例: 30"
                                       required>
                                <span class="unit">%</span>
                            </div>
                        </div>
                        <div class="error-message" id="discountError">割引率は1〜100の範囲で入力してください</div>
                    </div>

                    <!-- 送信ボタン -->
                    <button type="submit" class="btn-submit">設定</button>
                </form>
            </div>
        </div>
    </main>
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
    // フォームバリデーション
    const form = document.getElementById('discountForm');
    const timeInput = document.getElementById('time');
    const discountInput = document.getElementById('discount');
    const timeError = document.getElementById('timeError');
    const discountError = document.getElementById('discountError');

    // 時間のバリデーション
    timeInput.addEventListener('input', function() {
        const value = parseInt(this.value);
        if (isNaN(value) || value < 0 || value > 23) {
            timeError.classList.add('show');
            this.style.borderColor = '#e74c3c';
        } else {
            timeError.classList.remove('show');
            this.style.borderColor = '#ddd';
        }
    });

    // 割引率のバリデーション
    discountInput.addEventListener('input', function() {
        const value = parseInt(this.value);
        if (isNaN(value) || value < 1 || value > 100) {
            discountError.classList.add('show');
            this.style.borderColor = '#e74c3c';
        } else {
            discountError.classList.remove('show');
            this.style.borderColor = '#ddd';
        }
    });

    // フォーム送信時の最終チェック
    form.addEventListener('submit', function(e) {
        const timeValue = parseInt(timeInput.value);
        const discountValue = parseInt(discountInput.value);

        let hasError = false;

        if (isNaN(timeValue) || timeValue < 0 || timeValue > 23) {
            timeError.classList.add('show');
            timeInput.style.borderColor = '#e74c3c';
            hasError = true;
        }

        if (isNaN(discountValue) || discountValue < 1 || discountValue > 100) {
            discountError.classList.add('show');
            discountInput.style.borderColor = '#e74c3c';
            hasError = true;
        }

        if (hasError) {
            e.preventDefault();
            return false;
        }
    });

    // URLパラメータから成功メッセージを表示
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('success') === 'true') {
        document.getElementById('successMessage').classList.add('show');
        setTimeout(() => {
            document.getElementById('successMessage').classList.remove('show');
        }, 3000);
    }
</script>

<!-- ブラウザバック対策 -->
<script>
    // ページ読み込み時に履歴を操作
    history.pushState(null, null, location.href);

    // ブラウザの戻るボタンが押された時の処理
    window.addEventListener('popstate', function(event) {
        // 履歴を再び追加
        history.pushState(null, null, location.href);
        // 共通エラーページへ遷移
        location.href = '${pageContext.request.contextPath}/error.jsp?error=セッションが切れています';
    });
</script>
</body>
</html>