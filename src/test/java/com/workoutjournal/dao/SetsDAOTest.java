package com.workoutjournal.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.workoutjournal.model.ExSet;

@ContextConfiguration(locations={"/com/workoutjournal/testContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class SetsDAOTest extends AbstractTransactionalTestNGSpringContextTests {

	@Rollback(true)
	@Test	
	public void testInsert() {
		ExSet set = new ExSet();
		set.setExId(10);
		set.setNotes("NOTES");
		set.setNumber(10);
		set.setReps(20);
		set.setWeight(21);
		set.setTime(1);
		boolean inserted = dao.updateSet(set);
		Assert.assertEquals(inserted, true);
		
	}
	
	@Test
	public void test1() {
		ExSet set = dao.getSet(30);
		Assert.assertNotNull(set);
		Assert.assertEquals(set.getExId(), 9);
		Assert.assertEquals(set.getReps().intValue(), 20);
		Assert.assertEquals(set.getNumber(), 3);
		
	}
	
	@Test
	public void test3() {
		List<ExSet> sets = dao.getSets(30);
		Assert.assertNotNull(sets);
		Assert.assertTrue(sets.size() > 0);
	}
	
	
	
	@Autowired
	public void setSetsDao(SetsJDBCDao dao) {
		this.dao = dao;
	}
	
	SetsJDBCDao dao;

}
