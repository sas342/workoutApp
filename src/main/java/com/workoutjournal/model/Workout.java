package com.workoutjournal.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sastimm
 */
@XmlRootElement(name="workout")
public class Workout implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    private Integer workoutId=null;
    
    
    private String name;
  
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date date;
    
    
    private List<Exercise> exerciseList;

    
    private String time;
    
    
    private String notes;

    public Workout() {
    }

    public Workout(Integer workoutId) {
        this.workoutId = workoutId;
    }

    public Workout(Integer workoutId, String name) {
        this.workoutId = workoutId;
        this.name = name;
    }

    @XmlAttribute(name="id")
    public Integer getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Integer workoutId) {
        this.workoutId = workoutId;
    }

    @XmlAttribute(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement(name="exercise")
    public List<Exercise> getExerciseList() {
    	if(exerciseList == null) return new java.util.ArrayList<Exercise>();
        return exerciseList;
    }

    public void setExerciseList(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

//    public Users getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Users userId) {
//        this.userId = userId;
//    }
    
    public void setTime(String time){
    	this.time = time;
    }
    
    @XmlAttribute(name="time")
    public String getTime(){
    	return time;
    }
    
    public void setNotes(String notes){
    	this.notes = notes;
    }
    
    @XmlElement(name="notes")
    public String getNotes(){
    	return notes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workoutId != null ? workoutId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Workout)) {
            return false;
        }
        Workout other = (Workout) object;
        if ((this.workoutId == null && other.workoutId != null) || (this.workoutId != null && !this.workoutId.equals(other.workoutId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("workoutjournal.domain.Workout[workoutId=" + workoutId+", name="+name+", date="+date+",  notes="+notes);
        if(exerciseList != null){
        	for(Exercise e: exerciseList){
        		sb.append("\t"+e.toString()+"\n");
        	}
        }
        sb.append("]");
        
        return sb.toString();
    }

    
    
    

}
