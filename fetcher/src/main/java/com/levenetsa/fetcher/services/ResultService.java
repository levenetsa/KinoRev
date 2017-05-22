package com.levenetsa.fetcher.services;

import com.levenetsa.fetcher.dao.FilmDao;
import com.levenetsa.fetcher.dao.ResultDao;
import com.levenetsa.fetcher.entity.Doc;
import com.levenetsa.fetcher.entity.Film;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultService {
    private Logger logger;
    private FilmDao filmDao;
    private ResultDao resultDao;

    public ResultService() {
        logger = LoggerFactory.getLogger(this.getClass());
        filmDao = new FilmDao();
        resultDao = new ResultDao();
    }

    public String getResult(Integer id) {
        logger.info("Request for film with id " + id);
        String result = resultDao.getById(id);
        if (result != null) {
            logger.info("Result already exists");
            return result;
        } else {
            logger.info("Result does NOT exist");
            if (filmDao.getById(id) == null) {
                logger.info("Film does NOT exist");
            } else {
                logger.info("Film exists. Running script.py");
            }
        }
        return id.toString();
    }

    private static final String ID = "342";
    private static final String DEVIDING_STRING = " метростроение достопримечательность ";
    private static final String PARSED_DEVIDING_STRING = "\\{метростроение\\}\\{достопримечательность\\}";

    public static void maan(String[] args) throws IOException {
        ReviewsManager reviewsManager = new ReviewsManager(ID);
        reviewsManager.downloadContent();
        Integer revsAmount = reviewsManager.getReviewsAmount();
        List<Pair<String, String>> reviews = reviewsManager.getAllReviews();
        writeBuffered(reviews, 8196);
        Runtime.getRuntime().exec("./mystem -ld input output");
        DocsManager docsManager = new DocsManager();
        docsManager.attachDocs(prepareDocs());
    }

    private static List<Doc> prepareDocs() throws IOException {
        return readRevs();
    }

    private static List<Doc> readRevs() throws IOException {
        //InputStream is = new BufferedInputStream(new FileInputStream(new File("output")));
        BufferedReader br = new BufferedReader(new FileReader(new File("output")));
        String val;
        int pointer = 0;
        byte[] str = "{?}каждый".getBytes();
        int size = str.length;
        StringBuilder sb = new StringBuilder();
        String[] srt;
        List<Doc> documents = new ArrayList<>();
        while ((val = br.readLine()) != null) {
            srt = val.replace("?", "").split(PARSED_DEVIDING_STRING);
            for (String s : srt) {
                s = s.substring(1, s.length() - 1);
                String[] localStrs = s.split("\\}\\{");
                Doc currentDoc = new Doc(localStrs[localStrs.length - 1], Arrays.copyOf(localStrs, localStrs.length - 1));
                documents.add(currentDoc);
            }
        }
        return documents;
    }

    private static void writeBuffered(List<Pair<String, String>> records, int bufSize) throws IOException {
        File file = new File("input");
        FileWriter writer = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);
        write(records, bufferedWriter);
    }

    private static void write(List<Pair<String, String>> records, Writer writer) throws IOException {
        System.out.println("Writing in input file...");
        long start = System.currentTimeMillis();

        for (Pair<String, String> record : records) {
            writer.write(record.getValue());
            writer.write(getRevType(record.getKey()) + DEVIDING_STRING);
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000f + " seconds spend fror writing in input file.");
    }

    private static String getRevType(String key) {
        if (key.equals("good")) {
            return " хороший";
        } else if (key.equals("bad")) {
            return " плохой";
        } else {
            return " нейтральный";
        }
    }
}
