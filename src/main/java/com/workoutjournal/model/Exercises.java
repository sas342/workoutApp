package com.workoutjournal.model;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ExerciseName")
public class Exercises implements Serializable{
	private static final long serialVersionUID = 1L;
	
    private Integer id=null;
   
    private String name;
    
    public Exercises() {
    	
    }
    public Exercises(String name) {
    	this.name = name;
    }
    
    @XmlAttribute
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    
}
