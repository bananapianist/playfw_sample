$(document).ready ->
		
	uniqueId = (length=8) ->
		id = ""
		id += Math.random().toString(36).substr(2) while id.length < length
		id.substr 0, length

	$('#generate-secrectkey-button').click ->
		$('#input-secrectkey:text').val(uniqueId(64))
