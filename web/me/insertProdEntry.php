<?php
    require('connection.php');
    
    if(isset($_POST['name']) && isset($_POST['dateOfPurchase']) && isset($_POST['purOrSell'])
        && isset($_POST['soldUnsold']) && isset($_POST['paid']) && isset($_POST['due'])){
        $qry='';
        if($_POST['purOrSell']==1){
            $qry='select id from supplier_tbl where name ="'. $_POST['name'] .'"';
        }else{
            $qry='select id from customerr_tbl where name ="'. $_POST['name'] .'"';
        }
        $rslt=mysqli_query($conn, $qry);
        
        $cusId = mysqli_fetch_assoc($rslt)['id'];
        if(isset($_POST['billId'])){
            $qry='update prod_entry_tbl set cusId='. $cusId .', dateOfPurchase= "'. $_POST['dateOfPurchase'] .'", 
                dateTimeOfEntry= "'. date('Y-m-d H:i:s') .'", purOrSell='. $_POST['purOrSell'] .',
                soldUnsold='. $_POST['soldUnsold'].', due= '. $_POST['due'] .' where
                id= '. $_POST['billId'];
            if ($conn->query($qry) === TRUE) {
                $last_id = $conn->insert_id;
                if($_POST['purOrSell']==1){
                    $qry='update payment_tbl datetime="'. date('Y-m-d H:i:s') .'", 
                    dateOfPayment="'. $_POST['dateOfPurchase'] .'", supId='. $cusId;
                }else{
                    $qry='update payment_tbl datetime= "'. date('Y-m-d H:i:s') .'", 
                    dateOfPayment="'. $_POST['dateOfPurchase'] .'", cusId='. $cusId;
                }if(mysqli_query($conn, $qry)==1){
                    echo $last_id;
                }else{
                    echo "error while inserting payment";
                }
            } else {
                echo "Error: " . $qry . "<br>" . $conn->error;
            }
        }else{
            $qry='insert into prod_entry_tbl(cusId, dateOfPurchase, dateTimeOfEntry, purOrSell
                , soldUnsold, due)values(
                '. $cusId .', "'. $_POST['dateOfPurchase'] .'", "'. date('Y-m-d H:i:s') .'", '. $_POST['purOrSell'] .',
                '. $_POST['soldUnsold'].', '. $_POST['due'] .')';
            if ($conn->query($qry) === TRUE) {
                $last_id = $conn->insert_id;
                if($_POST['purOrSell']==1){
                    $qry='insert into payment_tbl(billId, datetime, dateOfPayment, supId)values
                    ('. $last_id .',"'. date('Y-m-d H:i:s') .'", "'. $_POST['dateOfPurchase'] .'", '. $cusId .')'; 
                }else{
                    $qry='insert into payment_tbl(billId, datetime, dateOfPayment, cusId)values
                    ('. $last_id .', "'. date('Y-m-d H:i:s') .'", "'. $_POST['dateOfPurchase'] .'", '. $cusId .')'; 
                }if(mysqli_query($conn, $qry)==1){
                    echo $last_id;
                }else{
                    echo "error while inserting payment";
                }
                
            } else {
                echo "Error: " . $qry . "<br>" . $conn->error;
            }
        }        
    }
?>