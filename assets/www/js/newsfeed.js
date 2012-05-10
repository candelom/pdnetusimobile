


	/**
	 * Initialize feed view
	 */
	function initFeedView() {
		if("feed" in localStorage) {
			loadStoredSources();
		} 
		else {
			loadXMLSources();
		}
		setBackButton();
	}

	
	
	
	function addTopic(name) {
		
		var topic = "<div class='topic_entry' onclick=\"goToSourcesView('"+name+"');\">"+
						"<div class='topic_name'>"+name+"</div>"+
					"</div>";
		
		$("#list_topics").append(topic);
		
	}

	
	function loadStoredSources() {
		var src_html = "";
		for(key in localStorage) {
			var src_obj = JSON.parse(localStorage[key]);
			src_html += "<div id='"+src_obj.id+"' class='source_entry'>"+
			"<div class='source_img'>"+
			"</div>"+
			"<div class='source_info'>"+
			"<span>"+src_obj.name+"</span>"+		
			"</div>"+
			"</div>";
		}
		$("#list_sources").append(src_html);
	}
	
	
	
	/**
	 * LoadsXML topics and sources from xml file stored on server
	 */
	function loadXMLSources() {
		
		console.log("loading sources");
		$.ajax({
			type: "GET",
			url: "http://172.16.224.104:8888/tacita/feed.xml",
			dataType: "xml",
			success: function(xml) {
				 $(xml).find('topic').each(function(){
					 var topic = $(this).find('title').text();
					 addTopic(topic);
					 var sources = "<div id='sources_"+topic+"'>";
					 $(this).find("source").each(function() {
						 
						 var source_name = $(this).find("name").text();
						 var source_link = $(this).find("link").text();
						 var source_icon = $(this).find("icon").text();
						 
						 sources += "<div class='source_entry'>"+
						 				"<div class='source_icon'><img src='"+source_icon+"' width='20px' height='20px' /></div>"+
						 				"<div class='source_name'>"+
						 						"<span>"+source_name+"</span>"+
						 						"<span class='source_link'>"+source_link+"</span>"+
						 				"</div>"+
						 				"<div class='source_cb'></div>"+
						 			"</div>";
					 
					 });
					 
					 sources += "</div>";
					 $("#sources_list").append(sources);
					 
				 });
				 
				 setSourceEntries();
			}
		});
		
	}
	
	
	
	function setSourceEntries() {
		$(".source_entry").each(function() {
			$(this).click(function() {
			
				var source = $(this).find('.source_link').text();
				if($(this).find('.source_cb').html().length == 0) {
					checkSource($(this).find('.source_cb'), source);
				} else {
					removeSource($(this).find('.source_cb'), source);
				}
				
			});
		});
	}
	
	
	
	/**
	 * Adds source to localStorage
	 */
	function checkSource(obj, source) {
		obj.html("<img src='../../images/checked.png' />");
		if("feed" in localStorage) {
			var stored_sources = JSON.parse(localStorage["feed"]);
			stored_sources.push(source);
			localStorage['feed'] = JSON.stringify(stored_sources);
		} else {
			localStorage['feed'] = JSON.stringify([source]);
		}
	}
	
	
	
	function removeSource(obj, source) {
		console.log("remove from sources");
		obj.html("");
		
		
		var stored_sources = JSON.parse(localStorage['feed']);
		localStorage['feed'] = JSON.stringify(removeFromArray(source, stored_sources));
		if(stored_sources.length == 0) {
			localStorage.removeItem('feed');
		} 
	}
	
	
	
	function setBackButton() {
		
		$("#back_button").click(flipBack());
		
	}
	


	
	function removeFromArray(value, array) {
		
		console.log("value => "+ value);
		var index = 0;
		for(var i = 0; i < array.length; i++) {
			if(array[i] == value) {
				index = i;
			}
		}
		
		return array.splice(index, 1);
	}
	
	
	
	/**
	 * 
	 * Show the topics list of the given source
	 */
	function showSources(name) {
		
		$('#sources_list div').not('#source_title').hide();
		console.log("showinf div #sources_"+name);
		$("#sources_"+name).show().children().show().children().show();
	}
	
	
	
	
	function flipBack() {
		$(".front").removeClass("flip");
		$(".back").removeClass("flip");
	}
	
	
	/**
	 * 
	 * Redirect to page containing list of topics of the given source
	 * using a flip transition
	 */
	function goToSourcesView(name) {
		
		console.log(name);
		//triggers flip transition
		$("#source_title h1").text(name);
		showSources(name);
		$('.front').addClass('flip');
		$('.back').addClass('flip');
		$('#back_bt').show();
		
	}
	
	

	
	


	


