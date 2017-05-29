package com.levenetsa.fetcher.services;

import com.levenetsa.fetcher.dao.FilmDao;
import com.levenetsa.fetcher.entity.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SearchService {
    private Logger logger;
    private FilmDao filmDao;

    public SearchService(){
        logger = LoggerFactory.getLogger(this.getClass());
        filmDao = new FilmDao();
    }

    public String getResult(String partOfname){
        List<Film> films = filmDao.getByPartOfName(partOfname);
        if (films.size() == 0) return ("[\"empty\"]");
        StringBuilder result = new StringBuilder("[");
        int size = films.size() > 10 ? 9 : films.size();
        for (int i = 0; i < size; i++){
            result.append(films.get(i).toJsonp()).append(",");
        }
        result.deleteCharAt(result.length() - 1);
        result.append("]");
        return result.toString();
    }
}
