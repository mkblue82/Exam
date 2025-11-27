<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Merchandise, bean.Store, bean.User" %>
<%
    HttpSession userSession = request.getSession(false);
    if (userSession == null || userSession.getAttribute("user") == null) {
        request.setAttribute("errorMessage", "セッションが切れています。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }

    User user = (User) userSession.getAttribute("user");
    Merchandise merch = (Merchandise) request.getAttribute("merchandise");
    Store store = (Store) request.getAttribute("store");
    Integer quantity = (Integer) request.getAttribute("quantity");
    Integer totalPrice = (Integer) request.getAttribute("totalPrice");

    if (merch == null || quantity == null) {
        request.setAttribute("errorMessage", "予約情報が取得できませんでした。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }

    // ユーザーの保有ポイント
    int userPoints = user.getPoint();
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>予約内容確認</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.confirm-box {
    max-width: 700px;
    margin: 40px auto;
    padding: 30px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 3px 10px rgba(0,0,0,0.1);
}

.confirm-title {
    font-size: 1.8rem;
    font-weight: bold;
    color: #c07148;
    text-align: center;
    margin-bottom: 30px;
    border-bottom: 2px solid #c07148;
    padding-bottom: 10px;
}

.confirm-info {
    background: #f9f9f9;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 25px;
}

.info-row {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #e0e0e0;
}

.info-row:last-child {
    border-bottom: none;
}

.info-label {
    width: 150px;
    font-weight: bold;
    color: #555;
}

.info-value {
    flex: 1;
    color: #333;
}

.total-price {
    font-size: 1.3rem;
    color: #c07148;
    font-weight: bold;
}

/* ポイント使用セクション */
.points-section {
    background: #f0f8ff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 25px;
    border: 2px solid #4a90e2;
}

.points-title {
    font-size: 1.2rem;
    font-weight: bold;
    color: #4a90e2;
    margin-bottom: 15px;
}

.points-info {
    display: flex;
    align-items: center;
    gap: 15px;
    flex-wrap: wrap;
    margin-bottom: 15px;
}

.user-points {
    font-size: 1.1rem;
    color: #333;
}

.user-points strong {
    color: #4a90e2;
    font-size: 1.3rem;
}

.points-input-group {
    display: flex;
    align-items: center;
    gap: 10px;
}

.points-input-group input {
    width: 120px;
    padding: 8px;
    font-size: 1rem;
    border: 1px solid #ddd;
    border-radius: 5px;
}

.use-all-btn {
    padding: 6px 15px;
    background: #4a90e2;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 0.9rem;
}

.use-all-btn:hover {
    opacity: 0.8;
}

.points-note {
    font-size: 0.9rem;
    color: #666;
    margin-top: 10px;
}

.discount-info {
    background: #ffe4e1;
    padding: 15px;
    border-radius: 5px;
    margin-top: 15px;
}

.discount-row {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
}

.final-price {
    font-size: 1.4rem;
    color: #d9534f;
    font-weight: bold;
}

/* 受け取り時刻選択 */
.pickup-section {
    background: #fff8f0;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 25px;
    border: 2px solid #c07148;
}

.pickup-title {
    font-size: 1.2rem;
    font-weight: bold;
    color: #c07148;
    margin-bottom: 15px;
}

.pickup-inputs {
    display: flex;
    gap: 15px;
    align-items: center;
    flex-wrap: wrap;
}

.pickup-inputs label {
    font-weight: bold;
    color: #333;
}

.pickup-inputs input[type="date"],
.pickup-inputs input[type="time"] {
    padding: 10px;
    font-size: 1rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    min-width: 150px;
}

.required-mark {
    color: #d9534f;
    font-weight: bold;
    margin-left: 5px;
}

/* ボタン */
.button-group {
    display: flex;
    gap: 15px;
    justify-content: center;
    margin-top: 30px;
}

.btn {
    padding: 12px 40px;
    border: none;
    border-radius: 5px;
    font-size: 1.1rem;
    cursor: pointer;
    text-decoration: none;
    display: inline-block;
    transition: opacity 0.3s;
}

.btn-confirm {
    background: #c07148;
    color: white;
}

.btn-cancel {
    background: #999;
    color: white;
}

.btn:hover {
    opacity: 0.8;
}

.error-message {
    color: #d9534f;
    background: #fdeaea;
    padding: 10px;
    border-radius: 5px;
    margin-bottom: 15px;
    display: none;
}
</style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="confirm-box">
                <div class="confirm-title">予約内容の確認</div>

                <div id="errorMsg" class="error-message"></div>

                <!-- 予約内容 -->
                <div class="confirm-info">
                    <div class="info-row">
                        <div class="info-label">商品名:</div>
                        <div class="info-value"><%= merch.getMerchandiseName() %></div>
                    </div>
                    <% if (store != null) { %>
                    <div class="info-row">
                        <div class="info-label">店舗名:</div>
                        <div class="info-value"><%= store.getStoreName() %></div>
                    </div>
                    <% } %>
                    <div class="info-row">
                        <div class="info-label">単価:</div>
                        <div class="info-value">¥<%= merch.getPrice() %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">予約数量:</div>
                        <div class="info-value">
                            <input type="number" id="quantityInput" name="quantity"
                                   min="1" max="<%= merch.getStock() %>" value="<%= quantity %>"
                                   style="width:80px; padding:5px; font-size:1rem; border:1px solid #ddd; border-radius:5px;">
                            個 (在庫: <%= merch.getStock() %>個)
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">消費期限:</div>
                        <div class="info-value"><%= merch.getUseByDate() %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">小計:</div>
                        <div class="info-value total-price" id="totalPrice">¥<%= totalPrice %></div>
                    </div>
                </div>

                <!-- ポイント使用セクション -->
                <div class="points-section">
                    <div class="points-title">ポイントを使用する</div>

                    <div class="points-info">
                        <div class="user-points">
                            保有ポイント: <strong id="userPoints"><%= userPoints %></strong> pt
                        </div>
                        <div class="points-input-group">
                            <label>使用ポイント:</label>
                            <input type="number" id="pointsInput" name="pointsToUse"
                                   min="0" max="<%= userPoints %>" value="0">
                            <span>pt</span>
                            <button type="button" class="use-all-btn" id="useAllPointsBtn">全て使う</button>
                        </div>
                    </div>

                    <p class="points-note">※ 1ポイント = 1円として使用できます</p>

                    <div class="discount-info">
                        <div class="discount-row">
                            <span>小計:</span>
                            <span id="subtotal">¥<%= totalPrice %></span>
                        </div>
                        <div class="discount-row">
                            <span>ポイント値引き:</span>
                            <span id="pointsDiscount" style="color:#4a90e2;">- ¥0</span>
                        </div>
                        <div class="discount-row" style="border-top:2px solid #ddd; padding-top:10px; margin-top:10px;">
                            <span style="font-weight:bold;">お支払い金額:</span>
                            <span class="final-price" id="finalPrice">¥<%= totalPrice %></span>
                        </div>
                    </div>
                </div>

                <!-- 受け取り時刻選択 -->
                <div class="pickup-section">
                    <div class="pickup-title">
                        受け取り希望日時<span class="required-mark">*</span>
                    </div>
                    <div class="pickup-inputs">
                        <label for="pickupDate">日付:</label>
                        <input type="date" id="pickupDate" name="pickupDate" required>

                        <label for="pickupTime">時刻:</label>
                        <input type="time" id="pickupTime" name="pickupTime" required>
                    </div>
                    <p style="margin-top:10px; color:#666; font-size:0.9rem;">
                        ※ 店舗の営業時間内、かつ消費期限(<%= merch.getUseByDate() %>)までにご指定ください
                    </p>
                </div>

                <!-- ボタン -->
                <form id="reserveForm" action="<%= request.getContextPath() %>/foodloss/ReserveExecute.action" method="post">
                    <input type="hidden" name="merchandiseId" value="<%= merch.getMerchandiseId() %>">
                    <input type="hidden" id="quantityHidden" name="quantity">
                    <input type="hidden" id="pickupDateTime" name="pickupDateTime">
                    <input type="hidden" id="pointsToUseHidden" name="pointsToUse">

                    <div class="button-group">
                        <button type="submit" class="btn btn-confirm">予約を確定する</button>
                        <a href="javascript:history.back();" class="btn btn-cancel">戻る</a>
                    </div>
                </form>
            </div>
        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="footer.jsp" />
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const pickupDateInput = document.getElementById('pickupDate');
    const pickupTimeInput = document.getElementById('pickupTime');
    const quantityInput = document.getElementById('quantityInput');
    const pointsInput = document.getElementById('pointsInput');
    const totalPriceElement = document.getElementById('totalPrice');
    const subtotalElement = document.getElementById('subtotal');
    const pointsDiscountElement = document.getElementById('pointsDiscount');
    const finalPriceElement = document.getElementById('finalPrice');
    const useAllPointsBtn = document.getElementById('useAllPointsBtn');
    const form = document.getElementById('reserveForm');
    const errorMsg = document.getElementById('errorMsg');

    const unitPrice = <%= merch.getPrice() %>;
    const maxStock = <%= merch.getStock() %>;
    const userPoints = <%= userPoints %>;
    const useByDate = '<%= merch.getUseByDate() %>';

    // 金額計算関数
    function updatePrices() {
        const qty = parseInt(quantityInput.value) || 1;
        const pointsToUse = parseInt(pointsInput.value) || 0;

        // 小計計算
        const subtotal = unitPrice * qty;

        // 使用ポイントの上限チェック（小計を超えない、保有ポイントを超えない）
        const maxUsablePoints = Math.min(userPoints, subtotal);
        if (pointsToUse > maxUsablePoints) {
            pointsInput.value = maxUsablePoints;
            return updatePrices();
        }

        // 最終金額計算
        const finalPrice = subtotal - pointsToUse;

        // 表示更新
        totalPriceElement.textContent = '¥' + subtotal.toLocaleString();
        subtotalElement.textContent = '¥' + subtotal.toLocaleString();
        pointsDiscountElement.textContent = '- ¥' + pointsToUse.toLocaleString();
        finalPriceElement.textContent = '¥' + finalPrice.toLocaleString();

        // ポイント入力欄の最大値を動的に設定
        pointsInput.max = maxUsablePoints;
    }

    // 数量変更時
    quantityInput.addEventListener('input', function() {
        let qty = parseInt(this.value);
        if (qty < 1) {
            qty = 1;
            this.value = 1;
        }
        if (qty > maxStock) {
            qty = maxStock;
            this.value = maxStock;
        }
        updatePrices();
    });

    // ポイント変更時
    pointsInput.addEventListener('input', function() {
        let points = parseInt(this.value) || 0;
        if (points < 0) {
            points = 0;
            this.value = 0;
        }
        updatePrices();
    });

    // 全て使うボタン
    useAllPointsBtn.addEventListener('click', function() {
        const subtotal = unitPrice * (parseInt(quantityInput.value) || 1);
        const maxUsablePoints = Math.min(userPoints, subtotal);
        pointsInput.value = maxUsablePoints;
        updatePrices();
    });

    // 今日の日付を最小値に設定
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    pickupDateInput.min = yyyy + '-' + mm + '-' + dd;

    // 消費期限を最大値に設定
    pickupDateInput.max = useByDate;

    // デフォルトで明日の日付を設定
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowStr = tomorrow.getFullYear() + '-' +
                        String(tomorrow.getMonth() + 1).padStart(2, '0') + '-' +
                        String(tomorrow.getDate()).padStart(2, '0');
    pickupDateInput.value = tomorrowStr;

    // デフォルトで10:00を設定
    pickupTimeInput.value = '10:00';

    // フォーム送信時のバリデーション
    form.addEventListener('submit', function(e) {
        e.preventDefault();

        const date = pickupDateInput.value;
        const time = pickupTimeInput.value;
        const quantity = parseInt(quantityInput.value);
        const pointsToUse = parseInt(pointsInput.value) || 0;

        // 入力チェック
        if (!date || !time) {
            showError('受け取り日時を入力してください。');
            return false;
        }

        if (quantity < 1 || quantity > maxStock) {
            showError('予約数量は1個以上、在庫数以下で指定してください。');
            return false;
        }

        if (pointsToUse < 0 || pointsToUse > userPoints) {
            showError('使用ポイントが不正です。');
            return false;
        }

        // 日時を組み合わせる
        const pickupDateTime = date + ' ' + time + ':00';
        document.getElementById('pickupDateTime').value = pickupDateTime;
        document.getElementById('quantityHidden').value = quantity;
        document.getElementById('pointsToUseHidden').value = pointsToUse;

        // 過去の日時チェック
        const selectedDateTime = new Date(date + 'T' + time);
        const now = new Date();
        if (selectedDateTime <= now) {
            showError('受け取り日時は現在より後の日時を指定してください。');
            return false;
        }

        // 消費期限チェック
        const useByDateTime = new Date(useByDate + 'T23:59:59');
        if (selectedDateTime > useByDateTime) {
            showError('受け取り日時は消費期限(' + useByDate + ')内で指定してください。');
            return false;
        }

        // エラーがなければ送信
        form.submit();
    });

    function showError(message) {
        errorMsg.textContent = message;
        errorMsg.style.display = 'block';
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // 初期表示時の金額計算
    updatePrices();
});
</script>

</body>
</html>