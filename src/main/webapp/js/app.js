$(function($){

	var Workout = Backbone.Model.extend({
		defaults: {
			"name": "",
			"time": "0",
			"date": "",
			"notes": ""
		},
				
		initialize: function() {
			this.exList = new ExerciseList();
		},
		
		toJSON: function() {
			var object = Backbone.Model.prototype.toJSON.call(this);
			object['exerciseList'] = this.exList;
			return object;
		}
				
	});
	
	
	
	var Exercise = Backbone.Model.extend({
		defaults: {
			"exNum": ""
		},
		
		initialize: function() {
			this.setsList = new Sets();	
			this.exercises = new Exercises();
		},

		toJSON: function() {
			var object = Backbone.Model.prototype.toJSON.call(this);
			object['setsList'] = this.setsList;
			object['exercises'] = this.exercises;
			return object;
		}
		
	});
	
	var Exercises = Backbone.Model.extend({
		defaults: {
			name: ""
		}
	});
	
	var Set = Backbone.Model.extend({
		defaults: {
			"reps": 0,
			"weight": 0,
			"notes": "",
			"number": 1
		}
	});
	
	var ExerciseList = Backbone.Collection.extend({
		model: Exercise
	});
	
	var Sets = Backbone.Collection.extend({
		model: Set
	});
	
	
	/**
	*  Form to edit a workout set 
	*/
	
	var SetsForm = Backbone.View.extend({
		tagName: 'tr',
		
		events: {
			"change input": "valuesUpdated"
		},
		
		initialize: function(options) {
			_.bindAll(this, 'render', 'valuesUpdated');			
		},
		
		render: function() {
			var template = _.template( $("#set-template").html(), {setobj: this.model});
			$(this.el).html(template);
			return this;
		},
		
		valuesUpdated: function(e) {
			e.stopImmediatePropagation();
			
			var target = e.target;
			this.model.set(target.name, target.value);
		}
	});
	
	/**
	* Form to edit workout exercise
	*/
	var ExerciseForm = Backbone.View.extend({
		events : {
			"change select": "updateNumberOfSetRows",
			"change input": "updateExName"
		},
		
		initialize: function(options) {
			_.bindAll(this, 'render', 'addNewRow', 'removeRow', 'addSet', 'removeSet', 'createSelects','updateNumberOfSetRows', 'createNamesList', 'updateExName');
			this.model.setsList.bind('add', this.addNewRow);
			this.model.setsList.bind('remove', this.removeRow);
			
		},
		
		render : function() {
			var template = _.template( $("#ex-template").html(), {});
			$(this.el).html(template);
			
			this.addSet();			
			this.createNamesList();
			return this.createSelects(); //.createNamesList();
		},
		
		addNewRow: function (s) {
			var setrow = new SetsForm({model: s});
			
			this.$('tbody').append(setrow.render().el);
			
		},
		
		removeRow: function() {
			this.$('tbody tr:last-child').remove();
		},
		
		addSet : function() {			
			this.model.setsList.add({reps: 0, weight: 0, notes: "", number: this.model.setsList.length+1});
		},
		
		removeSet : function() {
			this.model.setsList.pop();
		},
		
		createNamesList : function() {
			var that = this;
			$.ajax({
				url: "rest/names/.json",
				dataType: "json",
				success: function(data) {
					var names = new Array();
					var i = 0;
					_.each(data.exerciseNames, function(ex) {
						names[i++] = ex.name;
					});
					
					that.$(".exName").autocomplete({source: names});
					
				}
			});
		
		},
		
		updateExName: function(e) {
			e.stopImmediatePropagation();
					
			var target = e.target;
			this.model.exercises.set(target.name, target.value);
		},
		
		updateNumberOfSetRows : function(event) {
			var currentCnt = this.$("table tbody").children().length - 1; //for header
			var newCnt = event.currentTarget.value;
			
			if (currentCnt < newCnt) {
				_.each(_.range(newCnt - currentCnt), this.addSet);
			} 
			else if (currentCnt > newCnt) {
				_.each(_.range(currentCnt - newCnt), this.removeSet);				
			}
		},
		
		createSelects : function() {	
			_.each(_.range(1, 11), function(i) {			
				this.$("select").append("<option value=\""+i+"\">"+i+"</option>");
				}, this
			);		
			
			return this;
		}
	});
	
	
	//Form to edit workout
	var WorkoutForm = Backbone.View.extend({
		
		events: {
			"change #numEx": "updateNumberOfExercises",
			"click #submitbtn": "submitWorkout",
			"change input": "updateWorkoutValues"
			
		},
		
		initialize: function(options) {		
			_.bindAll(this, 'render', 'addExercise', 'removeExercise', 'addExerciseForm', 'removeExerciseForm', 'createSelects', 'updateNumberOfExercises', 'updateWorkoutValues'); 
			this.model.exList.bind('add', this.addExerciseForm);
			this.model.exList.bind('remove', this.removeExerciseForm);
			this.render().createSelects();	
			
			this.addExercise();
			
		},
		
		render: function() {
			
			var template = _.template( $("#form-template").html(), {});
			$(this.el).html(template);
			
			$("#datepicker").datepicker({ dateFormat: "yy-mm-dd" });
			
			return this;
		},
		
		createSelects : function() {
			_.each(_.range(1, 11), function(i) {			
				$("#numEx").append("<option value=\""+i+"\">"+i+"</option>");
				}
			);			
			
			return this;
		},
		
				
		addExerciseForm : function(ex) {			
			var exForm = new ExerciseForm({model: ex});
			this.$(".exDetails").append(exForm.render().el);
		},
		
		removeExerciseForm : function() {
			this.$(".exercise:last-child").remove();
		},
		
		addExercise : function() {
			
			this.model.exList.add({exNum: this.model.exList.length+1});
		},
		
		removeExercise : function() {
			this.model.exList.pop();
		},
		
		updateNumberOfExercises : function(event) {
			var currentCnt = this.$(".exerciseForm").length; 
			var newCnt = event.currentTarget.value;
						
			if (currentCnt < newCnt) {
				_.each(_.range(newCnt - currentCnt), this.addExercise);
			} 
			else if (currentCnt > newCnt) {
				_.each(_.range(currentCnt - newCnt), this.removeExercise);				
			}
			
		},
		
		updateWorkoutValues: function(e) {
			var target = e.target;
			this.model.set(target.name, target.value);
		},
		
		submitWorkout : function() {
			alert(JSON.stringify(this.model));
			$.ajax({
				type: 'POST',
				contentType: 'application/json',
				url: "rest/workouts.json",
				data: JSON.stringify(this.model),
				success: function(data) {
					alert("workout saved");
				},
				dataType: "json"});
			
		}
		
	});

	
	
	//var model = new Exercise();
	var model = new Workout();
	var form = new WorkoutForm({model: model,  el: $("#workoutForm")});

});