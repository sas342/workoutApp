package com.workoutjournal.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Exercises")
public class ExerciseList {

	@XmlElement(name="exercise")
	public List<Exercise> getExercises() {
		return exercises;
	}
	public void setExercises(List<Exercise> exercises) {
		this.exercises = exercises;
	}
	
	@XmlAttribute()
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
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
	
	
	List<Exercise> exercises;	
	int totalCount;	
	int start;		
	int size;
}
