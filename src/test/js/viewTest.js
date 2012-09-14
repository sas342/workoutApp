module("View Test");
test("search view", function() {
	var view = new SearchView();
	
	var searchSpy = sinon.spy();
	app.on("route:workouts", searchSpy);
	
	view.search("text");
	ok(searchSpy.called, "search performed");
	
});

test("pagination view", function() {
	
	var collection = new WorkoutList();
	collection.add([createWorkout("w1", "2012-08-01", "1", "note1"),
		createWorkout("w2", "2012-08-01", "2", "note2"),
		createWorkout("w3", "2012-08-01", "3", "note3")
	]);
	collection.totalCount = 10;
	collection.start = 0;
	
	var pag = new PaginatorView({model: collection});
	pag.render();
	ok(pag.el != null, "pagination rendered");
});

