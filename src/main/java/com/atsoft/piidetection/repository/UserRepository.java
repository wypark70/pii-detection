package com.atsoft.piidetection.repository;

import com.atsoft.piidetection.model.User;
import com.atsoft.piidetection.util.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String name, String email) throws Exception {
        String sql = SqlLoader.loadSql("sql/insert-user.sql");
        jdbcTemplate.update(sql, name, email);
    }

    public List<User> findAll() throws Exception {
        String sql = SqlLoader.loadSql("sql/select-users.sql");
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("name"), rs.getString("email")));
    }
}