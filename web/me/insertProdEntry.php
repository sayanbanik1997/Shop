<?php
    require('connection.php');
    
    if(isset($_POST['name']) && isset($_POST['dateOfPurchase'])){
        $qry='select id from supplier_tbl where name ="'. $_POST['name'] .'"';
        $rslt=mysqli_query($conn, $qry);
        
        $cusId = mysqli_fetch_assoc($rslt)['id'];

        $qry='insert into prod_entry_tbl(cusId, dateOfPurchase, dateTimeOfEntry)values(
            '. $cusId .', "'. $_POST['dateOfPurchase'] .'", "'. date('Y-m-d H:i:s') .'")
            ';
        // $rslt=mysqli_query($conn, $qry);
        // if($rslt>0){
        //     $qry= "select id from prod_entry_tbl ";
        //     $rslt=mysqli_query($conn, $qry);
        // }

        if ($conn->query($qry) === TRUE) {
            $last_id = $conn->insert_id;
            echo $last_id;
        } else {
            echo "Error: " . $qry . "<br>" . $conn->error;
        }
    }
?>