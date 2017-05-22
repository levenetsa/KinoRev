package com.levenetsa.fetcher.dao;

import com.levenetsa.fetcher.entity.Film;

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
        return executeQuery("SELECT * FROM films WHERE name LIKE '%" + part +"%'");
    }
}
