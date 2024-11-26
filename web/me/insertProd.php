<?php
    include('connection.php');

    if(isset($_POST['name'])){
        $qry = 'insert into prod_tbl (name, datetime) values ("'. $_POST['name'] .'", 
            "'. date('Y-m-d H:i:s') .'");';
        echo mysqli_query($conn, $qry);
    }
?>