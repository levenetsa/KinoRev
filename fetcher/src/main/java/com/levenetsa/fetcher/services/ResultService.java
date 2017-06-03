package com.levenetsa.fetcher.services;

import com.levenetsa.fetcher.dao.FilmDao;
import com.levenetsa.fetcher.dao.ResultDao;
import com.levenetsa.fetcher.dao.ReviewDao;
import com.levenetsa.fetcher.entity.Result;
import com.levenetsa.fetcher.entity.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("Downloading reviews for film " + id);
        List<Review> reviews = reviewDao.downloadReviewsFor(id);
        logger.info("Film exists. Running script.py");
        return countResult(reviews, id);
    }

    private String countResult(List<Review> reviews, Integer id) {
        Result r = new Result();
        r.setFilmId(id);
        if (reviews.size() == 0) {
            r.setText(NOT_ENOUGH_REVIEWS);
        } else {
            r.setText("counted");
        }
        resultDao.save(r);
        return "{\"text\": \"" + r.getText() + "\"}";
    }
}
