<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Store" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>店舗情報</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .store-info-container {
            max-width: 900px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .store-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid #c07148;
        }

        .store-header h2 {
            font-size: 1.8rem;
            color: #333;
            margin: 0;
            text-decoration: none;
            border: none;
        }

        .favorite-btn {
            padding: 0.8rem 2rem;
            border: 2px solid #c07148;
            border-radius: 8px;
            background-color: #fff;
            color: #c07148;
            font-size: 1rem;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .favorite-btn:hover {
            background-color: #c07148;
            color: #fff;
            transform: translateY(-2px);
        }

        .favorite-btn.active {
            background-color: #c07148;
            color: #fff;
        }

        .favorite-btn::before {
            content: "☆";
            font-size: 1.2rem;
        }

        .favorite-btn.active::before {
            content: "★";
        }

        /* Google Map表示エリア */
        .map-container {
            width: 100%;
            height: 400px;
            margin-bottom: 2rem;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        #map {
            width: 100%;
            height: 100%;
        }

        .store-details {
            margin-bottom: 2rem;
        }

        .detail-row {
            display: flex;
            padding: 1rem 0;
            border-bottom: 1px solid #eee;
        }

        .detail-label {
            width: 150px;
            font-weight: bold;
            color: #666;
        }

        .detail-value {
            flex: 1;
            color: #333;
        }

        .dummy-notice {
            background-color: #fff3cd;
            color: #856404;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1rem;
            border-left: 4px solid #ffc107;
        }

        @media screen and (max-width: 600px) {
            .store-info-container {
                margin: 20px;
                padding: 1.5rem;
            }

            .store-header {
                flex-direction: column;
                gap: 1rem;
                align-items: flex-start;
            }

            .detail-row {
                flex-direction: column;
            }

            .detail-label {
                width: 100%;
                margin-bottom: 0.5rem;
            }

            .map-container {
                height: 300px;
            }
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />

    <%
        Store store = (Store) request.getAttribute("store");
        Boolean isFavorite = (Boolean) request.getAttribute("isFavorite");

        // storeがnullの場合、ダミーデータ（画面確認用）
        boolean isDummy = false;
        if (store == null) {
            isDummy = true;
            store = new Store();
            store.setStoreId(1);
            store.setStoreName("サンプル店舗");
            store.setAddress("東京都渋谷区1-2-3");
            store.setPhone("03-1234-5678");
        }

        if (isFavorite == null) isFavorite = false;
    %>

    <!-- メインエリア -->
    <main class="column">
        <div class="main-contents">
            <div class="store-info-container">


                <div class="store-header">
                    <h2><%= store.getStoreName() %></h2>
                    <button class="favorite-btn <%= isFavorite ? "active" : "" %>"
                            id="favoriteBtn"
                            data-store-id="<%= store.getStoreId() %>">
                        お気に入り
                    </button>
                </div>

                <!-- Google Map表示 -->
                <div class="map-container">
                    <div id="map"></div>
                </div>

                <div class="store-details">
                    <div class="detail-row">
                        <div class="detail-label">店舗名</div>
                        <div class="detail-value"><%= store.getStoreName() %></div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">住所</div>
                        <div class="detail-value"><%= store.getAddress() != null ? store.getAddress() : "未設定" %></div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">電話番号</div>
                        <div class="detail-value"><%= store.getPhone() != null ? store.getPhone() : "未設定" %></div>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="footer.jsp" />
</div>

<!-- Google Maps API -->
<script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&callback=initMap" async defer></script>
<script>
let map;
let marker;

function initMap() {
    // 店舗の住所
    const storeAddress = "<%= store.getAddress() != null ? store.getAddress() : "" %>";

    // デフォルトの位置（東京駅）
    const defaultLocation = { lat: 35.6812, lng: 139.7671 };

    // マップを初期化
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 15,
        center: defaultLocation
    });

    // 住所から緯度経度を取得してマーカーを設置
    if (storeAddress) {
        const geocoder = new google.maps.Geocoder();
        geocoder.geocode({ address: storeAddress }, function(results, status) {
            if (status === 'OK') {
                const location = results[0].geometry.location;
                map.setCenter(location);

                marker = new google.maps.Marker({
                    map: map,
                    position: location,
                    title: "<%= store.getStoreName() %>"
                });

                // 情報ウィンドウを追加
                const infoWindow = new google.maps.InfoWindow({
                    content: '<div style="padding: 10px;"><strong><%= store.getStoreName() %></strong><br><%= store.getAddress() %></div>'
                });

                marker.addListener('click', function() {
                    infoWindow.open(map, marker);
                });
            } else {
                console.error('Geocode was not successful for the following reason: ' + status);
            }
        });
    }
}
</script>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script>
$(document).ready(function() {
    // お気に入りボタンのクリックイベント
    $('#favoriteBtn').on('click', function() {
        const btn = $(this);
        const storeId = btn.data('store-id');
        const isActive = btn.hasClass('active');

        // Ajax通信でお気に入り登録/削除
        $.ajax({
            url: '${pageContext.request.contextPath}/foodloss/ToggleFavorite.action',
            type: 'POST',
            data: {
                storeId: storeId,
                action: isActive ? 'remove' : 'add'
            },
            success: function(response) {
                if (response.success) {
                    // ボタンの表示を切り替え
                    btn.toggleClass('active');

                    // メッセージ表示（オプション）
                    const message = isActive ? 'お気に入りから削除しました' : 'お気に入りに追加しました';
                    alert(message);
                } else {
                    alert('エラーが発生しました。もう一度お試しください。');
                }
            },
            error: function() {
                alert('通信エラーが発生しました。');
            }
        });
    });
});
</script>
</body>
</html>