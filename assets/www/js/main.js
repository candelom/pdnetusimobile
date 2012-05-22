

			
		/**
		 * Read applications from XML file on server
		 * @returns {} 
		 */
		function loadApps() {
		
			$.ajax({
				 type: "GET",
				 url: pdnet_host+"/assets/applications/list.xml",
				 dataType: "xml",
				 success: function(xml) {
				 	 var num_of_apps = $(xml).find("app").length;
					 $(xml).find('app').each(function(i){
						 var app_name = $(this).find("name").text();
						 var app_desc = $(this).find("description").text();
						 var app_name_space = $(this).find("namespace").text();
						 var app_view = $(this).find("view").text();
						 var app_icon = $(this).find("icon").text();
						 var app_socket = $(this).find("websocket_address").text();
						 var app_image = $(this).find("preview-image").text();

//						 var app_image = $(this).find("size")[1].find("preview_image").text();
						 
						 var app_div = "<div id='"+app_name_space+"' class='app_entry'>"+
						 					"<div class='app_icon'><img src='"+app_icon+"' width='40px' height='40px'></div>"+
						 					"<div class='app_name'>"+
						 						"<span class='app_title'>"+app_name+"</span>"+
						 					"</div>"+
											"<div class='app_install'></div>"+						 					
						 				"</div>";
										
						 $("#all_apps_content").append(app_div);
						 var app_info_div = "<div id='"+app_name_space+"_info' class='app_info_entry'>"+
													"<div class='titlebar_header'>"+
														"<div class='titlebar_left'><a class='back_to_apps' href='#'><img src='images/back-icon-2.png' /></a></div>"+
														"<div class='titlebar_title'><span>"+app_name+"</span></div>"+
														"<div class='titlebar_right'></div>"+
													"</div>"+
													"<div class='app_info_content'>"+
														"<div class='app_info_img'>"+
															"<img src='"+app_image+"' width='200px' height='200px' />"+
														"</div>"+
														"<div class='app_info_desc'>"+
															"<span class='app_info_desctitle'>Description</span>"+
															"<span><p>"+ app_desc+
															"</p></span>"+
														"</div>"+
													"</div>"+					
													"<div class='install_bt'>"+
													"</div>"+
											"</div>";

						 $("#app_info").append(app_info_div);
						 $("#"+app_name_space+"_info .back_to_apps").click(function() {
							showAllApps();
						 });
						 
						 
						 $("#"+app_name_space).click(function() {
							showAppInfo(app_name_space);
						 });
						 
						 
						 
						 if(JSInterface.isAppInstalled(app_name_space)) {
														
							$("#"+app_name_space+" .app_install").text("Installed");
							var uninstall_bt = "<button class='install_bt_el' type='button'>Uninstall</button>";
							$("#"+app_name_space+"_info .install_bt").append(uninstall_bt);
							setUninstallButton(app_name_space);
							
						 } else {
						 	$("#"+app_name_space+" .app_install").text("Free");
							 //set install function on click
							 var install_bt = "<button class='install_bt_el' type='button'>Install</button>";
							 $("#"+app_name_space+"_info .install_bt").append(install_bt);
							 setInstallButton(app_name_space);
							 
						 }
						 
					 });

					console.log("loaded app successfully");
					
					showAllApps(); 
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
			 url: pdnet_host+"/assets/displays/list.xml",
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

		
		
	function setInstallButton(app_name_space) {
		
		$("#"+app_name_space+"_info .install_bt").off("click");
		$("#"+app_name_space+"_info .install_bt").click(function() {
//		    alert("INSTALL");
		 	installApp(app_name_space);
		 });
	}
	
	
	function setUninstallButton(app_name_space) {
		$("#"+app_name_space+"_info .install_bt").off("click");
		$("#"+app_name_space+"_info .install_bt").click(function() {
//		    alert("UNINSTALL");
		 	uninstallApp(app_name_space);
		 });
	}
	

	
	function setActiveTab(id) {
		
		$("#" + id).addClass("active");
		$("#"+id).html("<img src='images/tabss/"+id+"_sel.png' style='margin-top:10px' />");
		
	}




	/**
	 * Show only "My apps" view 	
	 */
	function showMyApps() {
		$("#tabs").show();
		$("#my_apps_content").show();
		setActiveTab("my_apps");
		unsetAllAppsTab();
		unsetMapTab();
		$("#app_info").hide();
		$("#my_apps").css("margin-top", "0px");
		$("#all_apps").css("margin-top", "10px");
		$("#pd_map").css("margin-top", "10px");

	}
	
	
	/**
	 * Show only "All apps" view 	
	 */
	function showAllApps() {
		$("#tabs").show();
		$("#all_apps_content").show();
		setActiveTab("all_apps");
		unsetMapTab();
		unsetMyAppsTab();
		$("#app_info").hide();
		$("#all_apps").css("margin-top", "0px");
		$("#pd_map").css("margin-top", "10px");
		$("#my_apps").css("margin-top", "10px");

		
	}
	
	
	
	function unsetMyAppsTab(){
	
		$("#my_apps_content").hide();
		$("#my_apps").html("<img src='images/tabss/my_apps.png' />");
	
	}
	
	
		
	function unsetAllAppsTab(){
	
		$("#all_apps_content").hide();
		$("#all_apps").html("<img src='images/tabss/all_apps.png' />");
	
	}
	
	
		
	function unsetMapTab(){
		$("#pd_map_content").hide();
		$("#pd_map").html("<img src='images/tabss/pd_map.png' />");
	}
	
	
	
	
	/**
	 * 
	 * Show only "Map" view
	 */
	function showMap() {
		$("#tabs").show();
		$("#pd_map_content").show();
		setActiveTab("pd_map");
		unsetMyAppsTab();
		unsetAllAppsTab();
		$("#app_info").hide();
		$("#pd_map").css("margin-top", "0px");
		$("#all_apps").css("margin-top", "10px");
		$("#my_apps").css("margin-top", "10px");
		//window.onresize = setViewport;

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


	
	function markAsInstalled(app_name_space) {
		$("#"+app_name_space+" .app_install").text("Installed");
		setUninstallButton(app_name_space);
	}
	
	
	
	function markAsFree(app_name_space) {
		$("#"+app_name_space+" .app_install").text("Free");
		setInstallButton(app_name_space);
	}
	
	
	

	
	
	
	function loadMyApps() {
		
		$.ajax({
			 type: "GET",
			 url: "../apps/my_list.xml",
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
	
	
	
	function setOnOffButton(app_namespace, app_socket) {
		
			$("#my_"+app_namespace+" ul li").click(function(){
				$("#my_"+app_namespace+" ul li").removeClass("on");
				$(this).addClass("on");
				
				var toggle = $(this).find("a").text();
				if(toggle == "ON") {
					
					activateUserPreference(app_namespace);
					
				} else if(toggle = "OFF"){
					
					deactivateUserPreference(app_namespace);
				}
				
			});
		
	}
	
	
	