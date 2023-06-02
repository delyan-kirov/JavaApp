package com.example.demo;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DataFiles {

    private static final Logger logger = LoggerFactory.getLogger(DataFiles.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DataFiles> findAll() {
        String sql = "SELECT * FROM blogposts";
        return jdbcTemplate.query(sql, new DataFilesRowMapper());
    }

    private static class DataFilesRowMapper implements RowMapper<DataFiles> {
        @Override
        public DataFiles mapRow(ResultSet rs, int rowNum) throws SQLException {
            DataFiles dataFiles = new DataFiles();
            dataFiles.setPicture(rs.getString("picture"));
            dataFiles.setSummary(rs.getString("summary"));
            dataFiles.setDate(rs.getDate("date"));
            dataFiles.setRating(rs.getString("rating"));
            dataFiles.setTitle(rs.getString("title"));
            return dataFiles;
        }
    }

    private String picture;
    private String summary;
    private Date date;
    private String rating;
    private String title;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void printAllDataFiles() {
        List<DataFiles> dataFilesList = this.findAll();
        for (DataFiles dataFile : dataFilesList) {
            logger.info("{}: {}", dataFile.getTitle(), dataFile.getSummary());
        }
    }

    public List<String> getAllTitles() {
        String sql = "SELECT title FROM blogposts";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getAllPictures() {
        String sql = "SELECT picture FROM blogposts";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getAllSummaries() {
        String sql = "SELECT summary FROM blogposts";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<Date> getAllDates() {
        String sql = "SELECT date FROM blogposts";
        return jdbcTemplate.queryForList(sql, Date.class);
    }

    public List<String> getAllRatings() {
        String sql = "SELECT rating FROM blogposts";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}