$(document).ready ->
		
	$('.autocomplete:text').autocomplete
		source: ( request, response ) ->
			$.ajax
				url: $('.autocomplete:text').data('uri')+encodeURIComponent($('.autocomplete:text').val())
				dataType: "json"
				type: "GET",
				success: (data) ->
					response(data)
		,
		autoFocus: true,
		delay: 300,
		minLength: 2

