package com.levenetsa.fetcher.services;

import com.levenetsa.fetcher.utils.TrustAllX509TrustManager;
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
        System.out.println("Grabbed amount of reviews : " + total);
        return total;
    }

    public List<Pair<String, String>> getAllReviews() {
        int reviewsPages = (int) Math.ceil(((double) total) / 100);
        total = 0;
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
        int counter = 0;
        for (Element review : responses) {
            String kclass = review.className().replace("response ", "");
            result.add(new Pair<>(kclass, review.select("table").text()));
            counter++;
        }
        total += counter;
        System.out.println("Grabbed " + counter + "revievs. Total now is " + total + ".");
        return result;
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
