<?php
    include('connection.php');
    date_default_timezone_set("Asia/Calcutta");

    if(isset($_POST['name'])){
        $qry = 'insert into prod_tbl (cusId, dateOfPurchase, datetimeOfEntry) values 
        ("'. $_POST['cusId'] .'", ".' date('Y-m-d') .'", "'. date('Y-m-d H:i:s') .'");';
        echo mysqli_query($conn, $qry);
    }
?>