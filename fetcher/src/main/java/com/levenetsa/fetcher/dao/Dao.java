package com.levenetsa.fetcher.dao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface Dao<T> {

    T parseResult(ResultSet rs) throws SQLException;

    default List<T> executeQuery(String sql) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/kpra?useUnicode=yes&characterEncoding=cp866";
        final String USER = "root";
        final String PASS = "new-password";
        Connection conn = null;
        Statement stmt = null;
        List<T> result = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("Fetching for: " + sql.substring(0, sql.length() > 60 ? 60 : sql.length()));
            stmt = conn.createStatement();
            if (sql.contains("SELECT")) {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    try {
                        result.add(parseResult(rs));
                    } catch (Exception e) {
                        logger.error("Can NOT parse : " + rs.toString());
                    }
                }
                rs.close();
            } else {
                stmt.execute(sql);
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
                logger.error("Cannot close SQL connection!");
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return result;
    }

    default Document download(String s) {
        Document result = null;
        try {
            System.setProperty("http.proxyHost", "proxy.t-systems.ru"); // set proxy server
            System.setProperty("http.proxyPort", "3128");
            result = Jsoup.connect(s).timeout(10_000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
