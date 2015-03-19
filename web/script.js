defaultEntry = {
	"id": 1,
	"default": true,
	"conds": {
		"reactionTime": -1,
		"reactionTimeCondition": "not_used",
		"plateCondition": "not_used",
		"footCondition": "not_used",
		"ok": false
	}
}
window.entries = [defaultEntry];

function selectedPanel() {
	$("#fragment_history").hide();
	$("#fragment_panel").show();
	window.location.hash = "panel";
}
function selectedHistory() {
	$("#fragment_panel").hide();
	$("#fragment_history").show();
	window.location.hash = "history";
}


function connectSocket() {
	h = window.location.hostname.length > 0 ? window.location.hostname : "localhost";
	window.webSocket = new WebSocket("ws://"+h+":7070");
	window.webSocket.onopen = function() {
		request = {
			content: "request",
			type: "history",
			count: 10
		}
		this.send(JSON.stringify(request));
	}
	window.webSocket.onmessage = socketMessage;
	window.webSocket.onclose = function() {
		//location.reload()
	}
}

function clearBoomedCounterInterval() {
	if (window.boomedCounterInterval) {
		clearInterval(window.boomedCounterInterval);
		window.boomedCounterInterval = 0;
	}
}

function socketMessage(evt) {
	json = JSON.parse(evt.data);
	switch (json.content) {
		case "response":
			switch (json.type) {
				case "history":
					if (json.count > 0)
						window.entries = json.data;
					else
						window.entries = [defaultEntry];
					updatePanel(window.entries[0]);
					updateHistory(json.data);
					break;
			}
			break;
		case "event":
			switch (json.type) {
				case "entry":
					clearBoomedCounterInterval();
					if (window.entries[0].default) {
						window.entries = [json.data];
					} else {
						window.entries.unshift(json.data);
					}
					updatePanel(json.data);
					updateHistory(window.entries);
					break;
				case "boomed":
					updateTime(0);
					$("#stats ul li").css("color", "white");
					window.boomed = new Date().getTime();
					window.boomedCounterInterval = setInterval(function() {
						time = new Date().getTime() - window.boomed;
						time = time - (time % 10);
						updateTime(time);
						/*if (time >= 4000) {
							clearInterval(window.boomedCounterInterval)
							$("#reaction_time").html("&mdash;.&mdash;&mdash;&mdash;");
						}*/
					}, 5);
					break;
				case "statechange":
					clearBoomedCounterInterval();
					break;
			}
			break;
		case "error":
			message = json.originalMessage;
			setTimeout(function() {
				window.webSocket.send(message);
			}, 500);
			break;
	}
}

function updatePanel(entry) {
	updateTime(entry.conds.reactionTime)
	el = $("#stats_plate");
	switch (entry.conds.plateCondition) {
		case "correct":
			el.css("color", "green");
			break;
		case "incorrect":
			el.css("color", "red");
			break;
		case "not_used":
			el.css("color", "grey");
			break;
	}
	el = $("#stats_foot")
	switch (entry.conds.footCondition) {
		case "correct":
			el.css("color", "green");
			break;
		case "incorrect":
			el.css("color", "red");
			break;
		case "not_used":
			el.css("color", "grey");
			break;
	}
	el = $("#stats_reactiontime")
	switch (entry.conds.reactionTimeCondition) {
		case "correct":
			el.css("color", "green");
			break;
		case "incorrect":
			el.css("color", "red");
			break;
		case "not_used":
			el.css("color", "grey");
			break;
	}
	//$("#stats ul li").css("display", "inline-block");
}

function updateTime(time) {
	if (time >= 0) {
		$("#reaction_time").text((time/1000).toFixed(3));
	} else {
		//$("#reaction_time").html("&mdash;.&mdash;&mdash;&mdash;")
		$("#reaction_time").text("0.000")
	}
}

function updateHistory(array) {
	$("#history_table table tr:not(.head)").remove();
	table = $("#history_table table");
	for (i = 0; i < array.length; i++) {
		entry = array[i];
		el = "";
		el += "<tr>";
		el += '<td>'+entry.id+'</td>';
		if (entry.conds.reactionTime >= 0) {
			el += '<td style="color:#800;">'+(entry.conds.reactionTime/1000).toFixed(3)+'</td>';
		} else {
			el += '<td style="color:#800;">/</td>';
		}
		switch (entry.conds.plateCondition) {
			case "correct":
				el += '<td style="color:green;">OK</td>';
				break;
			case "incorrect":
				el += '<td style="color:red;">MISSED</td>';
				break;
			case "not_used":
				el += '<td style="color:grey;">&mdash;</td>';
				break;
		}
		switch (entry.conds.footCondition) {
			case "correct":
				el += '<td style="color:green;">OK</td>';
				break;
			case "incorrect":
				el += '<td style="color:red;">WRONG</td>';
				break;
			case "not_used":
				el += '<td style="color:grey;">&mdash;</td>';
				break;
		}
		el += "</tr>";
		table.append(el);
	}
}

$(function(){
	connectSocket();

	if (window.location.hash.substr(1) == "history") {
		selectedHistory();
	} else {
		selectedPanel();
	}

	window.document.body.onkeydown = function(e) {
		switch (e.keyCode) {
			case 72:
				selectedHistory();
				break;
			case 80:
				selectedPanel();
				break;
		}
	}
});
