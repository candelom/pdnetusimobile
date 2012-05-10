  /**
	    * Gets all albums from FB
	    */
       function getAlbums(callback) {
         
          
           albums = [];
           FB.api('/me/albums', function(me){

             $.each(me.data, function(key, value) {
               
                var id_album = me.data[key]["id"];
                var album_name = me.data[key]["name"];
                var img_number = me.data[key]["count"];
                var cover_photo = me.data[key]["cover_photo"];

                var info = [];
                info.push(id_album);
                info.push(album_name);
                info.push(img_number);
                info.push(cover_photo);
      
                albums.push(info);
                
                });

                callback(albums);
              });
              

       }



    /**
     * Load Albums already stored in localStorage
     */
    function loadAlbums() {
        for(var key in localStorage) {
            var album_obj = JSON.parse(localStorage[key]);
            if(album_obj['fav']) {
	            $("#favorites").prepend($("<div class=\"album\" id=\"fav_album_"+key+"\">"+
	                                       "<div class=\"album_img\">"+
	                                          "<img src=\"https://graph.facebook.com/"+key+"/picture\" width=\"50px\" height=\"50px\"/>"+
	                                       "</div>"+
	                                       "<div class=\"album_info\">"+
	                                            "<p>"+album_obj['name']+"</p>"+
	                                        "</div>"+
	                                   "</div>"));
            }
        }
    }



	/**
	 * Returns the number of favorite albums
	 *  
	 */
	function getNumOfFavorites() {
	
		var num = 0;
		var fav_albums = [];
	    for(var key in localStorage) {
	       var album_obj = JSON.parse(localStorage[key]);
	       if(album_obj['fav']) {
	           num += 1;
	       }
	    }
	    
	    return num;
	}


	/**
	 * Add album to Favorite view
	 *
	 */
    function addAlbumToFavorites(id_album, album_name) {
         
        //if there are no favorite albums yet, update favorite view        
        if(getNumOfFavorites() == 0) {
           $("#publish_bt").show();
           $("#favorites").find("p").remove();
           localStorage['photostream'] = [];
        } 
         
        var stored_album = JSON.parse(localStorage[id_album]); 
        stored_album['fav'] = true;
        localStorage[id_album] = JSON.stringify(stored_album);
        localStorage['photostream'].push(id_album);
        
		//update the favorite View html
        var fav = "<div class=\"album\" id=\"fav_album_"+id_album+"\">"+
                                     "<div class=\"album_img\">"+
                                        "<img src=\"https://graph.facebook.com/"+id_album+"/picture\" width=\"50px\" height=\"50px\"/>"+
                                     "</div>"+
                                     "<div class=\"album_info\">"+
                                          "<p>"+album_name+"</p>"+
                                      "</div>"+
                                  "</div>";

        $("#favorites").prepend($(fav));

    }



	/**
	 * Remove the given album from localStorage (array of favorites)
	 * 
	 */
    function removeAlbumFromFavorites(id_album, album_name) {
      
        //remove corresponding html
        $("#favorites").find("#fav_album_"+id_album).remove();
        var stored_album = JSON.parse(localStorage[id_album]);
        stored_album["fav"] = false;
    	localStorage[id_album] = JSON.stringify(stored_album);
    	
    	if(getNumOfFavorites() == 0) {
           $("#publish_bt").hide();
           $("#favorites").append("<p>Select your favorite album</p>");
        } 

    }




	/**
	 * Checks whether the given album is contained in localStorage 
	 */
    function isFavorite(id_album) {
      
        var stored_album = JSON.parse(localStorage[id_album]);
        if(stored_album['fav']) {
          return true;
        } 
      	return false;

    }



    function goToImagesView(id_album, album_name) {
        
        console.log('goToImagesView');
        //if album is not in localStorage
        if(!localStorage[id_album]) {
           
            alert("not yet stored");
            $("#albums").hide();
            $("#album-"+id_album).css("background-color", "inherit").css("background-image", "none").css("color", "#555555");
            FB.api('/'+id_album+'/photos', function(resp) {


				$("#add_cb").prop('checked', false);
                $("#add_cb").val(id_album);
                $("#add_cb").off("click");
                $("#add_cb").click(function() {
					if($(this).attr("checked")) {
						addAlbumToFavorites(id_album, album_name);				
					} else {
						removeAlbumFromFavorites(id_album, album_name);
					}
				});
                
                
                
                $("#album_imgs").append($("<div id=\"imgs_"+id_album+"\"></div>"));
				
                var img_sources = [];
                for(var i = 0; i<resp.data.length; i++) {
                     var image =  "<div id=\"photo-"+resp.data[i]["id"]+"\" style=\"float:left;padding:5px;\">"+
                                       "<img src=\""+resp.data[i]['source']+"\" width=\"90px\" height=\"80px\" />"+
                                   "</div>";
                    
                    img_sources.push(resp.data[i]['source']);
                    $("#imgs_"+id_album).append($(image));
                }

		        var album_obj = {'name': album_name, 'imgs': img_sources};
                localStorage[id_album] = JSON.stringify(album_obj);
				
         	});
				
		
				
        } else {
          
               $("#albums").hide();
               $("#album-"+id_album).css("background-color", "white").css("color", "#555555");
           
                var storedImages = JSON.parse(localStorage[id_album]).imgs;
				$("#add_cb").val(id_album);
                $("#album_imgs").append($("<div id=\"imgs_"+id_album+"\"></div>"));
                
               $("#add_cb").off("click");
               if(isFavorite(id_album)) {
		           console.log('already in local storage');
		           $("#add_cb").prop('checked', true);
		           
               } else {
		           console.log('not yet in local storage');
		           $("#add_cb").prop('checked', false);
		           
               }
                
              
              	 $("#add_cb").click(function() {
					if($(this).attr("checked")) {
						addAlbumToFavorites(id_album, album_name);				
					} else {
						removeAlbumFromFavorites(id_album, album_name);
					}
				
				});
              
                
                
                for(var i = 0; i < storedImages.length; i++) {
                    var source = storedImages[i];
                    var image =  "<div style=\"float:left;padding:5px;\">"+
                                       "<img src=\""+source+"\" width=\"90px\" height=\"80px\" />"+
                                   "</div>";
                  
                    $("#imgs_"+id_album).append($(image));
                }
				
        }

       $("#images").show();

    }





    
    function unsetDivHighlight() {

        $("div[id*='album-']").each(function(i) {
            $(this).css("background", "none").css("color", "black");
        });

    }


	/**
	 * Redirect to albums list view
	 */
    function goBackToList() {
        
        $("#images").hide();

        unsetDivHighlight();
        $("#images #album_imgs").find("div").remove(); 
        $("#albums").show();
        
    }


	function setFavCheckbox() {
	
		$("#add_cb").click(function() {
			
			if($(this).attr("checked")) {
				addAlbumToFavorite();				
			} else {
				addAlbumToFavorite();			
			}
		
		});
		
		
	}

	

    function setAlbumImagesView(id_album, album_name) {

        $("#images h2").append("Photo of album "+album_name);

    }


		
    
    
      function getPhotos(id_album, album_name) {
        
            $("#album-"+id_album).css("background-image", "none");
            setTimeout("goToImagesView("+id_album+", '"+album_name+"')", 300);   
      }


		
		
	   	
       function addCovers(albums) {
         
          $("#albums_res").append("<div id=\"divider\" class=\"ui-li ui-li-divider ui-bar-b\">Facebook</div>");
          
          var html = "";
          for(var i =0 ; i < albums.length; i++) {
              
                source = null;
                var id_album = albums[i][0];
                var album_name = albums[i][1];
                var album_count = albums[i][2];
                var source = albums[i][3];

                var html = "<div class=\"album\" id=\"album-"+id_album+"\" onclick=\"getPhotos('"+id_album+"', '"+album_name+"');\">"+
                              "<div class=\"album_img\">"+
                                  "<img src=\"https://graph.facebook.com/"+id_album+"/picture\" width=\"50px\" height=\"50px\"/>"+
                                      "</div>"+
                                      "<div class=\"album_info\">"+
                                          "<p>"+album_name+"</p>"+
                                      "</div>"+
                                      "<div class=\"album_count\">"+
                                          "<p>"+album_count+"</p>"+
                                      "</div>"+
                                  "</div>";
                    
                $("#albums_res").append($(html));
              
          }          
         
          $("#loading-album").hide();

       }


		
	   /**
	    * Set Back button functionality
	    */
       function setBackButton() {
         
            
             $("#back_bt").click(function() {
          
                 goBackToList();
                 $("#album_imgs").html("");

             });

       }
       
       
       
	   /**
	    * Shows the Albums view and hide Favorite and Sources views
	    */
       function showAlbums() {
          
          $("#favorites").hide();
          $("#sources").hide();
          $("#album_imgs").html("");
          $("#images").hide();
          $("#albums").show();

       }
 

	  /**
	    * Shows the Favorite view and hide Sources and Albums views
	    */
       function showFavorites() {
          
             $("#albums").hide();
             $("#sources").hide();
             $("#album_imgs").html("");
             $("#images").hide();

             if(localStorage.length == 0) {
                $("#favorites").find("p").remove();
                $("#favorites").prepend("<p>No images or albums are selected.</p>");
                $("#publish_bt").hide();
             }
              
             $("#favorites").show();
             
        }
        
        

       /**
	    * Shows the Sources view and hide Albums and Favorites views
	    */
        function showSources() {
             
          $("#favorites").hide();
          $("#albums").hide();
          $("#album_imgs").html("");
          $("#images").hide();
          $("#sources").show();

        }



		/**
		 * Initialize html content of Favorite view
		 */
        function initializeFavoriteView() {
            if(getNumOfFavorites() == 0) {      
                $("#favorites").append($("<p>No albums or images are selected.</p>"));
            } else {
                loadAlbums();    
                alert('publishing buttons');
                $("#publish_bt").show();        
            }
        }


        /**
         * 
         * Send albums preferences to application controller through websockets
         */
        function publishAlbums() {
                   
              var fav_albums = [];
              for(var key in localStorage) {
                 
                  var album_obj = JSON.parse(localStorage[key]);
                  if(album_obj['fav']) {
                     fav_albums.push(album_obj);
                  }                  
               }
        
        	   var jsonAlbums = {}
        	   jsonAlbums.albums = fav_albums;
	           jsonAlbums.username = "@username";
	           jsonAlbums.displayID = "@display";
	           console.log(jsonAlbums);
	           socket.send(JSON.stringify(jsonAlbums));

        }


		/**
		 * Triggers Facebook Login when toggle switch "Facebook" is On
		 */
        function setFBLogin() {
            $("#fb_slider").change(function() {
                if($(this).val() == "on") {
                    FB.login();
                } 
                else if($(this).val() == "off") {
                    FB.logout();
                }
            });

        }