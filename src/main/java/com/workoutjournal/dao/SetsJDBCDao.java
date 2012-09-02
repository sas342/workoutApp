package com.workoutjournal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.workoutjournal.model.ExSet;


public class SetsJDBCDao {
	
	public ExSet getSet(int setId) {
		String sql = "select * from sets where id=?";
		return template.queryForObject(sql, new SetsRowMapper(), setId);
	}
	
	public List<ExSet> getSets(int exId) {
		String sql = "select * from sets where ex_id=?";
		return template.query(sql, new SetsRowMapper(), exId);
	}
	
	public boolean updateSet(ExSet set) {
		String sql = null;
		int affected = 0;
		if (set.getId() != null) {
			sql = "update sets set reps=?, weight=?, notes=?, time=?, number=? where id=?";
			affected = template.update(sql, set.getReps(), set.getWeight(), set.getNotes(), set.getTime(), set.getNumber(), set.getId());
		}
		else {
			sql = "insert into sets (reps, weight, notes, time, number, ex_id) values (?,?,?,?,?,?)";
			affected = template.update(sql, set.getReps(), set.getWeight(), set.getNotes(), set.getTime(), set.getNumber(), set.getExId());
		}
		
		return affected > 0;
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		template = new SimpleJdbcTemplate(dataSource);
	}
	
	public class SetsRowMapper implements RowMapper<ExSet> {
		
		public ExSet mapRow(ResultSet rs, int arg1) throws SQLException {
			ExSet set = new ExSet();
			set.setExId(rs.getInt("ex_id"));
			set.setId(rs.getInt("id"));
			set.setNotes(rs.getString("notes"));
			set.setNumber(rs.getInt("number"));
			set.setReps(rs.getInt("reps"));
			set.setTime(rs.getInt("time"));
			set.setWeight(rs.getInt("weight"));
			return set;
		}
		
	}
	
	private SimpleJdbcTemplate template;
}
