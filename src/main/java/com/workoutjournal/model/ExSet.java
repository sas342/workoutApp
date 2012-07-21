package com.workoutjournal.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author sastimm
 */

public class ExSet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id=null;
   
    private Integer reps;
   
    private Integer weight;
   
    private int number;
  
    private String notes;
    private Integer time;
    
    private int exId;

    public ExSet() {
    }

    public ExSet(Integer id) {
        this.id = id;
    }

    public ExSet(Integer id, int number) {
        this.id = id;
        this.number = number;
    }

    @XmlAttribute()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlAttribute
    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    @XmlAttribute
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @XmlAttribute
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @XmlElement
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @XmlAttribute
    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    @XmlAttribute(name="exerciseId")
    public int getExId() {
        return exId;
    }

    public void setExId(int exId) {
        this.exId = exId;
    }
    
//    public Exercise getEx() {
//    	return ex;
//    }
//    
//    public void setEx(Exercise ex) {
//    	this.ex = ex;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExSet)) {
            return false;
        }
        ExSet other = (ExSet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "workoutjournal.domain.Sets[id=" + id + ", number="+number+", reps="+reps+", weight="+weight+", time="+time+"]";
    }

    @Override
    public ExSet clone(){
        ExSet s = new ExSet();
        s.setNotes(new String(this.notes));
        s.setNumber(new Integer(this.number));
        s.setReps(new Integer(this.reps));
        s.setTime(new Integer(this.time));
        s.setWeight(new Integer(this.weight));

        return s;
    }
}

