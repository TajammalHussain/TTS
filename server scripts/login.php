<?php 
if(isset($_GET['user'])) {
  /* soak in the passed variable or set our own */
  $password = $_GET['password']; //10 is the default
  $format = strtolower($_GET['format']) == 'json' ? 'json' : 'xml'; //xml is the default
  $user_id = $_GET['user']; //no default
  /* connect to the db */
  $link = mysql_connect('mysql.2freehosting.com','u637985876_root','bustracker') or die('Cannot connect to the DB');
  mysql_select_db('u637985876_mydb',$link) or die('Cannot select the DB');
  /* grab the posts from the db */
  //$query = "SELECT post_title, guid FROM wp_posts WHERE post_author = 
  //  $user_id AND post_status = 'publish' ORDER BY ID DESC LIMIT $number_of_posts";
  $query = "SELECT * FROM `u637985876_mydb`.`bus` WHERE bus_no='$user_id' AND password='$password';";
  $result = mysql_query($query,$link) or die('Errant query:  '.$query);
  /* create one master array of the records */
  $login = array();
  if(mysql_num_rows($result)) {
    
      $login[] = array("status"=>1, "message"=>"login successfully",);
    
  }else{
 $login[] = array("status"=>0, "message"=>"login fail",);
	}
  /* output in necessary format */
  if($format == 'json') {
    header('Content-type: application/json');
    echo json_encode(array('login'=>$login));
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