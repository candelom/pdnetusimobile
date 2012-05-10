	/**
	 * Starts the location service.
	 *  It continuously checks the position of the user.
	 */ 
	function startLocationMonitor() {
	
		 if (navigator && navigator.geolocation) {
		   	var wpid = navigator.geolocation.watchPosition(geo_success, geo_error,  { enableHighAccuracy: true, maximumAge:30000, timeout:27000 });
		 } else {
		    error('Geolocation is not supported.');
		 }
	}
		
		
		
	/**
	 * Executes when position of user changes	
	 */ 
	function geo_success(position) {
		updateUserPosition(position.coords.latitude, position.coords.longitude, position.coords.accuracy);
		checkNearDisplays();
	}
	
		
		
	/**
	 * 	Executes when there is an error in getting position of user 
	 */ 
	function geo_error(err) {
	    if (err.code == 1) {
	        error('The user denied the request for location information.')
	    } else if (err.code == 2) {
	        error('Your location information is unavailable.')
	    } else if (err.code == 3) {
	        error('The request to get your location timed out.')
	    } else {
	        error('An unknown error occurred while requesting your location.')
	    }
	}
	
	
	
	
	/**
	 * Converts degrees value to radiants
	 * @param x
	 * @returns
	 */
	function rad(x) { 
		
		return x*Math.PI/180;
	}
	
	
	
	/**
	 * Compute distance between two given points using Haversine formula
	 * @param lat1
	 * @param long1
	 * @param lat2
	 * @param long2
	 * @returns
	 */
	function getHaversineDistance(lat1, long1, lat2, long2) {
	  var R = 6371; // earth's mean radius in km
	  var dLat  = rad(lat2 - lat1);
	  var dLong = rad(long2 - long1);
	  var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	          Math.cos(rad(lat1)) * Math.cos(rad(lat2)) * Math.sin(dLong/2) * Math.sin(dLong/2);
	  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	  var d = R * c;
	
	  return d.toFixed(3);
	}
	
				
	
	
	/**
	 * Checks whether there are dipslays in proximity of user's position
	 * @returns
	 */
	function checkNearDisplays() {
		
		for(var i = 0; i < displays.length; i++) {
			
			var cur_display = displays[i];
			
			var cur_lat = cur_display[2];
			var cur_lng = cur_display[3];
			var id = cur_display[0];

			var distance = getHaversineDistance(cur_lat, cur_lng, user_pos.lat, user_pos.lng);

			//if user is in range of 50 meters distance from display
			if(distance < 0.015) {
				triggerUserPreference(id);	
			}
			
		}	
	}
	
	
	
	
	
	