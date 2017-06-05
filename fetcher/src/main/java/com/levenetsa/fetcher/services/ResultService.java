package com.levenetsa.fetcher.services;

import com.levenetsa.fetcher.dao.FilmDao;
import com.levenetsa.fetcher.dao.ResultDao;
import com.levenetsa.fetcher.dao.ReviewDao;
import com.levenetsa.fetcher.entity.Context;
import com.levenetsa.fetcher.entity.Result;
import com.levenetsa.fetcher.entity.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class ResultService {
    private static final String NOT_ENOUGH_REVIEWS = "Not enough reviews!";
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
        if (filmDao.getById(id) == null) {
            logger.info("Film does NOT exist");
            filmDao.addFilm(id);
        }
        List<Review> reviews = reviewDao.getByFilmId(id);
        if (reviews.size() == 0){
            logger.info("Downloading reviews for film " + id);
            reviews = reviewDao.downloadReviewsFor(id);
        }
        logger.info("Film exists. Running script.py");
        return countResult(reviews, id);
    }

    private String countResult(List<Review> reviews, Integer id) {
        Result r = new Result();
        r.setFilmId(id);
        if (reviews.size() == 0) {
            r.setText(NOT_ENOUGH_REVIEWS);
        } else {
            try {
                Context filmContext = new Context(reviews);
                logger.info("context creted");
                filmContext.countTScore(-1, 500, 0.3, -1D);
                logger.info("t-score counted");
                filmContext.countUslovn(-1, 500, 2D, -1D);
                logger.info("uslovn counted");
                r.setText(filmContext.getMostCallocated());
                logger.info("answer counted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resultDao.save(r);
        return "{\"text\": \"" + r.getText() + "\"}";
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
        return countResult(reviews, id);
    }
}
