//  Bind the event handler to the "submit" JavaScript event
$('#addComputerForm').submit(function (e) {

    // Get the Login Name value and trim it
    var name = $.trim($('#name').val());
    
    if(name === '') {
    	$("#nameProblem").show();
    	e.preventDefault(e);
	}
});