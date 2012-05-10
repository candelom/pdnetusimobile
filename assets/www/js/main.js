

	


			
		/**
		 * Read applications from XML file on server
		 * @returns {} 
		 */
		function loadApps() {
		
			
			$.ajax({
				 type: "GET",
				 url: "apps/list.xml",
				 dataType: "xml",
				 success: function(xml) {
				 
				 	 var num_of_apps = $(xml).find("app").length;
					 $(xml).find('app').each(function(i){
						 
						 var app_name = $(this).find("name").text();
						 var app_desc = $(this).find("description").text();
						 var app_name_space = $(this).find("namespace").text();
						 var app_view = $(this).find("view").text();
						 var app_icon = $(this).find("icon").text();
						 var app_socket = $(this).find("socket").text();
						 
						 
						 var app_div = "<div id='"+app_name_space+"' class='app_entry'>"+
						 					"<div class='app_icon'><img src='"+app_icon+"' width='40px' height='40px'></div>"+
						 					"<div class='app_name'>"+
						 						"<span class='app_title'>"+app_name+"</span>"+
						 						"<span>"+app_desc+"</span>"+
						 					"</div>"+
											"<div class='app_install'>Install</div>"+						 					
						 				"</div>";
						 
						 
						 
						 $("#all_apps_content").append(app_div);
						 
						 //set install function on click
						 $("#"+app_name_space).click(function() {
						 	
							installApp(app_name_space);
							$(this).off('click');
						 
						 });
						 
						 
						 
					 });
					 
				 } 	
			});
	}



	/**
	 * Read all displays from XML file on server
	 * @param map
	 * @returns
	 */
	function loadDisplays() {
		
		$.ajax({
			 type: "GET",
			 url: "displays/list.xml",
			 dataType: "xml",
			 success: function(xml) {
				 $(xml).find('display').each(function(i){
					 
					 var id = $(this).find("id").text();
					 var name = $(this).find("name").text();
					 var lat = $(this).find("latitude").text();
					 var lng = $(this).find("longitude").text();
					  
					 displays.push([id, name, lat, lng]);
					 
				 });
			 } 	
		});
		
	}
	


	/**
	 * Creates a WebSocket instance for the given application
	 * @param app_name
	 */

	function createAppSocket(app_socket_addr) {
		
   	 	
        var app_socket = {};
   	 	app_socket.name = app_name;
   	 	app_socket.socket = new WebSocket(app_server);
   	 	sockets.push(app_socket);

	
	}


	
		



	
	function setActiveTab(id) {
		
		$(".tab").each(function() {
			if($(this).attr("id") == id) {
				$(this).addClass("normal_tab");
				$(this).addClass("active_tab");
			} else {
				$(this).removeClass("active_tab");
				$(this).addClass("normal_tab");
			}
			
		});
		
	}








	/**
	 * Show only "Share" view
	 */
	function showShareView() {
		console.log("show share apps");
	
		
		$("#share_content").show();
		setActiveTab("share");
		$("#all_apps_content").hide();
		$("#my_apps_content").hide();
		$("#share_item").hide();

	
	}

	/**
	 * Show only "My apps" view 	
	 */
	function showMyApps() {
		
		if($("#my_apps_content").html().length == 0) {
			loadMyApps();
		}
		
		$("#my_apps_content").show();
		setActiveTab("my_apps");
		$("#all_apps_content").hide();
		$("#share_content").hide();
		$("#share_item").hide();

	}

	
	/**
	 * Show only "All apps" view 	
	 */
	function showAllApps() {
		
		$("#all_apps_content").show();
		setActiveTab("all_apps");
		$("#my_apps_content").hide();
		$("#share_content").hide();
		$("#share_item").hide();
		$("#pd_map_content").hide();
		
	}
	
	
	
	/**
	 * Show only "Share item" view
	 */
	function showShareItem() {
		
		$("#share_item").show();
		$("#all_apps_content").hide();
		$("#my_apps_content").hide();
		$("#share_content").hide();
		$("#tabs").hide();

	}
	
	
	/**
	 * 
	 * Show only "Map" view
	 */
	function showMap() {
		
		$("#pd_map_content").show();
		$("#all_apps_content").hide();
		$("#my_apps_content").hide();
		$("#share_content").hide();
		$("#tabs").hide();
		
		
	}
	
	
	/**
	 * Initializes google map
	 */
	function initializeMap() {
		
		var myOptions = {
				zoom: 15,
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				mapTypeControlOptions: { style: google.maps.MapTypeControlStyle.DROPDOWN_MENU }
			};
        
        map = new google.maps.Map(document.getElementById("pd_map_content"), myOptions);
        
     }

	

	function insertTiles(apps) {
			
		for(var i = 0; i < apps.length; i++) {
			
			var div_tile = $("<div class='share_div'>"+
								"<div class='app_name'><span>"+apps[i]+"</span></div>"+
								"<div class='share_icons'>"+
									"<div class='fb_icon'><img src='http://172.16.224.104:9000/assets/images/fb-icon.png' /></div>"+
									"<div class='gp_icon'><img src='http://172.16.224.104:9000/assets/images/gp-icon.png' /></div>"+
								"</div>"+
							"</div>");
			
			$("#share_content").append(div_tile);
			div_tile.click(function() {
				console.log("item");
				showShareItem();
				
			});
		}
	
	}

	
	function postonwall()
	{
	    FB.api('/me/feed', 'post',
	    {
	        message: 'A good reference for APIs',
	        link:'http://www.pdnet.com',
	        name:'News Feed App',
	        picture:'http://www.demo.lookmywebpage.com/publish-on-facebook-wall/Google-Twitter-Facebook.jpg',
	        description:'News Feed App content on pdnet display'
	    }, function(response) {
	        if (!response || response.error) {
	        	alert('Oops! User Denied Access');
	        } else {
	        	alert('Success: Content Published');
	        }
	    });
	}


	
	function markInstalledApp(app_name_space) {
		
		$("#"+app_name_space+" .app_install").text("Installed");
		
	}
	
	
	

	/**
	 * Adds app into locally installed app.
	 * @param app_name_space
	 * @returns
	 */
	function installApp(app_name_space) {
		
		console.log(typeof(app_name_space));
		var app_socket = "";
		var app_view = "";
		var app_name = "";
		
		
		$.ajax({
			type: "GET",
			url: "apps/list.xml",
			dataType: "xml",
			success: function(xml) {
				 
				var num_of_apps = $(xml).find("app").length;
				$(xml).find('app').each(function(i){
				
					var cur_namespace = $(this).find("namespace").text();
					if(cur_namespace == app_name_space) {
						console.log("setting app info");
						app_name = $(this).find("name").text();
						app_view = $(this).find("view").text();
						app_socket = $(this).find("socket").text();
						
					}
					
					
				});
				
				//update my_list.xml file by adding app
				$.ajax({
					type: "POST",
					url: "save_xml.php",
					data: {name: app_name, namespace: app_name_space, view: app_view, socket: app_socket},
					success: function() {
						
						var my_app_div = "<div id='my_"+app_name_space+"' class='my_app_entry'>"+
											"<div class='my_app_name'>"+app_name+"</div>"+
											"<div class='my_app_toggle'>"+
												"<ul>"+
													"<li class='on'><a href='#'>OFF</a></li>"+
													"<li><a href='#'>ON</a></li>"+
												"</ul>"+
											"</div>"+
										"</div>";
											
						
						$("#my_apps_content").append(my_app_div);
						
						//set link to app configuration view
						$("#my_"+app_name_space+" .my_app_name").click(function() {
							window.location.replace(app_view); 
						});
						
						
						//set button to activate or disactivate application socket
						$("#my_"+app_name_space+" ul li").click(function(){
							$("#my_"+app_name_space+" ul li").removeClass("on");
							$(this).addClass("on");
							
							var toggle = $(this).find("a").text();
							if(toggle == "ON") {
								
								activateUserPreference(app_name_space);
								
							} else if(toggle = "OFF"){
								
								deactivateAppSocket(app_socket);
								console.log(sockets);
							}
							
						});
						
						markInstalledApp(app_name_space);
						
						//create entry in SQL android DB
						createEntry("mattia", app_name_space)
						showMyApps();
						
					}
				});
				
			}
			});
			
			
		
	}
	
	
	function loadMyApps() {
		
		$.ajax({
			 type: "GET",
			 url: "apps/my_list.xml",
			 dataType: "xml",
			 success: function(xml) {
				 
				 $(xml).find("app").each(function() {
					 
					 
					var name = $(this).find("name").text();
					var namespace = $(this).find("namespace").text();
					var view = $(this).find("view").text();
					var socket_addr = $(this).find("socket").text();
					
					 var my_app_div = "<div id='my_"+app_name_space+"' class='my_app_entry'>"+
										 "<div class='my_app_name'>"+app_data.name+"</div>"+
										 "<div class='my_app_toggle'>"+
										 	"<ul>"+
										 		"<li class='on'><a href='#'>OFF</a></li>"+
										 		"<li><a href='#'>ON</a></li>"+
										 	 "</ul>"+
										 "</div>"+
									  "</div>";
					 
					 $("#my_apps_content").append(my_app_div);
					
				 });
				 
			 }
		});
		
		
		
		
	}
	
	
	
	

	
	/**
	 * Deactivates the socket client running on the given host
	 * @param socket_addr
	 */
	function deactivateAppSocket(socket_addr) {
			
		var socket = null;
		var to_remove = 0;
		for(var j =0; j < sockets.length; j++) {
			
			if(sockets[j].socket.url == socket_addr) {

				socket = sockets[j].socket;
				to_remove = j;
				
			}
		}
		
		socket.close();
		return sockets.splice(to_remove, 1);
		
	}
	
	
	