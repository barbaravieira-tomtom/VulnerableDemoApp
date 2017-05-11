$(document).ready(
		function() {

			var url = window.location;

			// SUBMIT FORM
			$("#xssForm").submit(function(event) {
				// Prevent the form from submitting via the browser.
				event.preventDefault();
				ajaxPost();
			});

			function ajaxPost() {
				// DO POST
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "/postxss",
					data : JSON.stringify($("#xssInput").val()),
					dataType : 'json',
					success : function(result) {
						if (result.status == "Done") {
							$("#getResultDiv2").html(
									"<strong>" + "Post Successfully!" + $.parseJSON(result.data) + "</strong>");
						} else {
							$("#getResultDiv2").html("<strong>" + $.parseJSON(result.data) + "</strong>");
						}
					},
					error : function(e) {
						alert("Error! " + JSON.stringify($("#xssInput").val()))
						console.log("ERROR: ", e);
					}
				});

				// Reset FormData after Posting
				resetData();
			}

			function resetData() {
				$("#xssInput").val("");
			}
		});

$(document).ready(function() {
	var url = window.location;
	// GET REQUEST
	$("#getBtnLoad").click(function(event) {
		event.preventDefault();
		ajaxGet();
	});

	// DO GET
	function ajaxGet() {
		$.ajax({
			type : "GET",
			url : "/getxss",
			success : function(result) {
				$("#getResultDivLoad").html(result.data); //$.parseJSON(result.data));
				console.log("Success: ", result.data);
			},
			error : function(e) {
				$("#getResultDivLoad").html("<h4>Application error</h4>");
				console.log("ERROR: ", e);
			}
		});
	}
});

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
};

$.ajaxPrefilter(function(options, originalOptions, jqXHR) {
	var token;
	var csrf_token = getCookie('CSRF-TOKEN');
	if (csrf_token) {
		return jqXHR.setRequestHeader('X-CSRF-Token', csrf_token);
	}
});