<?php
    include("connection.php");
    $cusSupIdTag="supId";
    $cusSupId=0;
    if(isset($_POST['cusId'])){
        $cusSupIdTag="cusId";
        $cusSupId = $_POST['cusId'];
    }else{
        $cusSupId = $_POST['supId'];
    }
    $qry="INSERT INTO `payment_tbl` ( `billId`, `datetime`, `dateOfPayment`, `supId`, `amount`) VALUES 
        ('". $_POST['billId']. "', '". date('Y-m-d H:i:s') ."', '". $_POST['dateOfPayment'] ."', '". $cusSupId ."', '". $_POST['amount']."')";
    if((int)$_POST['billId']==0){
        $qry="INSERT INTO `payment_tbl` ( `datetime`, `dateOfPayment`, `supId`, `amount`) VALUES 
        ('". date('Y-m-d H:i:s') ."', '". $_POST['dateOfPayment'] ."', '". $cusSupId ."', '". $_POST['amount']."')";
    }
    echo mysqli_query($conn, $qry);
?>