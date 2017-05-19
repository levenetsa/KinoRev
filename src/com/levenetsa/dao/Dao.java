package com.levenetsa.dao;

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
        final String DB_URL = "jdbc:mysql://priv/kpra?useSSL=false";
        final String USER = "root";
        final String PASS = "lolkekcheburek";

        Connection conn = null;
        Statement stmt = null;
        List<T> result = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                try {
                    result.add(parseResult(rs));
                }catch (Exception e){
                    logger.error("Can NOT parse : " + rs.toString());
                }
            }
            rs.close();
            stmt.close();
            conn.close();
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