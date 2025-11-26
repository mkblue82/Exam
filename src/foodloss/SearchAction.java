package foodloss;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Merchandise;
import bean.MerchandiseImage;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
import tool.Action;
import tool.DBManager;

public class SearchAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        System.out.println("===== SearchAction START =====");

        String keyword = req.getParameter("keyword");
        System.out.println("検索キーワード: [" + keyword + "]");

        List<Merchandise> searchResults = new ArrayList<>();

        try (Connection con = new DBManager().getConnection()) {
            MerchandiseDAO merchDAO = new MerchandiseDAO(con);
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);

            if (keyword != null && !keyword.trim().isEmpty()) {
                // キーワードで検索
                System.out.println("searchByKeyword実行前");
                searchResults = merchDAO.searchByKeyword(keyword.trim());
                System.out.println("検索結果: " + searchResults.size() + "件");

                // 各商品に画像をセット
                for (Merchandise m : searchResults) {
                    List<MerchandiseImage> images = imageDAO.selectByMerchandiseId(m.getMerchandiseId());
                    m.setImages(images);
                    System.out.println("  商品: " + m.getMerchandiseName() + " / 画像数: " + images.size());
                }
            } else {
                System.out.println("キーワードがnullまたは空です");
            }

            // 検索結果をリクエストスコープにセット
            req.setAttribute("itemList", searchResults);
            req.setAttribute("searchKeyword", keyword);

            System.out.println("itemListセット完了: " + searchResults.size() + "件");

        } catch (Exception e) {
            System.out.println("SearchActionで例外発生:");
            e.printStackTrace();
            req.setAttribute("errorMessage", "検索中にエラーが発生しました。");
        }

        // main_user.jspにフォワード
        System.out.println("main_user.jspへフォワード");
        req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);

        System.out.println("===== SearchAction END =====");
    }
}