package com.company;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ReviewsManager {
    private String ID;
    private Document document;
    private Integer positive;
    private Integer negative;
    private Integer neutral;
    private Integer total;

    public ReviewsManager(String id) {
        this.ID = id;
    }

    public void downloadContent() {
        //initSslSkipping();
        document = download("https://www.kinopoisk.ru/film/" + ID + "/ord/rating/perpage/100/#list");
    }

    public Integer getReviewsAmount() {
        Element tmp = document.select("li[class$=pos]").select("b").last();//.text();
        positive = Integer.parseInt(tmp == null ? "0" : tmp.text());
        tmp = document.select("li[class$=neg]").select("b").last();
        negative = Integer.parseInt(tmp == null ? "0" : tmp.text());
        tmp = document.select("li[class$=neut]").select("b").last();
        neutral = Integer.parseInt(tmp == null ? "0" : tmp.text());
        total = positive + neutral + negative;
        return total;
    }

    public List<Pair<String, String>> getAllReviews() {
        int reviewsPages = (int) Math.ceil(((double) total) / 100);
        List<Pair<String, String>> result = getReviews(document);
        for (int i = 2; i <= reviewsPages; i++) {
            result.addAll(getReviews(download("https://www.kinopoisk.ru/film/" + ID + "/ord/rating/perpage/100/page/" + i + "/#list")));
        }
        return result;
    }

    private Document download(String s) {
        Document result = null;
        try {
            result = Jsoup.connect(s).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Pair<String, String>> getReviews(Document document) {
        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        Elements responses = document.select("div[itemprop=reviews]");
        for (Element review : responses) {
            result.add(new Pair<>("",review.select("table").text()));
        }
        return result;
    }

    public String[] getBadRevs() {
        return new String[0];
    }

    public String[] getGoodRevs() {
        return null;
    }

    private void initSslSkipping() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, new TrustManager[]{new TrustAllX509TrustManager()}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
