package foodloss;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Merchandise;
import bean.Search;
import bean.Store;
import dao.DAO;
import dao.MerchandiseDAO;
import dao.StoreDAO;
import tool.Action;

public class SearchAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 検索キーワードを取得
        String keyword = request.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            request.setAttribute("error", "検索キーワードを入力してください");
            request.setAttribute("searchResults", new ArrayList<>());
            request.getRequestDispatcher("/jsp/search.jsp").forward(request, response);
            return;
        }

        keyword = keyword.trim();

        // データベース接続を取得
        DAO dao = new DAO();
        Connection connection = null;

        // 検索結果を格納するリスト
        List<Search> searchResults = new ArrayList<>();

        try {
            connection = dao.getConnection();

            // DAOの初期化
            MerchandiseDAO merchandiseDAO = new MerchandiseDAO(connection);
            StoreDAO storeDAO = new StoreDAO(connection);

            // 商品名またはタグで検索
            List<Merchandise> merchandises = merchandiseDAO.searchByKeyword(keyword);

            // 店舗名で検索
            List<Store> stores = storeDAO.searchByKeyword(keyword);

            // 店舗ごとにグループ化
            if (!merchandises.isEmpty()) {
                for (Merchandise merchandise : merchandises) {
                    Store store = storeDAO.selectById(merchandise.getStoreId());

                    if (store == null) continue;

                    // 既存の店舗結果を探す
                    Search existingResult = findResultByStoreId(searchResults, store.getStoreId());

                    if (existingResult != null) {
                        existingResult.addMerchandise(merchandise);
                    } else {
                        Search newResult = new Search();
                        newResult.setStore(store);
                        newResult.addMerchandise(merchandise);
                        searchResults.add(newResult);
                    }
                }
            }

            // 店舗名で検索された場合、その店舗の全商品を追加
            for (Store store : stores) {
                List<Merchandise> storeMerchandises = merchandiseDAO.selectByStoreId(store.getStoreId());

                Search existingResult = findResultByStoreId(searchResults, store.getStoreId());

                if (existingResult != null) {
                    // 重複しない商品のみ追加
                    for (Merchandise m : storeMerchandises) {
                        if (!existingResult.hasMerchandise(m.getMerchandiseId())) {
                            existingResult.addMerchandise(m);
                        }
                    }
                } else if (!storeMerchandises.isEmpty()) {
                    Search newResult = new Search();
                    newResult.setStore(store);
                    newResult.setMerchandises(storeMerchandises);
                    searchResults.add(newResult);
                }
            }

            // 結果をリクエストに設定
            request.setAttribute("searchResults", searchResults);
            request.setAttribute("keyword", keyword);

            // デバッグ用ログ
            System.out.println("=== 検索結果デバッグ ===");
            System.out.println("キーワード: " + keyword);
            System.out.println("検索結果数: " + searchResults.size());
            for (Search result : searchResults) {
                System.out.println("店舗: " + result.getStoreName() + ", 商品数: " + result.getMerchandiseCount());
            }

        } catch (Exception e) {
            // 本番環境ではログに記録し、ユーザーには一般的なメッセージのみ表示
            e.printStackTrace();
            request.setAttribute("error", "検索中にエラーが発生しました。");
            request.setAttribute("searchResults", new ArrayList<>());
            request.setAttribute("keyword", keyword);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 検索結果ページにフォワード
        request.getRequestDispatcher("/jsp/search.jsp").forward(request, response);
    }

    /**
     * 店舗IDで検索結果を探す
     */
    private Search findResultByStoreId(List<Search> results, int storeId) {
        for (Search result : results) {
            if (result.getStoreId() == storeId) {
                return result;
            }
        }
        return null;
    }
}