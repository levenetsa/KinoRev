package com.company;

import java.util.List;

public class ObsoleteCode {

    public static void countTenTfIdfFor(Integer index) {
        /*List<Pair<String,Double>> data = uniqWords.stream().map(word->new Pair<>(word, tfIdf(texts.get(index),texts,word)))
                .sorted(Comparator.comparing(Pair::getValue)).collect(Collectors.toList());
        for(int j = 0; j < 10; j++){
            int i = data.size() - j - 1;
            System.out.println(data.get(i).getKey() + " " + data.get(i).getValue());
        }*/
    }

    public static double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }

    public static double idf(List<List<String>> docs, String term) {
        double n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(docs.size() / n);
    }

    public static double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }
}
