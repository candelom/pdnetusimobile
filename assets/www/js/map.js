

	
	
	/**
	 * 
	 * Update position of user's marker on the map
	 * @param lat
	 * @param long
	 * @param accuracy
	 * @returns
	 */
	function updateUserPosition(lat, lng, accuracy) {
		console.log("/*** UPDATE USER POSITION ***/");
		if(user_pos == null) {
			user_pos = {};
		} 
		user_pos.lat = lat;
		user_pos.lng = lng;
		user_pos.accuracy = accuracy;
	}	
	
	
	
	
	/**
	 * Adapts map's width and height to fill the entire screen
	 * @returns
	 */
	function setViewport() {
		// set map size to full screen
		width = window.innerWidth ? window.innerWidth + 'px' : '100%';
		height = window.innerHeight ? window.innerHeight + 'px' : '100%';
	
		var map_div = document.getElementById('pd_map_content');
		map_div.style.width = width;
		map_div.style.height = height;
	
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
       	window.onresize = setViewport;

     }


	
	
	
	
	/**
	 * Inserts markers corresponding to public displays in the map.
	 */
	function insertMarkers(map) {
		
		 //insert user marker
		 var pos = new google.maps.LatLng(user_pos.lat, user_pos.lng);
		 
		 var user_marker = new google.maps.Marker({
		      position: pos,
		      title: name,
		  });
		 
		 //Add a Circle overlay to the map.
	   	 var circle = new google.maps.Circle({
	     		radius: user_pos.accuracy, // 3000 km
	     		fillColor: "#FF4D4D",
	     });

		  
		  user_marker.setMap(map);
		  map.setCenter(user_marker.getPosition());
		  
		  user_pos.circle = circle;
		  user_pos.circle.setMap(map);
		  
		  user_pos.circle.bindTo('center', user_marker, 'position');
		
		
		//insert displays markers
		for(var j =0 ; j < displays.length; j++) {
			
			 var name = displays[j][1];
			 var lat = displays[j][2];
			 var lng = displays[j][3];
			 
			 var pos =  new google.maps.LatLng(lat, lng);
			 
			 var marker = new google.maps.Marker({
			      position: pos,
			      icon: 'images/display-icon.png',
			      title: name,
			 });
			 
			 marker.setMap(map);
		}
		
	}
	
	
	
	