package com.levenetsa.fetcher.dao;

import com.levenetsa.fetcher.entity.Film;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FilmDao implements Dao<Film> {

    private List<Film> byPartOfName;

    @Override
    public Film parseResult(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setLastFetched(rs.getTimestamp("last_fetched"));
        return film;
    }

    public Film getById(Integer id) {
        List<Film> results = executeQuery("SELECT * FROM films WHERE id = " + id);
        return results.size() == 1 ? results.get(0) : null;
    }

    public List<Film> getByPartOfName(String part) {
        String searchString = "SELECT * FROM films WHERE name LIKE '%" + part +"%'";
        return executeQuery(searchString);
    }

    public void addFilm(Integer id) {
        Document document = download("https://www.kinopoisk.ru/film/" + id);
        Element tmp = document.select("h1[class$=moviename-big]").last();//.text();
        System.out.println(tmp.text());
    }
}
