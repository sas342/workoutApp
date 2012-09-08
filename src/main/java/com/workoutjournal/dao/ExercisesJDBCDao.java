package com.workoutjournal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.workoutjournal.model.Exercises;

public class ExercisesJDBCDao {

	public Exercises getExercises(int id) {
		String sql = "select * from exercises where id=?";
		return template.queryForObject(sql, new ExNameRowMapper(), id);
		
	}
	
	public Exercises getExercisesByName(String name) {
		String sql = "select * from exercises where name=?";
		try {
			return template.queryForObject(sql, new ExNameRowMapper(), name);
		}catch(DataAccessException dae) {
			return null;
		}
	}
	
	public List<Exercises> getExercises(String name) {
		String sql = "select * from exercises where name like ?";
		return template.query(sql, new ExNameRowMapper(), "%"+name+"%");
	}
	
	public Exercises addExerciseName(String name)  {
		Exercises current = getExercisesByName(name);
		if (current == null) {
			String sql = "insert into exercises (name) values (?)";
			template.update(sql, name);
			current = getExercisesByName(name);
		}
		return current;
	}
	
	public class ExNameRowMapper implements RowMapper<Exercises> {
		
		public Exercises mapRow(ResultSet rs, int arg1) throws SQLException {
			Exercises ex = new Exercises();
			ex.setName(rs.getString("name"));
			ex.setId(rs.getInt("id"));
			return ex;
		}
		
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		template = new SimpleJdbcTemplate(dataSource);
	}
	
	SimpleJdbcTemplate template;
}
