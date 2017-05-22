package com.levenetsa.fetcher.dao;

//STEP 1. Import required packages

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface Dao<T> {

    T parseResult(ResultSet rs) throws SQLException;

    default List<T> executeQuery(String sql) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/kpra";
        final String USER = "root";
        final String PASS = "dummyAss";
        Connection conn = null;
        Statement stmt = null;
        List<T> result = new ArrayList<>();
        try {
            logger.info("In connector: ");
            Class.forName(JDBC_DRIVER);
            logger.info("    Trying to get connection");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("    Connected. Fetching for:");
            logger.info("                  " + sql);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                try {
                    result.add(parseResult(rs));
                }catch (Exception e){
                    logger.error("Can NOT parse : " + rs.toString());
                }
            }
            if (result.size() == 0) {
                logger.info("    Empty answer");
            }
            rs.close();
            stmt.close();
            conn.close();
            logger.info("    Connection closed");
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
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
}
