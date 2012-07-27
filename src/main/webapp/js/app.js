$(function($){

	var Workspace = Backbone.Router.extend({
		routes: {
			"add": "update",
			"update/:id": "update",
			"": "workouts",
			"workouts": "workouts",
			"similar/:id": "similar"

		},
		
		initialize: function(options) {
			var menu = new MenuView();
			$("#header").append(menu.render().el);
		},
		
				
		update : function(id) {
			var model;			
			if (id) {
				model = new Workout({id: id});
				model.fetch();
			} else {
				model = new Workout();
				
			}

			var form = new WorkoutForm({model: model,  el: $("#content")});
		},
	
		workouts : function() {
			var workoutCollection = new WorkoutList();
			var view = new WorkoutListView({model: workoutCollection});
			workoutCollection.fetch();
			$("#content").html(view.el);
		},
		
		similar : function(id) {
			$.ajax({
				url: "rest/workouts/similar/"+id,
				dataType: "json",
				success: function(data) {
					var workouts = new WorkoutList(data.workouts);
					var view = new WorkoutListView({model: workouts});
					$("#content").html(view.el);
					view.render();
				}
			});
		}


	});

	var Workout = Backbone.Model.extend({
		defaults: {
			"name": "",
			"time": "0",
			"date": "",
			"notes": "",
			"workoutId": "0"
		},
		
		urlRoot: "rest/workouts",
				
		initialize: function(options) {
			if (options && options.exerciseList) {
				this.exerciseList = new ExerciseList(options.exerciseList);
			} else {
				this.exerciseList = new ExerciseList();	
			}
			
		},
		
		parse: function(response) {			
			if (response.workout) {
				this.exerciseList = new ExerciseList(response.workout.exerciseList);
				return response.workout;
			}
			
			return response;
			
		},
		
		toJSON: function() {
			var object = Backbone.Model.prototype.toJSON.call(this);
			object['exerciseList'] = this.exerciseList;
			if (this.get('workoutId') == 0) {
				delete object['workoutId'];
			}
			return object;
		}
				
	});
	
	
	
	var Exercise = Backbone.Model.extend({
		defaults: {
			"exNum": ""
		},
		
		initialize: function(options) {
			if (options && options.setsList) {
				this.setsList = new Sets(options.setsList);
			} else {
				this.setsList = new Sets();	
			}
			if (options && options.exercises) {
				this.exercises = new Exercises(options.exercises);
			} else {
				this.exercises = new Exercises();
			}
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

	var WorkoutList = Backbone.Collection.extend({
		model: Workout,
		
		url: "rest/workouts.json",

		initialize: function(options) {
			this.orderBy = "Name";
			this.dir = "asc";
		},
		
		parse : function(response) {
			if (response.workouts) {
				this.totalCount = response.totalCount;
				this.start = response.start;			
				return response.workouts;
			}
			return response;
		}	
	
	});
	
	var ExerciseList = Backbone.Collection.extend({
		model: Exercise,
				
	});
	
	var Sets = Backbone.Collection.extend({
		model: Set
	});
	
	var SearchView = Backbone.View.extend({
		
		className: "searchBar",
		
		events: {
		
		},
		
		initialize: function(options) {
			_.bindAll(this, 'render', 'search');
		},
		
		render: function() {
			var template = _.template($("#SearchBar-template").html(), {});
			$(this.el).html(template);
			return this;
		},
		
		search: function() {
		
		}
		
		
	});

	var MenuView = Backbone.View.extend({
		tagName: 'div',
		
		className: "menuHeader",
		
		events: {
			"click span": "navigate"
		},
		
		initialize: function(options) {
			_.bindAll(this, 'render', 'navigate', 'clear');
		},
		
		render: function(options) {
			var template = _.template($("#Menu-template").html(), {});
			$(this.el).html(template);
			$(this.el).append(new SearchView().render().el);
			return this;
		},
		
		navigate: function(e) {
			this.clear();			
			var to = e.target.getAttribute('data-to');
			this.$('span[data-to="'+to+'"]').addClass('selected');
			app.navigate(to, {trigger: true});
		},
		
		clear: function() {
			this.$('span').removeClass('selected');
		}
	});
	
	var PaginatorView = Backbone.View.extend({
		className: "paginator",
		
		events: {
			"click .first": "first",
			"click .prev": "prev",
			"click .next": "next",
			"click .last": "last"
		},
		
		initialize: function(options) {
			_.bindAll(this, 'first', 'prev', 'next', 'last');
		},
		
		render: function() {
			var template = _.template($("#pagination-template").html(), {setobj: this.model});
			$(this.el).html(template);
			return this;
		},
		
		first: function() {			
			this.model.fetch({data: {"offset":"0", "sortField": this.model.orderBy, "sortDir": this.model.dir}});
		},
		
		prev: function() {
			var size = this.model.models.length;
			var offset = this.model.start - size;
			this.model.fetch({data: {"offset": offset, "sortField": this.model.orderBy, "sortDir": this.model.dir}});
		},
		
		next: function() {
			var size = this.model.models.length;
			var offset = this.model.start + size;
			this.model.fetch({data: {"offset": offset, "sortField": this.model.orderBy, "sortDir": this.model.dir}});
		},
		
		last: function() {
			var total = this.model.totalCount;
			var size = this.model.models.length;
			var offset = total - size;
			
			this.model.fetch({data: {"offset": offset, "sortField": this.model.orderBy, "sortDir": this.model.dir}});
		}
		
	});
	
	var WorkoutItemView = Backbone.View.extend({
		tagName: 'tr',
		className: 'workoutRow',
		
		events: {
			"click .edit": "edit",
			"click .similar": "similar",
			"click ": "showExDetails"
		},

		initialize: function(options) {
			this.attributes = {'data-id': this.model.get('workoutId')}
			$(this.el).attr(this.attributes);
		},
		
		render: function() {
			var template = _.template( $("#workoutItem-template").html(), {setobj: this.model});
			$(this.el).html(template);
			return this;
		},
		
		edit: function(e) {
			var id = this.attributes['data-id'];
			app.navigate("update/"+id, {trigger: true});
		},
		
		similar: function(e) {
			var id = this.attributes['data-id'];
			app.navigate("similar/"+id, {trigger: true});
		},
		
		showExDetails: function(e) {
			$('#exDetails').show();
			$('#exercises').empty();
			_.each(this.model.exerciseList.models, function(ex) {	
				$('#exercises').append(new ExDetailsView({model: ex}).render().el);
			}, this);
			
		}
	});
	
	var ExDetailsView = Backbone.View.extend({
		
		initialize: function(options) {
			_.bindAll(this, 'render');			
		},
		
		render: function() {
			var template = _.template($("#exDetails-template").html(), {setobj: this.model});
			$(this.el).html(template);
			return this;
		}
	});

	var WorkoutListView = Backbone.View.extend({
		events: {
			"click .headerRow .sortable" : "sort"			
		},
		
		className: 'workoutsPage',
		
		initialize : function(options) {
			_.bindAll(this, 'render');	
			this.model.bind("reset", this.render, this);			
		},

		render: function() {
			var template = _.template($("#workoutCollection-template").html(), {});
			$(this.el).html(template);
			_.each(this.model.models, function(w) {				
				this.$('tbody').append(new WorkoutItemView({model: w}).render().el);
			}, this);
			this.$('table').append(new PaginatorView({model: this.model}).render().el);
			this.$('#exDetails').hide();
			return this;
		},
		
		sort: function(e) {
			var target = e.target;
			
			var column = target.childNodes[0].nodeValue;
			var dir;
			if (this.model.dir == "asc") {
				dir = "desc";
			} else {
				dir = "asc";
			}
			
			this.model.dir = dir;
			this.model.orderBy = column;
			this.model.fetch({data: {"sortField": column, "sortDir": dir}});
		}

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
			var template = _.template( $("#ex-template").html(), {setobj: this.model});
			$(this.el).html(template);
					
			this.createSelects();
			var numSets = this.model.setsList.models.length;
			this.$("select").val(numSets);
			
			_.each(this.model.setsList.models, function(set) {
				this.addNewRow(set);
			}, this);
			
			if (numSets == 0) {
				this.addSet();			
			}
			this.createNamesList();
			return this;
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
			this.model.exerciseList.bind('add', this.addExerciseForm);
			this.model.exerciseList.bind('remove', this.removeExerciseForm);
			this.model.bind("change", this.render, this);	
			this.setup();
			
		},

		setup: function() {
			this.render();
			
			if (this.model.get('workoutId') == 0) {
				this.addExercise();
			}

		},
		
		render: function() {
			var template = _.template( $("#form-template").html(), {setobj: this.model});
			$(this.el).html(template);
			
			this.createSelects();
						
			var numExercises = this.model.exerciseList.models.length;
			
			$("#numEx").val(numExercises);
			
			//exercises
			_.each(this.model.exerciseList.models, function(ex) {				
				this.$(".exDetails").append(new ExerciseForm({model: ex}).render().el);
			}, this);
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
			this.model.exerciseList.add({exNum: this.model.exerciseList.length+1});
		},
		
		removeExercise : function() {
			this.model.exerciseList.pop();
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
			var that = this;
			$.ajax({
				type: 'POST',
				contentType: 'application/json',
				url: "rest/workouts.json",
				data: JSON.stringify(this.model),
				success: function(data) {
					that.clear();
				},
				dataType: "json"});
					
		},

		clear : function() {
			this.model.clear();
			this.setup();
		}
		
	});

	
	
	var app = new Workspace();
	Backbone.history.start();
});
