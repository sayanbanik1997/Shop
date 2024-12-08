<?php
    include('connection.php');
    $qry=" select e.dateOfPurchase, e.dateTimeOfEntry, e.purOrSell, 
        e.soldUnsold, e.haveToPay, p.id, p.billId, p.datetime, p.dateOfPayment, p.cusId, 
        p.supId, p.cusId, p.amount from prod_entry_tbl as e right join payment_tbl as p on e.id = p.billId
        order by id desc";
        
    $rslt=mysqli_query($conn, $qry);
    $rsltt='[';
    $count =0;
    while($row = mysqli_fetch_assoc($rslt)){
        $myObj = new stdClass();
        $myObj->dateOfPurchase = $row['dateOfPurchase'];
        $myObj->dateTimeOfEntry = $row['dateTimeOfEntry'];
        $myObj->purOrSell = $row['purOrSell'];
        $myObj->soldUnsold = $row['soldUnsold'];

        $myObj->paymentId = $row['id'];
        $myObj->billId = $row['billId'];
        $myObj->totalAmount="";
        $myObj->due="";
        if($row['billId']!=null){
            $qry='select sum(totalAmount) from prod_list_tbl where prodEntryTblId='. $row['billId'];
            $totalAmount=mysqli_fetch_assoc(mysqli_query($conn, $qry))['sum(totalAmount)'];
            $myObj->totalAmount = $totalAmount;
            $myObj->due=$totalAmount-$row['haveToPay'];
        }
        $myObj->dateOfPayment = $row['dateOfPayment'];
        $myObj->cusSupName="";

        if($row['cusId']!=null){
            $qry='select name from customerr_tbl where id='. $row['cusId'];
        }else{
            $qry='select name from supplier_tbl where id='. $row['supId'];
        }
        $myObj->cusSupName = mysqli_fetch_assoc(mysqli_query($conn, $qry))['name'];
        $myObj->cusSupName;
        $myObj->amount = $row['amount'];

        if($count>0){
            $rsltt=$rsltt.', ';
        }
        $myJSONString = json_encode($myObj);
        $rsltt=$rsltt. $myJSONString;
        $count=$count+1;
    }
    $rsltt=$rsltt.']';
    echo $rsltt;
?>