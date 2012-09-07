module("Model Test");
test("default workout value", function() {
	var workout = new Workout();
	equal(workout.get("name"), "", "default name");
	equal(workout.get("time"), "0", "default time");
	equal(workout.get("date"), "", "default date");
	equal(workout.get("notes"),"", "default notes");

});

test("create workout", function() {
	var workout = new Workout();
	workout.set({name: "testWorkout", time: 10, date: "2012-08-20", notes: "testNotes"});
	equal(workout.get("name"), "testWorkout", "workout name");
	equal(workout.get("time"), 10, "workout time");
	equal(workout.get("date"), "2012-08-20", "workout date");
	equal(workout.get("notes"), "testNotes", "workout notes");
});

test("create exercise", function() {
	var exercise = new Exercise();
	equal(exercise.get("exNum"), "", "ex num");
	
	var exercises = new Exercises();
	exercises.set({name: "myExercise"});
	exercise.set({exercises: exercises});
	
	equal(exercise.get("exercises").get("name"), "myExercise", "ex name");

});

test("create set", function() {
	var set = new Set();
	equal(set.get("reps"), "0", "sets");
	equal(set.get("weight"), "0", "weight");
	equal(set.get("notes"), "", "notes");
	equal(set.get("number"), 1, "number");
});