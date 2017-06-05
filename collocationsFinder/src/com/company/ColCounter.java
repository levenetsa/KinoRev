package com.company;

import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ColCounter {
    DecimalFormat df;
    private String INPUT = "input_spisok";
    Integer uniqN;
    int[][] colloc;
    int[][] collocN;
    int[] word;
    List<List<String>> texts;
    String content;
    String[] uniqWords;
    String[] words;
    HashMap<String, Integer> indexes;

    public ColCounter(String input){
        this.df = new DecimalFormat("0.00");
        this.INPUT = input;
    }

    public void countTScore(Integer mN, Integer N, Double mF, Double F) {
        List<Col> list = new ArrayList<>();
        for (int i = 0; i < uniqN; i++) {
            for (int j = 0; j < uniqN; j++) {
                if (word[i] < N && word[j] < N && word[i] > mN && word[j] > mN) {
                    double v = getTScore(i, j);
                    if (v >= mF && v <= F) {
                        collocN[i][j]++;
                        list.add(new Col(i,j,v));
                    }
                }
            }
        }
        list.sort((a, b) -> Double.compare(b.v, a.v));
        list.stream().limit(1000).forEach(x -> {
            if (collocN[x.i][x.j] == 2)
            System.out.println(x.w());
        });
    }

    private double getTScore(int i, int j) {
        return ((double) colloc[i][j] - (((double) word[j]) * word[i]) / words.length) / Math.sqrt(colloc[i][j]);
    }


    public void countTenUslovn(Integer mN, Integer N, Double mF, Double F) {
        List<Pair<String, Double>> list = new ArrayList<>();
        for (int i = 0; i < uniqN; i++) {
            for (int j = 0; j < uniqN; j++) {
                if (word[i] < N && word[j] < N && word[i] > mN && word[j] > mN) {
                    Double value = ((double) colloc[i][j]) / word[j];
                    if (value >= mF && value <= F) {
                        collocN[i][j]++;
                        //list.add(new Pair<>(uniqWords[i] + " " + uniqWords[j], ((double) colloc[i][j]) / word[j]));
                    }
                }
            }
        }
        //list.sort((o2, o1) -> o1.getValue().compareTo(o2.getValue()));
        //list.forEach(x -> System.out.println(x.getValue() + " " + x.getKey()));
    }

    public void precountStats() {
        try {
            content = new Scanner(new File(INPUT)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        texts = Arrays.stream(content.split("\n"))
                .map(el -> Arrays.asList(el.split("\\s"))).collect(Collectors.toList());
        indexes = new HashMap<>();
        words = content.split("\n|\\s");
        uniqWords = new HashSet<>(Arrays.asList(words)).stream().toArray(String[]::new);
        uniqN = uniqWords.length;
        for (int i = 0; i < uniqN; i++) {
            indexes.put(uniqWords[i], i);
        }
        word = new int[uniqN];
        colloc = new int[uniqN][uniqN];
        collocN = new int[uniqN][uniqN];
        word[getIndex(0)]++;
        word[getIndex(words.length - 1)]++;
        colloc[getIndex(words.length - 2)][getIndex(words.length - 1)]++;
        colloc[getIndex(0)][getIndex(1)]++;
        for (int i = 1; i < words.length - 1; i++) {
            word[getIndex(i)]++;
            colloc[getIndex(i - 1)][getIndex(i)]++;
            colloc[getIndex(i)][getIndex(i + 1)]++;
        }
    }

    private int getIndex(int i) {
        return indexes.get(words[i]);
    }

    public void printNMostOftenWords(int amount) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        for (int i = 0; i < uniqN; i++) {
            list.add(new Pair<>(uniqWords[indexes.get(uniqWords[i])], word[i]));
        }
        list.stream().sorted((a, b) -> b.getValue().compareTo(a.getValue())).limit(amount)
                .forEach(x -> System.out.println(x.getValue() + " " + x.getKey()));
    }

    public class Col{

        public Col(int i, int j, double v){
            this.i = i;
            this.j = j;
            this.v = v;
        }

        public int i,j;
        public double v;

        public String w(){
            return df.format(v) + " " + collocN[i][j] + " " + uniqWords[i] + " " + uniqWords[j];
        }
    }
}
