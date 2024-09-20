var OMRS_WEBSERVICES_BASE_URL = '../..';

getReportsAndPopulateToEachServer();

function addRows() {
	var table = document.getElementById('table');
	var rowCount = table.rows.length;
	var cellCount = table.rows[0].cells.length;
	var row = table.insertRow(rowCount);
	for (var i = 0; i < cellCount; i++) {
		var cell = 'cell' + i;
		cell = row.insertCell(i);
		var copycel = document.getElementById('col' + i).innerHTML;
		cell.innerHTML = copycel;
	}
}

function deleteRows() {
	var table = document.getElementById('table');
	var rowCount = table.rows.length;
	if (rowCount > '2') {
		var row = table.deleteRow(rowCount - 1);
		rowCount--;
	}
	else {
		alert('Deve existir pelo menos uma configuração.');
	}
}


function removeConfiguration(event) {

	let objectOfThisRow = {};
	var tr = $j(event).closest('tr');
	var tds = $j(tr).find("td").get();
	let url = $j(tds[0]).text();
	let user = $j(tds[1]).text();

	let location = "";

	$j(tds[2]).find('input').attr('name', function() {
		location = $j(this).val();
	});

	objectOfThisRow = {
		url,
		user,
		location
	}

	if (confirm("Tem certeza que deseja remover essa configuracao?")) {
		deleteServerConfig(objectOfThisRow);
	}
}

function saveListOfConfiguredServers(event) {

	var dhisserver = {};
	dhisserver.configurations = [];
	$j('#table tbody tr').each(function(i) {
		var arrayOfThisRow = [];
		$j(this).find('input,select').attr('name', function() {

			arrayOfThisRow.push($j(this).val());
		});
		let objectOfThisRow = {};

		for (let j = 0; j < arrayOfThisRow.length; j++) {
			objectOfThisRow = {
				url: arrayOfThisRow[0],
				user: arrayOfThisRow[1],
				password: arrayOfThisRow[2],
				location: arrayOfThisRow[3],
			}
		}

		dhisserver.configurations.push(objectOfThisRow);
	});


	if (dhisserver.configurations.length > 0) {
		//jQuery("#error-encountered-saving").html("");
		// post json object
		jQuery.ajax({
			url: OMRS_WEBSERVICES_BASE_URL
				+ "/ws/rest/v1/dhisconnector/servers",
			type: "POST",
			data: JSON.stringify(dhisserver),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(data) {
				//window.location = '../../module/dhisconnector/manageMappings.form';
				alert('Configuraçoes salvas com sucesso');
			}
		});
	} else {
		alert('Deve existir pelo menos uma configuração.');
	}


}

function deleteServerConfig(objectOfThisRow) {
	jQuery.ajax({
		url: "../../module/dhisconnector/deleteServer.form?location=" + objectOfThisRow.location + "&url=" + objectOfThisRow.url,
		type: "POST",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function(data) {
			window.location = '../../module/dhisconnector/configureServer.form';
		},
		error: function(data) {
			console.log(data);
		}
	});
}


	function populateReportsToEachServer(servers) {
	// fetch reports     jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/periodindicatorreports?q=hasMapping", function (data) {
	jQuery.ajaxSetup({ async: false });
	jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/periodindicatorreports", function(data) {

		for (var i = 0; i < servers.length; i++) {
			for (var j = 0; j < data.results.length; j++) {
				jQuery('#' + servers[i] + '').prepend('<input type="checkbox" id="' + servers[i] + data.results[j].uuid + '" value="' + data.results[j].uuid + '">' + data.results[j].name + '</input><br>');
			}

		}
	});

	jQuery.ajax({
		type: "GET",
		url: "getServersAndReportsToReceive.form",
		async: false,
		data: "",
		datatype: "json",
		success: function(data) {
			for (var j = 0; j < servers.length; j++) {

				$j('#table tbody tr').each(function(i) {

					$j(this).find('td').each(function(i) {

						$j(this).find('input').attr('id', function(i) {

							for (var i = 0; i < data.length; i++) {
								
								console.log(data[i]);
								
								console.log($j(this).attr('id'));

								if ($j(this).attr('id') === data[i]) {

									document.getElementById($j(this).attr('id')).checked = true;

								}
							}
						});
					});
				});
			}
		}
	});

	}

function getReportsAndPopulateToEachServer() {
	jQuery.ajax({
		type: "GET",
		url: "getServers.form",
		data: "",
		datatype: "json",
		success: function(data) {
			populateReportsToEachServer(data);
		}
	});
}

function saveReportsOfTheSelectedServer(elm) {
	
	let arrayOfThisRow = [];
	
	if(elm !== undefined){
	let serveruuid = elm.parentNode.id;
    var childInputs = document.getElementById(''+serveruuid+'').getElementsByTagName('input');
    
    arrayOfThisRow.push(serveruuid);
    
	for (var i = 0; i < childInputs.length; i++) {
		if(childInputs[i].checked)
			arrayOfThisRow.push(childInputs[i].value);
	}
			if (arrayOfThisRow.length == 1) {
			alert('O relatorio a configurar nao tem nenhum report selecionado, seleccione pelo menos um.');
			return;
		}
		
			if (arrayOfThisRow.length > 1) {
		jQuery.ajax({
			url: "../../module/dhisconnector/saveDHISServerSespReportsToReceive.form",
			type: "POST",
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(arrayOfThisRow),
			dataType: "json",
			success: function(data) {
				window.location = '../../module/dhisconnector/configureServer.form';
			},
			error: function(data) {
			}
		});
		}
	}
}

function toggler(divId) {
  $j("#" + divId).toggleClass("hidden");
}
