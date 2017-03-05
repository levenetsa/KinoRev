package com.company;

import javafx.util.Pair;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;

import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;



public class Main {
    public static final String ID = "342";


    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, IOException, MyStemApplicationException {
        ReviewsManager  manager = new ReviewsManager(ID);
        manager.downloadContent();
        Integer revsAmount = manager.getReviewsAmount();
        List<Pair<String,String>> reviews = manager.getAllReviews();
        System.out.print(revsAmount);
        writeBuffered(reviews, 8196);

        Runtime.getRuntime().exec("./mystem -ld input output");
        //String[] goodRevs = getGoodRevs();
        //String[] badRevs = getBadRevs();
    }

    private static void writeBuffered(List<Pair<String, String>> records, int bufSize) throws IOException {
        File file = new File("input");
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);

            System.out.print("Writing buffered (buffer size: " + bufSize + ")... ");
            write(records, bufferedWriter);
        } finally {
            // comment this out if you want to inspect the files afterward
           // file.delete();
        }
    }

    private static void write(List<Pair<String, String>> records, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        for (Pair<String, String> record: records) {
            writer.write(record.getValue());
            writer.write(" satana ");
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000f + " seconds");
    }
}
