<?php  
if(isset($_GET['user'])) {
  /* soak in the passed variable or set our own */
  $user_lat = $_GET['lat'];
  $user_lng = $_GET['lng'];
  $format = strtolower($_GET['format']) == 'json' ? 'json' : 'xml'; //xml is the default
  $user_id = $_GET['user']; //no default
  /* connect to the db */
  $link = mysql_connect('mysql.2freehosting.com','u637985876_root','bustracker') or die('Cannot connect to the DB');
  mysql_select_db('u637985876_mydb',$link) or die('Cannot select the DB');
  /* grab the posts from the db */
  //$query = "UPDATE bus SET lat=$user_lat, lng=$user_lng WHERE bus_no = 
  //  $user_id AND post_status = 'publish' ORDER BY ID DESC LIMIT $number_of_posts";
  $query = "UPDATE `u637985876_mydb`.`bus` SET lat=$user_lat, lng=$user_lng WHERE bus_no='$user_id';";
  $result = mysql_query($query,$link) or die('Errant query:  '.$query);
  /* create one master array of the records */
  $update = array();
  if($result) {
      $update[] = array('status'=>1, 'message'=>'updated successfully');
  }else{

$update[] = array('status'=>0, 'message'=>'updated fail');
	}
  /* output in necessary format */
  if($format == 'json') {
    header('Content-type: application/json');
    echo json_encode(array('update'=>$update));
  }
  else {
    header('Content-type: text/xml');
    echo '';
    foreach($posts as $index => $post) {
      if(is_array($post)) {
        foreach($post as $key => $value) {
          echo '<',$key,'>';
          if(is_array($value)) {
            foreach($value as $tag => $val) {
              echo '<',$tag,'>',htmlentities($val),'</',$tag,'>';
            }
          }
          echo '</',$key,'>';
        }
      }
    }
    echo '';
  }
  /* disconnect from the db */
  @mysql_close($link);
}
?>