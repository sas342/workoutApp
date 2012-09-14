load("resources/qunit.js");

QUnit.init();
QUnit.config.blocking = false;
QUnit.config.autorun = true;
QUnit.config.updateRate = 0;
QUnit.log = function(result, message) {
    if(result == false) {
        print("FAILED: " + message);
        java.lang.System.exit(0);
    }else {
       print("PASS: " + message) ;
   }

};

load("modelTest.js");
load("collectionTest.js");
load("viewTest.js");