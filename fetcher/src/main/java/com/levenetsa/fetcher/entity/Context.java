package com.levenetsa.fetcher.entity;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Context {
    private List<Review> reviews;
    private Film film;
    private Boolean tScore;
    private Boolean cValue;
    private Boolean uslovn;
    private Boolean stats;
    DecimalFormat df;

    Integer uniqN;
    HashMap<String, Col> colloc;
    HashMap<String, Col> collocN;
    HashMap<String, Col> collocUslovn;
    HashMap<String, Col> collocTScore;
    HashMap<String, Col> collocDice;
    HashMap<String, Col> collocMI;
    HashMap<Integer, Col> collocCValue;
    int[] word;
    String[] uniqWords;
    String[] words;
    HashMap<String, Integer> indexes;

    public Context(List<Review> reviews, Film film) {
        this.film = film;
        this.setReviews(reviews);
        df = new DecimalFormat("0.00000");
    }

    public void countDice() {
        collocDice = new HashMap<>();
        colloc.forEach((x, y) -> {
            double v = getDice(y.i, y.j, y.v);
            if (collocN.containsKey(x)) {
                collocN.get(x).v += 1;
            } else {
                collocN.put(x, new Col(y.i, y.j, 1));
            }
            collocDice.put(x, new Col(y.i, y.j, v));
        });
    }

    private double getDice(int i, int j, double v) {
        return 2 * v / (word[i] * word[j]);
        //return 14 + Math.log(2 * v / (word[i] * word[j])) / Math.log(2);
    }

    public void countMI() {
        collocMI = new HashMap<>();
        colloc.forEach((x, y) -> {
            double v = getMI(y.i, y.j, y.v);
            if (collocN.containsKey(x)) {
                collocN.get(x).v += 1;
            } else {
                collocN.put(x, new Col(y.i, y.j, 1));
            }
            collocMI.put(x, new Col(y.i, y.j, v));
        });
    }

    private double getMI(int i, int j, double v) {
        return Math.log(v * v * v * words.length / (word[i] * word[j])) / Math.log(2);
    }

    public void countCValue() {
        if (cValue) return;
        if (!uslovn) countUslovn(-1, -1, -1D, -1D);
        collocCValue = new HashMap<>();
        colloc.forEach((x, y) -> {
            if (!collocCValue.containsKey(y.i)) {
                collocCValue.put(y.i, getCValue(y.i, y.j, new Col(-1, -1, y.v)));
            } else {
                getCValue(y.i, y.j, collocCValue.get(y.i));
            }
        });
        collocCValue.forEach((x, y) -> {
            y.v = (((double) word[y.i] / words.length) - ((double) y.v / (y.j)));
        });
        cValue = true;
    }

    private Col getCValue(int i, int j, Col v) {
        if (v.i == -1) {
            v.i = i;
            v.j = (int) v.v;
            v.v = collocUslovn.get("" + i + '_' + j).v;
            return v;
        } else {
            v.j += v.v;
            v.v += collocUslovn.get("" + i + '_' + j).v;
            return v;
        }
    }

    public void countTScore(Integer mN, Integer N, Double mF, Double F) {
        if (tScore) return;
        if (!stats) countStats();
        collocTScore = new HashMap<>();
        colloc.forEach((x, y) -> {
            if ((N == -1 || word[y.i] < N && word[y.j] < N) && (mN == -1 || word[y.i] > mN && word[y.j] > mN)) {
                double v = getTScore(y.i, y.j);
                if ((mF == -1 || v >= mF) && (F == -1 || v <= F) && v != 0) {
                    if (collocN.containsKey(x)) {
                        collocN.get(x).v += 1;
                    } else {
                        collocN.put(x, new Col(y.i, y.j, 1));
                    }
                    collocTScore.put(x, new Col(y.i, y.j, v));
                }
            }
        });
        tScore = true;
    }

    private double getTScore(int i, int j) {
        if (!colloc.containsKey("" + i + '_' + j)) return 0;
        return ((double) colloc.get("" + i + '_' + j).v - (((double) word[j]) * word[i]) / words.length) / Math.sqrt((double) colloc.get("" + i + '_' + j).v);
    }


    public void countUslovn(Integer mN, Integer N, Double mF, Double F) {
        if (uslovn) return;
        if (!stats) countStats();
        collocUslovn = new HashMap<>();
        colloc.forEach((x, y) -> {
            if ((N == -1 || word[y.i] < N && word[y.j] < N) && (mN == -1 || word[y.i] > mN && word[y.j] > mN)) {
                double v = ((double) y.v) / word[y.j];
                if (v != 0 && (mF == -1 || v >= mF) && (F == -1 || v <= F)) {
                    if (collocN.containsKey(x)) {
                        collocN.get(x).v += 1;
                    } else {
                        collocN.put(x, new Col(y.i, y.j, 1));
                    }
                    collocUslovn.put(x, new Col(y.i, y.j, v));
                }
            }
        });
        uslovn = true;
    }

    public void countStats() {
        if (stats) return;
        indexes = new HashMap<>();
        ArrayList<String> tmp = new ArrayList<>();
        reviews.forEach(r -> tmp.addAll(Arrays.asList(r.getContent().split("\\s"))));
        words = tmp.stream().toArray(String[]::new);
        uniqWords = new HashSet<>(Arrays.asList(words)).stream().toArray(String[]::new);
        uniqN = uniqWords.length;
        for (int i = 0; i < uniqN; i++) {
            indexes.put(uniqWords[i], i);
        }
        word = new int[uniqN];
        System.out.println("size " + uniqN);
        colloc = new HashMap<>();
        collocN = new HashMap<>();
        word[getIndex(0)]++;
        word[getIndex(words.length - 1)]++;
        colloc.put("" + getIndex(words.length - 2) + "_" + getIndex(words.length - 1),
                new Col(getIndex(words.length - 2), getIndex(words.length - 1), 1));
        colloc.put("" + getIndex(0) + "_" + 1, new Col(getIndex(0), 1, 1));
        for (int i = 1; i < words.length - 1; i++) {
            int iind = getIndex(i);
            if (iind > uniqN || getIndex(i - 1) >uniqN || getIndex(i + 1)> uniqN)
                throw new ArrayIndexOutOfBoundsException();
            word[iind]++;
            String key1 = "" + getIndex(i - 1) + '_' + iind;
            String key2 = "" + iind + '_' + getIndex(i + 1);
            if (colloc.containsKey(key1)) {
                colloc.get(key1).v += 1;
            } else {
                colloc.put(key1, new Col(getIndex(i - 1), iind, 1));
            }
            if (colloc.containsKey(key2)) {
                colloc.get(key2).v += 1;
            } else {
                colloc.put(key2, new Col(iind, getIndex(i + 1), 1));
            }
        }
        System.out.println("hash size " + colloc.size());
        applyStopWords();
        stats = true;
    }

    private int getIndex(int i) {
        return indexes.get(words[i]);
    }

    private void applyStopWords() {
        String[] stopWords = new String[]{
                "для", "на", "по", "со", "из", "от", "до", "без", "над", "под", "за", "при", "после", "во",
                "не", "же", "то", "бы", "всего", "итого", "даже", "да", "нет",
                "или", "но", "дабы", "затем", "потом", "коли", "лишь только",
                "как", "так", "еще", "тот", "откуда", "зачем", "почему", "значительно",
                "он", "мы", "его", "вы", "вам", "вас", "ее", "что", "который", "их", "все", "они", "я", "весь", "мне", "меня", "таким", "весь", "всех",
                "кб", "мб", "дн", "руб", "ул", "кв", "дн", "гг",
                "ой", "ого", "эх", "браво", "здравствуйте", "спасибо", "извините",
                "что-то", "какой-то", "где-то", "как-то", "зачем-то", "из-за", "дальше", "ближе", "раньше", "позже", "когда-то",
                "скажем", "может", "допустим", "честно говоря", "например", "на самом деле", "однако", "вообще", "в общем", "вероятно",
                "всего", "почти", "примерно", "около", "где-то", "порядка",
                "является", "есть", "иметь", "хотеть", "содержаться", "существует",
                "осуществлять", "оказывается", "можно",
                "ни", "P", "S",
                "б", "г", "д", "е", "ё", "ж", "з", "й", "л", "м", "н", "п", "р", "с", "т", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю",
                "Б", "Г", "Д", "Е", "Ё", "Ж", "З", "Й", "Л", "М", "Н", "П", "Р", "С", "Т", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю",
                "один", "два", "три", "пять", "шесть", "семь", "восемь", "девять", "и", "в", "вот", "этот", "уже", "а",
                "у", "о", "к","мочь","это","герой","сам","себя","актерский","сей","мой","точка","актер","быть","самый","фильм"
                ,"год","раз","очень","свой","когда","первый","второй","третий","пятый","если","ты"//,""
        };
        for (String stop :
                stopWords) {
            checkStopWord(stop);
        }
        Arrays.stream(film.getCast().split(" ")).forEach(name -> checkStopWord(name));
    }

    private void checkStopWord(String stop) {
        Integer index = indexes.get(stop.length() == 1 ? stop : stop.toLowerCase());
        if (index != null) {
            for (int i = 0; i < uniqN; i++) {
                String key1 = "" + index + "_" + i;
                String key2 = "" + i + "_" + index;
                colloc.remove(key1);
                colloc.remove(key2);
            }
        }
    }

    public String getMostCallocated(String usl) {
        switch (usl) {
            case "mi": {
                StringBuilder sb = new StringBuilder("");
                collocMI.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue().v, a.getValue().v)).limit(20)
                        .forEach(x -> sb.append(uniqWords[x.getValue().i]).append(" ").append(uniqWords[x.getValue().j]).append("<br />"));
                return sb.toString();
            }
            case "uslovn": {
                StringBuilder sb = new StringBuilder("");
                collocUslovn.entrySet().stream().filter(x -> x.getValue().v < 1)
                        .sorted((a, b) -> Double.compare(b.getValue().v, a.getValue().v)).limit(20)
                        .forEach(x -> sb.append(uniqWords[x.getValue().i]).append(" ").append(uniqWords[x.getValue().j]).append("<br />"));
                return sb.toString();
            }
            /*case "cValue": {
                StringBuilder sb = new StringBuilder("");
                collocCValue.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue().v, a.getValue().v)).limit(20)
                        .forEach(x -> sb.append(uniqWords[x.getValue().i]).append(" ").append(df.format(x.getValue().v)).append("<br />"));
                return sb.toString();
            }*/
            case "tScore": {
                StringBuilder sb = new StringBuilder("");
                collocTScore.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue().v, a.getValue().v)).limit(20)
                        .forEach(x -> sb.append(uniqWords[x.getValue().i]).append(" ").append(uniqWords[x.getValue().j]).append("<br />"));
                return sb.toString();
            }case "dice": {
                StringBuilder sb = new StringBuilder("");
                collocDice.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue().v, a.getValue().v)).limit(20)
                        .forEach(x -> sb.append(uniqWords[x.getValue().i]).append(" ").append(uniqWords[x.getValue().j]).append("<br />"));
                return sb.toString();
            }
            default: {
                return "unknown request";
            }
        }

    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        this.tScore = false;
        this.cValue = false;
        this.uslovn = false;
        this.stats = false;
    }

    public Boolean isTScoreCounted() {
        return tScore;
    }

    public Boolean isCValueCounted() {
        return cValue;
    }

    public Boolean isUslovnCounted() {
        return uslovn;
    }

    public Boolean isStatsCounted() {
        return stats;
    }

    public List<String> getMostCallocatedVal(String tScore) {
        return  collocTScore.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue().v, a.getValue().v))
                .map(x -> uniqWords[x.getValue().i]+ " " + uniqWords[x.getValue().j] + " ").collect(Collectors.toList());
    }

    public class Coup {

        public Coup(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int i, j;
    }


    public class Col {

        public Col(int i, int j, double v) {
            this.i = i;
            this.j = j;
            this.v = v;
        }

        public int i, j;
        public double v;
    }
}
