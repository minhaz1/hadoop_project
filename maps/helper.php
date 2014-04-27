<?php

echo "<script>";

// Gets the number of tweets according to the number of lines in the TSV file
$file="tweetData.tsv";
$linecount = 0;
$handle = fopen($file, "r");
while(!feof($handle)){
  $line = fgets($handle);
  $linecount++;
}
$linecount--;
fclose($handle);

// Initializes arrays
$coordinates = array($linecount);
$city = array($linecount);
$hashtag = array($linecount);
$lat = array($linecount);
$long = array($linecount);
$count = 0;

// parses file and assigns data to arrays
$file = fopen("tweetData.tsv", "r");
if ($file) {
  while (($line = fgets($file)) != false) {
    $temp = explode("\t", $line);
    $coorArr = explode(" ", $temp[0]);
    trim($coorArr[0]);
    trim($coorArr[1]);
    $lat[$count] = $coorArr[0];
    $long[$count] = $coorArr[1];
    trim($temp[1]);
    trim($temp[2]);
    $coordinates[$count] = $temp[0];
    $city[$count] = $temp[1];
    $hashtag[$count] = $temp[2];
    $count = $count + 1;
  }
} else{
  echo "File Not Found Exception";
}
fclose($file);

// Encodes the javascript arrays
$js_coordinates = json_encode($coordinates);
$js_city = json_encode($city);
$js_hashtag = json_encode($hashtag);
$js_lat = json_encode($lat);
$js_long = json_encode($long);

// Writes to javascript file
echo "var jsCoordinates = $js_coordinates;";
echo "var jsCity = $js_city;";
echo "var jsHashtag = $js_hashtag;";
echo "var jsLat = $js_lat;";
echo "var jsLong = $js_long;";

echo "</script>";
?>
