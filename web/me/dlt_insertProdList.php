<?php
    include('connection.php');
    date_default_timezone_set("Asia/Calcutta");

    if(isset($_POST['name'])){
        $qry = 'insert into prod_tbl (prodEntryTblId, prodId, boxPrice, boxQuantity, prodQuan,
            prodQuanPerBox, unit) values ('. $_POST['prodEntryTblId'] .', '. $_POST['prodId'] .',
            '. $_POST['boxPrice'] .',  '. $_POST['boxQuantity'] .',  '. $_POST['prodQuan'] .',  
            '. $_POST['prodQuanPerBox'] .',  "'. $_POST['unit'] .'");';
        echo mysqli_query($conn, $qry);
    }
?>