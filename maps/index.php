<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title>Twitter Stuff</title>
    <style type="text/css">
      #map-canvas {
   height: 700px;
}
.labels {
 color: white;
  background-color: red;
   font-family: "Lucida Grande", "Arial", sans-serif;
   font-size: 10px;
   font-weight: bold;
   text-align: center;
   font-color: blue;
 width: 10px;
 height: 10px;
 border: 1px solid black;
   white-space: nowrap;
 }
    </style>
    <script type="text/javascript"
        src="http://www.google.com/jsapi?autoload={'modules':[{name:'maps',version:3,other_params:'sensor=false'}]}"></script>
    <script type="text/javascript" src="markerwithlabel.js"></script>
      <?php 
   include('helper.php');
      ?>    
    <script type="text/javascript">

   /**
    * plotMarkers takes in a coordinate, label, and map to place the 
    * pin on the map in the coorect location according to the coordinate
    * with the label passed in
    **/     
   function plotMarkers(coordinate, label, map){
   var marker = new MarkerWithLabel({
     position: coordinate,
	 draggable: false,
	 raiseOnDrag: true,
	 map: map,
	 labelContent: "$425K",
	 labelAnchor: new google.maps.Point(22, 0),
	 labelClass: "labels", // the CSS class for the label
	 labelStyle: {opacity: 0.0}
     });

   var iw1 = new google.maps.InfoWindow({
     content: label
	 });

   google.maps.event.addListener(marker, "mouseover", function (e) { iw1.open(map, this); });
   google.maps.event.addListener(marker, "mouseout", function (e) {iw1.close(map, this); });

 }
/**
 * init creates a new map centered over the US.
 *
 **/
function init() {
  var mapDiv = document.getElementById('map-canvas');
  var map = new google.maps.Map(mapDiv, {
    center: new google.maps.LatLng(41.73, -99.64),
	zoom: 4,
	mapTypeId: google.maps.MapTypeId.ROADMAP
	});

  // Parses through the arrays of lats/longs, hashtags, and cities.  Puts
  // labels together and makes coordinates with lat and long.
  var cityCoordinates = new Array(jsHashtag.length);
  for (var i =0; i < jsHashtag.length; i++){
    cityCoordinates[i] = new google.maps.LatLng(jsLat[i], jsLong[i]);
    jsHashtag[i] = "<p> <b>" + jsCity[i] + "</b><br />" + jsHashtag[i] + "</p>";
  }

  // plots all the markers
  for (var i = 0; i < cityCoordinates.length; i++){
    plotMarkers(cityCoordinates[i], jsHashtag[i], map);
  }
}

// loads map on page load
google.maps.event.addDomListener(window, 'load', init);
    </script>
  </head>
  <body>
    <div id="map-canvas"></div>
    <div id="info"></div>
  </body>
</html>
