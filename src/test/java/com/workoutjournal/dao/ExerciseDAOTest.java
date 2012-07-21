package com.workoutjournal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.workoutjournal.model.Exercise;
import com.workoutjournal.model.Exercises;

@ContextConfiguration(locations={"/com/workoutjournal/testContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class ExerciseDAOTest extends AbstractTransactionalTestNGSpringContextTests {

	@Rollback(true)
	@Test	
	public void testInsert() {
		Exercise ex = new Exercise();
		ex.setWorkoutId(20);
		ex.setExNum(5);		
		Exercises e = new Exercises();
		e.setId(10);
		ex.setExercises(e);
		Exercise inserted = dao.updateExercise("sas342", ex);
		Assert.assertNotNull(inserted);
		Assert.assertTrue(inserted.getExerciseId() != null);
		
	}
	
	@Test
	public void test1() {
		Exercise ex = dao.getExercise("sas342", 49);
		Assert.assertNotNull(ex);
		Assert.assertEquals(ex.getExerciseId().intValue(), 49);
		Assert.assertNotNull(ex.getExercises());
		
	}
	
	
	
	@Autowired
	public void setExerciseDao(ExerciseJDBCDao dao) {
		this.dao = dao;
	}
	
	ExerciseJDBCDao dao;

}
