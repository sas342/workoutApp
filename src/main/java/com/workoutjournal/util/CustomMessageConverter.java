package com.workoutjournal.util;


import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

/**
 * This class updates the object mapper's deserialization date format to EST instead of GMT.
 * 
 * @author sas342
 *
 */
public class CustomMessageConverter extends MappingJacksonHttpMessageConverter{

		
	@PostConstruct()
	public void afterPropertiesSet() {
		this.setObjectMapper(new CustomObjectMapper());
	}
	
	/**
	 * custom object mapper
	 * @author sas342
	 *
	 */
	public class CustomObjectMapper extends ObjectMapper {

		public CustomObjectMapper() {
			super();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			df.setTimeZone(TimeZone.getTimeZone("EST"));
			this.getDeserializationConfig().setDateFormat(df);
		}
		
		
		
	}
}
