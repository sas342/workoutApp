package com.workoutjournal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.workoutjournal.model.Exercise;
import com.workoutjournal.model.ExerciseList;
import com.workoutjournal.model.Paginator;

@ContextConfiguration(locations={"/com/workoutjournal/testContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class ExerciseServiceTest extends AbstractTransactionalTestNGSpringContextTests{

	@Test
	public void testGetExercise() {
		Exercise ex = service.getExercise("sas342", 76);
		Assert.assertNotNull(ex);
		Assert.assertNotNull(ex.getSetsList());
	}
	
	@Test
	public void testGetExercises() {
		Parameters param = new Parameters();
		param.setPaginator(new Paginator(0, 10));
		
		ExerciseList list = service.getExercises("sas342", "deadlift", param);
		Assert.assertNotNull(list);		
		Assert.assertTrue(list.getExercises().size() > 0 && list.getExercises().size() <= 10);
		for (Exercise e : list.getExercises()) {
			Assert.assertNotNull(e.getSetsList());
			Assert.assertTrue(e.getSetsList().size() > 0);
			Assert.assertNotNull(e.getExercises());
			Assert.assertNotNull(e.getExercises().getName());
		}
		
	}
	
	@Autowired
	public void setService(ExerciseService service) {
		this.service = service;
	}
	
	ExerciseService service;
}
