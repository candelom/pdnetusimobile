<?php

	  $doc = new DOMDocument();
	  $doc->preserveWhiteSpace = false;
	  $doc->formatOutput = true;
	  
 	  $doc->load('apps/my_list.xml');
 	  

 	  $apps = $doc->getElementsByTagName("apps")->item(0);
 	  
 	  $app = $doc->createElement("app");
  		
 	  $name = $doc->createElement("name");
 	  $name->appendChild($doc->createTextNode($_POST['name']));
 	  
 	  $namespace = $doc->createElement("namespace");
 	  $namespace->appendChild($doc->createTextNode($_POST['namespace']));
 	  
 	  $view = $doc->createElement("view");
 	  $view->appendChild($doc->createTextNode($_POST['view']));
 	  
 	  $socket = $doc->createElement("socket");
 	  $socket->appendChild($doc->createTextNode($_POST['socket']));
 	  
 	  $app->appendChild($name);
 	  $app->appendChild($namespace);
 	  $app->appendChild($view);
 	  $app->appendChild($socket);
 	  
 	  
 	  $apps->appendChild($app);
 	  
 	  echo $doc->save('apps/my_list.xml');
?> 