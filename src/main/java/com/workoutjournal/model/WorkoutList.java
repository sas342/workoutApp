package com.workoutjournal.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="workouts")
public class WorkoutList {

	@XmlAttribute()
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	@XmlElement(name="workout")
	public List<Workout> getWorkouts() {
		return workouts;
	}
	public void setWorkouts(List<Workout> workouts) {
		this.workouts = workouts;
	}
	
	@XmlAttribute()
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	@XmlAttribute()
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	
	int totalCount;	
	int start;	
	int size;	
	List<Workout> workouts;
	
	
}
