package com.levenetsa.dao;

import com.levenetsa.entity.Result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResultDao implements Dao<Result>{
    @Override
    public Result parseResult(ResultSet rs) throws SQLException {
        Result film = new Result();
        film.setFilmId(rs.getInt("film_id"));
        film.setText(rs.getString("text"));
        film.setLastUpdate(rs.getTimestamp("last_update"));
        return film;
    }

    public String getById(Integer id) {
        List<Result> results = executeQuery("SELECT * FROM results WHERE film_id = " + id);
        return results.size() == 1 ? results.get(0).getText() : null;
    }
}
