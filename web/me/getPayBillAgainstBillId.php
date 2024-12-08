<?php
    include('connection.php');
    if(isset($_POST['billId'])){
        $qry='select * from prod_entry_tbl where id ='. $_POST['billId'];
        $rsltOfQry=mysqli_query($conn, $qry);
        if(!$row=mysqli_fetch_assoc($rsltOfQry)){
            echo "-1";
            return;
        }
        if($row['purOrSell']==1){
            $qry='select * from payment_tbl where supId='. $row['cusId'] .' and billId='. $row['id'];
        }else{
            $qry='select * from payment_tbl where cuSId='. $row['cusId'] .' and billId='. $row['id'];
        }
        $rsltOfQry=mysqli_query($conn, $qry);
        $amount=0;
        while($rowOfPaymentTbl=mysqli_fetch_assoc($rsltOfQry)){
            $amount=$amount+$rowOfPaymentTbl['amount'];
        }
        echo '{"id": "'.$row['cusId'].'","purOrSell":"'.$row['purOrSell'].'","due":"'.($row['haveToPay']-$amount).'"}';
    }
?>