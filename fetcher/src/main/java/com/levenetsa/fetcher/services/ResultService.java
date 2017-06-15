package com.levenetsa.fetcher.services;

import com.levenetsa.fetcher.dao.FilmDao;
import com.levenetsa.fetcher.dao.ResultDao;
import com.levenetsa.fetcher.dao.ReviewDao;
import com.levenetsa.fetcher.entity.Context;
import com.levenetsa.fetcher.entity.Film;
import com.levenetsa.fetcher.entity.Result;
import com.levenetsa.fetcher.entity.Review;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultService {
    private static final String NOT_ENOUGH_REVIEWS = "Not enough reviews!";
    private static final String OK = "{\"text\": \"OK\"}";
    private Logger logger;
    private FilmDao filmDao;
    private ResultDao resultDao;
    private ReviewDao reviewDao;

    public ResultService() {
        logger = LoggerFactory.getLogger(this.getClass());
        filmDao = new FilmDao();
        resultDao = new ResultDao();
        reviewDao = new ReviewDao();
    }

    public String getResult(Integer id) {
        logger.info("Request for film with id " + id);
        String result = resultDao.getById(id);
        if (result != null) {
            logger.info("Result already exists");
            return "{\"text\": \"" + result + "\"}";
        }
        logger.info("Result does NOT exist");
        Film film = filmDao.getById(id);
        if (film == null) {
            logger.info("Film does NOT exist");
            filmDao.addFilm(id);
        }
        if (film.getCast() == null) {
            filmDao.updateAndSetCast(film);
        }
        List<Review> reviews = reviewDao.getByFilmId(id);
        if (reviews.size() == 0) {
            logger.info("Downloading reviews for film " + id);
            reviews = reviewDao.downloadReviewsFor(id);
        }
        logger.info("Film exists. Running script.py");
        return countResult(reviews, film);
    }

    private String countResult(List<Review> reviews, Film film) {
        Result r = new Result();
        r.setFilmId(film.getId());
        if (reviews.size() == 0) {
            r.setText(NOT_ENOUGH_REVIEWS);
        } else {
            try {
                Context filmContext = new Context(reviews, film);
                List<Review> g = new ArrayList<>();
                List<Review> n = new ArrayList<>();
                List<Review> b = new ArrayList<>();
                reviews.forEach(re ->{
                    if (re.getMood().equals("good"))
                            g.add(re);
                    if (re.getMood().equals("bad"))
                            b.add(re);
                    if (re.getMood().equals("response"))
                            n.add(re);

                });
                logger.info("context creted");
                //filmContext.countTScore(-1, -1, -1D, -1D);
                //logger.info("t-score counted");
                //filmContext.countCValue();
                //logger.info("C-Value counted");
                //filmContext.countDice();
                //logger.info("dice counted");
                //filmContext.countUslovn(-1, -1, -1D, -1D);
                //logger.info("uslovn counted");
                //filmContext.countMI();
                //logger.info("MI counted");
                Context gk = new Context(g,film);
                gk.setReviews(g);
                gk.countTScore(-1,-1,-1D,-1D);
                List<String> good = gk.getMostCallocatedVal("tScore");
                Context nk = new Context(n,film);
                nk.setReviews(n);
                nk.countTScore(-1,-1,-1D,-1D);
                List<String> neut = nk.getMostCallocatedVal("tScore");
                Context bk = new Context(b,film);
                bk.setReviews(b);
                bk.countTScore(-1,-1,-1D,-1D);
                List<String> bad= bk.getMostCallocatedVal("tScore");
                ArrayList<String> badgood = new ArrayList<>(bad);
                ArrayList<String> badneut = new ArrayList<>(bad);
                ArrayList<String> goodneut = new ArrayList<>(good);
                badgood.retainAll(badneut);
                badgood.retainAll(goodneut);
                /*ArrayList<String> goodbad = new ArrayList<>(good);
                ArrayList<String> neutbad = new ArrayList<>(neut);
                ArrayList<String> neutgood = new ArrayList<>(neut);
                goodbad.retainAll(bad);
                goodbad.retainAll(neut);
                neutbad.retainAll(bad);
                neutbad.retainAll(good);
                badgood.retainAll(good);
                badgood.retainAll(neut);
                HashMap<String,Boolean> goode = new HashMap<>();
                HashMap<String,Boolean> bade = new HashMap<>();
                HashMap<String,Boolean> neute = new HashMap<>();
                goodbad.forEach(x->goode.put(x,true));
                neutbad.forEach(x->neute.put(x,true));
                badgood.forEach(x->bade.put(x,true));
                logger.info("Mood counted");*/
                HashMap<String,Boolean> goode = new HashMap<>();
                badgood.forEach(x->goode.put(x,false));
                StringBuilder gS = new StringBuilder("");
                good.stream().filter(x->!goode.containsKey(x)).forEach(x->gS.append(x).append("<br />"));
                StringBuilder bS = new StringBuilder("");
                bad.stream().filter(x->!goode.containsKey(x)).forEach(x->bS.append(x).append("<br />"));
                StringBuilder nS = new StringBuilder("");
                neut.stream().filter(x->!goode.containsKey(x)).forEach(x->nS.append(x).append("<br />"));
                r.setText("{\"good\":\"" + gS.toString() +"\"," +
                        "\"neut\":\"" + nS.toString() +"\"," +
                        "\"bad\":\"" + bS.toString() +"\"}");
                logger.info("answer counted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resultDao.save(r);
        return r.getText();
    }

    private void putIntoFile(List<Review> reviews) {
        try {
            PrintWriter write = new PrintWriter("input1");
            StringBuilder content = new StringBuilder("");
            reviews.forEach(x -> content.append(x.getContent()).append("\n"));
            write.print(content);
            write.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String recountResult(int id) {
        List<Review> reviews = reviewDao.getByFilmId(id);
        Film film = filmDao.getById(id);
        return countResult(reviews, film);
    }

    public String addFilm(int id) {
        filmDao.addFilm(id);
        return OK;
    }

    public String loadReviews() {
        filmDao.getAll().forEach(film -> {
            try {
                logger.info("Checking " + film.getName());
                List<Review> reviews = reviewDao.getByFilmId(film.getId());
                if (reviews.size() == 0) {
                    logger.info("Downloading reviews for film " + film.getId());
                    reviewDao.downloadReviewsFor(film.getId());
                }
                logger.info("Finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return OK;
    }

    public String addFilms(int id1, int id2) {
        StringBuilder result = new StringBuilder("{\"text\": \"\\n");
        DecimalFormat df = new DecimalFormat();
        df.setMinimumIntegerDigits(7);
        for (int id = id1; id < id2; id++) {
            result.append(df.format(id));
            if (filmDao.getById(id) == null) {
                try {
                    filmDao.addFilm(id);
                } catch (Exception e) {
                    result.append(" Unknown exception ").append(e.getMessage()).append("\\n");
                    e.printStackTrace();
                }
            } else {
                result.append(" Film exists\\n");
            }
        }
        return result.append("\"}").toString();
    }
}
