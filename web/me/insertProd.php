<?php
    include('connection.php');
    date_default_timezone_set("Asia/Calcutta");

    if(isset($_POST['name'])){
        $qry = 'insert into prod_tbl (name, datetime) values ("'. $_POST['name'] .'", 
            "'. date('Y-m-d H:i:s') .'");';
        echo mysqli_query($conn, $qry);
    }
?>