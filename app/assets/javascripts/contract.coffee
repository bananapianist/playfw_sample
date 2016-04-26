$ ->
		
	$('.autocomplete input[type="text"]').change ->
		$(this).autocomplete({ source:$(this).data('url') });


