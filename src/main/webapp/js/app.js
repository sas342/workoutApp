$(function($){

	/** Module to handle loading views into main content panel.  Will close previous view
	  http://lostechies.com/derickbailey/2011/09/15/zombies-run-managing-page-transitions-in-backbone-apps/ and
	  http://stackoverflow.com/questions/7567404/backbone-js-repopulate-or-recreate-the-view/7607853#7607853
	 */
	function AppView() {
		this.currentObj;
		
		return {
			show : function(view) {
				if (this.currentObj) {
					this.currentObj.dispose();
				}
				this.currentObj = view;
				
				$("#content").html(this.currentObj.el);
			}
		}
	};
	
	var Workspace = Backbone.Router.extend({
		routes: {
			"add": "update",
			"update/:id": "update",
			"": "workouts",
			"workouts": "workouts",
			"workouts/:name": "workouts",
			"similar/:id": "similar"

		},
		
		initialize: function(options) {
			this.menu = new MenuView();
			$("#header").append(this.menu.render().el);
			this.AppView = new AppView();
						
		},
		
		updateMenu: function(page) {
			this.menu.updateMenuIcon(page);
		},
				
		update : function(id) {
			this.updateMenu("add");
			var model;			
			if (id) {
				model = new Workout({id: id});
				model.fetch();
			} else {
				model = new Workout();				
			}
			var form = new WorkoutForm({model: model});			
			this.AppView.show(form);
			form.render();
		},
	
		workouts : function(name) {
			this.updateMenu("workouts");
			var workoutCollection = new WorkoutList();
			var view = new WorkoutListView({model: workoutCollection});
			if (name) {
				$('#header .searchBar input').val(name);
				workoutCollection.searchByName(name);
			} else {
				workoutCollection.fetch();
			}
			this.AppView.show(view);
		},
		
		similar : function(id) {
			var that = this;
			$.ajax({
				url: "rest/workouts/similar/"+id,
				dataType: "json",
				success: function(data) {
					var workouts = new WorkoutList(data.workouts);
					var view = new WorkoutListView({model: workouts});
					that.AppView.show(view);					
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
		},

		clear: function() {
			Backbone.Model.prototype.clear.call(this);
			this.exerciseList = new ExerciseList();
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
		
		url: function() {
			if(this.name) {
				return "rest/workouts/search/"+this.name+".json";
			} else {
				return "rest/workouts.json";
			}
		},

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
		},

		searchByName : function(name) {
			this.name = name;
			this.fetch();
		}
	
	});
	
	var ExerciseList = Backbone.Collection.extend({
		model: Exercise,
				
	});
	
	var Sets = Backbone.Collection.extend({
		model: Set
	});
	
	/** Base view all views should extend */
	var BaseView = function(options) {
		this.bindings = [];
		Backbone.View.apply(this, [options]);
	};
	
	/** have BaseView extend Backbone.View */	
	_.extend(BaseView.prototype, Backbone.View.prototype, {
		
		bindTo: function(model, ev, callback) {
			model.bind(ev, callback, this);
			this.bindings.push({model: model, ev: ev, callback: callback});
		},
		
		unbindFromAll: function() {
			_.each(this.bindings, function (binding) {
				binding.model.unbind(binding.ev, binding.callback);
			});
			this.bindings = [];
		},
		
		dispose: function() {
			this.unbindFromAll();
			this.unbind();
			this.remove();
		}
	
	});
	
	BaseView.extend = Backbone.View.extend;
	
	var SearchView = BaseView.extend({
		
		className: "searchBar",
		
		events: {
			"keypress input": "keypressed",
			"click .close": "close",
			"click input": "lowerText"
		},
		
		initialize: function(options) {
			_.bindAll(this, 'render', 'search', 'close');
		},
		
		render: function() {
			var template = _.template($("#SearchBar-template").html(), {});
			$(this.el).html(template);
			return this;
		},
		
		keypressed: function(e) {
			if (!this.clearedSearchText) {
				this.$('input').val('');
				this.clearedSearchText = true;
				this.$('input').removeClass("lightGray");
			}
			
			if (e.keyCode == 13) {
				this.search(e.target.value);
			}
			
		},
		
				
		lowerText: function() {
			if (!this.clearedSearchText) {
				this.$('input').removeClass("gray");
				this.$('input').addClass("lightGray");
			}
		},
		
		search: function(name) {
			app.navigate("workouts/"+name, {trigger: true});
		},
		
		close: function(name) {
			this.$('input').val('search for workouts');
			this.clearedSearchText = false;
			app.navigate("workouts", {trigger: true});
		}
		
		
	});

	var MenuView = BaseView.extend({
		tagName: 'div',
		
		className: "menuHeader",
		
		events: {
			"click span": "navigate"
		},
		
		initialize: function(options) {
			_.bindAll(this, 'render', 'navigate', 'clear', 'updateMenuIcon');
		},
		
		render: function(options) {
			var template = _.template($("#Menu-template").html(), {});
			$(this.el).html(template);
			$(this.el).append(new SearchView().render().el);
			return this;
		},
		
		navigate: function(e) {
			var to = e.target.getAttribute('data-to');
			this.updateMenuIcon(to);
			app.navigate(to, {trigger: true});
		},
		
		updateMenuIcon: function(to) {
			this.clear();			
			this.$('span[data-to="'+to+'"]').addClass('selected');
		},
		
		clear: function() {
			this.$('span').removeClass('selected');
		}
	});
	
	var PaginatorView = BaseView.extend({
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
	
	var WorkoutItemView = BaseView.extend({		
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
			e.stopImmediatePropagation();
			
			var id = this.attributes['data-id'];			
			app.navigate("similar/"+id, {trigger: true});			
		},
		
		showExDetails: function(e) {	
			this.$('.exercises > *').remove();			
			_.each(this.model.exerciseList.models, function(ex) {	
				this.$('.exercises').append(new ExDetailsView({model: ex}).render().el);
			}, this);
			
			//this.$('.exercises').slideDown();
			/**
			$('#exDetails').show();
			$('#exercises').empty();
			_.each(this.model.exerciseList.models, function(ex) {	
				$('#exercises').append(new ExDetailsView({model: ex}).render().el);
			}, this);
			*/
		}
	});
	
	var ExDetailsView = BaseView.extend({
		
		initialize: function(options) {
			_.bindAll(this, 'render');			
		},
		
		render: function() {
			var template = _.template($("#exDetails-template").html(), {setobj: this.model});
			$(this.el).html(template);
			return this;
		}
	});

	var WorkoutListView = BaseView.extend({
		events: {
			"click .headerRow .sortable" : "sort",
			"click .workoutRow" : "updateSelection"
		},
		
		className: 'workoutsPage',
		
		initialize : function(options) {
			_.bindAll(this, 'render');	
			this.bindTo(this.model, 'reset', this.render);						
		},

		render: function() {
			var template = _.template($("#workoutCollection-template").html(), {});
			$(this.el).html(template);
			_.each(this.model.models, function(w) {				
				this.$('.wtable').append(new WorkoutItemView({model: w}).render().el);
			}, this);
			this.$('.wtable').append(new PaginatorView({model: this.model}).render().el);
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
		},
		
		updateSelection: function(e) {
			this.$('.workoutRow').removeClass("selected");
			this.$(e.target.parentNode).addClass("selected");
			
			this.$('.workoutRow .exercises').slideUp();			
			this.$(e.target.parentNode).children('.exercises').slideDown();
		}

	});

	/**
	*  Form to edit a workout set 
	*/
	
	var SetsForm = BaseView.extend({
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
	var ExerciseForm = BaseView.extend({
		events : {
			"change select": "updateNumberOfSetRows",
			"change input": "updateExName"
		},
		
		initialize: function(options) {
			_.bindAll(this, 'render', 'addNewRow', 'removeRow', 'addSet', 'removeSet', 'createSelects','updateNumberOfSetRows', 'createNamesList', 'updateExName');
			this.bindTo(this.model.setsList, 'add', this.addNewRow);
			this.bindTo(this.model.setsList, 'remove', this.removeRow);
			
			//this.model.setsList.bind('add', this.addNewRow);
			//this.model.setsList.bind('remove', this.removeRow);
			
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
					
					that.$(".exName").autocomplete({source: names, change: function(event, ui){that.model.exercises.set(event.target.name, event.target.value);}});
					
				}
			});
		
		},
		
		updateExName: function(e) {
			e.stopImmediatePropagation();
					
			var target = e.target;
			//alert(target.name+' ' + target.value);
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
	var WorkoutForm = BaseView.extend({
		
		events: {
			"change #numEx": "updateNumberOfExercises",
			"click #submitbtn": "submitWorkout",
			"change input": "updateWorkoutValues",
			"focus input": "outline"
			
		},
		
		initialize: function(options) {		
			_.bindAll(this, 'render', 'addExercise', 'removeExercise', 'addExerciseForm', 'removeExerciseForm', 'createSelects', 'updateNumberOfExercises', 'updateWorkoutValues', 'outline'); 
			this.bindTo(this.model.exerciseList, 'add', this.addExerciseForm);
			this.bindTo(this.model.exerciseList, 'remove', this.removeExerciseForm);
			this.bindTo(this.model, 'change', this.render);
			
			//this.model.exerciseList.bind('add', this.addExerciseForm);
			//this.model.exerciseList.bind('remove', this.removeExerciseForm);
			//this.model.bind("change", this.render, this);	
			
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
			
			if (this.model.get('workoutId') == 0) {
				this.addExercise();
			}
			
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
			this.$(".exDetails > div:last-child").remove();
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
			console.log(currentCnt+" "+newCnt);			
			if (currentCnt < newCnt) {
				_.each(_.range(newCnt - currentCnt), this.addExercise);
			} 
			else if (currentCnt > newCnt) {
				_.each(_.range(currentCnt - newCnt), this.removeExercise);				
			}
			
		},
		
		updateWorkoutValues: function(e) {
	
			var target = e.target;
			var name = target.name;
			var value = target.value;
			this.model.set(target.name, target.value, {silent: true});
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
			this.render();
		},
		
		outline : function(e) {
			this.$('input').removeClass('outline');
			this.$(e.target).addClass('outline');
		}
		
	});
	
	
	var app = new Workspace();
	Backbone.history.start();
});
