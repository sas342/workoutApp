module("Collection Test");
test("workout collections", function() {
	var collection = new WorkoutList();
	collection.add([createWorkout("w1", "2012-08-01", "1", "note1"),
		createWorkout("w2", "2012-08-01", "2", "note2"),
		createWorkout("w3", "2012-08-01", "3", "note3")
	]);
	
	equal(collection.length, 3, "collection size");
	equal(collection.at(1).get("name"), "w2");
});

test("search by name", function() {
	var collection = new WorkoutList();
	this.collectionStub = sinon.stub(Backbone, "sync").yieldsTo("success", {totalCount: 2, start: 0, workouts: [{name: "w1", date: "2012-08-01", time: "1", notes: "note1"}, {name: "w2", date: "2012-08-01", time: "2", notes: "note2"}]}); 
	
	collection.searchByName("name");
	
	Backbone.sync.restore();
	
	equal(collection.length, 2, "search result size");
});

test("adding to collection", function() {
	var collection = new WorkoutList();
	var addCallback = sinon.spy(collection, "add");
	var removeCallback = sinon.spy(collection, "remove");
	
	var w = createWorkout("w1", "2012-08-01", "1", "note1");
	collection.add(w);
	collection.add(createWorkout("w2", "2012-08-01", "2", "note2"));
	collection.remove(w);
	
	equal(addCallback.callCount, 2, "add called");
	equal(removeCallback.callCount, 1, "remove called");
});

function createWorkout(name, date, time, notes) {
	var workout = new Workout({name: name, date: date, time: time, notes: notes});
	return workout;
};



