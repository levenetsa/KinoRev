package com.levenetsa.fetcher.dao;

import com.levenetsa.fetcher.entity.Review;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao implements Dao<Review> {
    private static final String DEVIDING_STRING = " метростроение достопримечательность ";
    private static final String PARSED_DEVIDING_STRING = "\\{метростроение}\\{достопримечательность}";
    private static final String INPUT = "input";
    private static final String OUTPUT = "output";
    private static final Integer BUFF_SIZE = 8196;
    private Logger logger;
    private Integer ID;
    private Document document;
    private Integer positive;
    private Integer negative;
    private Integer neutral;
    private Integer total;

    public List<Review> downloadReviewsFor(Integer id) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.ID = id;
        downloadContent();
        countReviewsAmount();
        List<Review> reviews = mystem(getAllReviews());
        save(reviews);
        return reviews;
    }

    private void downloadContent() {
        //initSslSkipping();
        document = download("https://www.kinopoisk.ru/film/" + ID + "/ord/rating/perpage/100/#list");
    }

    private void countReviewsAmount() {
        Element tmp = document.select("li[class$=pos]").select("b").last();//.text();
        positive = Integer.parseInt(tmp == null ? "0" : tmp.text());
        tmp = document.select("li[class$=neg]").select("b").last();
        negative = Integer.parseInt(tmp == null ? "0" : tmp.text());
        tmp = document.select("li[class$=neut]").select("b").last();
        neutral = Integer.parseInt(tmp == null ? "0" : tmp.text());
        total = positive + neutral + negative;
        logger.info("Grabbed amount of reviews : " + total);
    }

    private List<Review> getAllReviews() {
        int reviewsPages = (int) Math.ceil(((double) total) / 100);
        total = 0;
        List<Review> result = getReviews(document);
        for (int i = 2; i <= reviewsPages; i++) {
            result.addAll(getReviews(download("https://www.kinopoisk.ru/film/" + ID + "/ord/rating/perpage/100/page/" + i + "/#list")));
        }
        return result;
    }

    private List<Review> getReviews(Document document) {
        List<Review> result = new ArrayList<>();
        Elements responses = document.select("div[itemprop=reviews]");
        for (Element review : responses) {
            Review r = new Review();
            r.setFilm_id(ID);
            r.setMood(review.className().replace("response ", ""));
            r.setContent(review.select("table").text());
            result.add(r);
        }
        total += result.size();
        logger.info("Grabbed " + result.size() + "revievs. Total now is " + total + ".");
        return result;
    }

    public void save(List<Review> reviews) {
        StringBuilder sql = new StringBuilder("INSERT INTO reviews (film_id, mood, content) VALUES ");
        reviews.forEach(review -> sql.append("(")
                .append(review.getFilm_id())
                .append(",'")
                .append(getMood(review))
                .append("','")
                .append(review.getContent().replace("'", ""))
                .append("'),"));
        executeQuery(sql.toString().substring(0, sql.length() - 1));
    }

    private String getMood(Review review) {
        if (review.getMood().equals("good") || review.getMood().equals("bad")) {
            return review.getMood();
        }
        return "neut";
    }

    private List<Review> mystem(List<Review> reviews) {
        try {
            writeBuffered(reviews);
            Process p = Runtime.getRuntime().exec("./mystem -ld " + INPUT + " " + OUTPUT);
            Long time = System.currentTimeMillis();
            p.waitFor();
            logger.info("spent for MyStem exec: " + (System.currentTimeMillis() - time));
            return readRevs(reviews);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private static void writeBuffered(List<Review> reviews) throws IOException {
        File file = new File(INPUT);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file), BUFF_SIZE);
        for (int i = 0; i < reviews.size(); i++) {
            writer.write(reviews.get(i).getContent() + DEVIDING_STRING);
        }
        writer.flush();
        writer.close();
    }

    private static List<Review> readRevs(List<Review> reviews) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("output")));
        String val;
        StringBuilder sb = new StringBuilder("");
        String[] srt;
        while ((val = br.readLine()) != null) {
            sb.append(val);
        }
        srt = sb.toString().replace("?", "").split(PARSED_DEVIDING_STRING);
        for (int i = 0; i < reviews.size(); i++) {
            String s = srt[i];
            s = s.substring(1, s.length() - 1);
            reviews.get(i).setContent(s.replace("}{", " "));
        }
        return reviews;
    }

    @Override
    public Review parseResult(ResultSet rs) throws SQLException {
        return null;
    }
}
