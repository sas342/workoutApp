package com.workoutjournal.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sastimm
 */

@XmlRootElement(name="Exercise")
public class Exercise implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    private Integer exerciseId=null;    
    private Integer exNum;     
    private Exercises exercises;     
    private List<ExSet> setsList;    
    private int workoutId;
    

    public Exercise() {
    }

    public Exercise(Integer exerciseId) {
        this.exerciseId = exerciseId;
    }

    
    @XmlAttribute(name="id")
    public Integer getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Integer exerciseId) {
        this.exerciseId = exerciseId;
    }

    @XmlAttribute(name="num")
    public Integer getExNum() {
        return exNum;
    }

    public void setExNum(Integer exNum) {
        this.exNum = exNum;
    }

//    public Workout getWorkout() {
//        return workout;
//    }
//
//    public void setWorkout(Workout workout) {
//        this.workout = workout;
//    }
    
    @XmlAttribute(name="workoutId")
    public int getWorkoutId() {
    	return workoutId;
    }
    
    public void setWorkoutId(int workoutId) {
    	this.workoutId = workoutId;
    }

    @XmlElement(name="sets")
    public List<ExSet> getSetsList() {
    	if (setsList == null ) return java.util.Collections.emptyList();
        return setsList;
    }

    public void setSetsList(List<ExSet> setsList) {
        this.setsList = setsList;
    }
    
    @XmlElement(name="exerciseName")
    public Exercises getExercises() {
		return exercises;
	}

	public void setExercises(Exercises exercises) {
		this.exercises = exercises;
	}
	
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (exerciseId != null ? exerciseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Exercise)) {
            return false;
        }
        Exercise other = (Exercise) object;
        //if new exercises use name
        if (this.exerciseId == -1 && other.exerciseId == -1) {
        	return this.exercises.getName().equals(other.getExercises().getName());
        }
        if ((this.exerciseId == null && other.exerciseId != null) || (this.exerciseId != null && !this.exerciseId.equals(other.exerciseId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("workoutjournal.domain.Exercise[exerciseId=" + exerciseId + ", num="+exNum+",  ");
        if(setsList != null){
        	for(ExSet s: setsList){
        		sb.append("\t"+s.toString());
        	}
        }
        return sb.toString();
        
    }

    
}
