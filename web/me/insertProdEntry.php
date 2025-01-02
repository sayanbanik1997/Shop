<?php
    include('connection.php');include("getBillId.php");
    
    if(isset($_POST['name']) && isset($_POST['dateOfPurchase']) && isset($_POST['purOrSell'])
        && isset($_POST['soldUnsold']) && isset($_POST['paid']) && isset($_POST['haveToPay']) &&
        isset($_POST['billId'])){
        $qry='';
        if($_POST['purOrSell']==1){
            $qry='select id from supplier_tbl where name ="'. $_POST['name'] .'"';
        }else{
            $qry='select id from customerr_tbl where name ="'. $_POST['name'] .'"';
        }
        $rslt=mysqli_query($conn, $qry);
        
        $cusId = mysqli_fetch_assoc($rslt)['id'];
        if($_POST['billId']!=0){
            $originalBillId = getOriginalBillId($_POST['billId']);
            $modifiedBillId = getModifiedBillId($_POST['billId']);

            $getProdEntryInfoQry = 'select * from prod_entry_tbl where id ='. $modifiedBillId;
            $getProdEntryInfoRslt = mysqli_query($conn, $getProdEntryInfoQry);
            $getProdEntryInfoRow = mysqli_fetch_assoc($getProdEntryInfoRslt);
            if(!(
                $cusId == $getProdEntryInfoRow['cusId'] && $_POST['dateOfPurchase']== $getProdEntryInfoRow['dateOfPurchase'] &&
                $_POST['soldUnsold']== $getProdEntryInfoRow['soldUnsold'] && $_POST['haveToPay']== $getProdEntryInfoRow['haveToPay']
            )){
                $qry='insert into prod_entry_tbl(cusId, dateOfPurchase, dateTimeOfEntry, purOrSell
                    , soldUnsold, haveToPay, updatedFrom)values(
                    '. $cusId .', "'. $_POST['dateOfPurchase'] .'", "'. date('Y-m-d H:i:s') .'", '. $_POST['purOrSell'] .',
                    '. $_POST['soldUnsold'].', '. $_POST['haveToPay'] .', '. $modifiedBillId .')';
                if ($conn->query($qry) === TRUE) {
                    $last_id = $conn->insert_id;
                    $qry = 'update prod_entry_tbl set updatedTo = '. $last_id .' where id= '. $modifiedBillId;
                    if(mysqli_query($conn, $qry)){
                        
                    }else{
                        echo "error updating prod entry";
                        return;
                    }
                }else{
                    echo "error inserting prod entry";return;
                }
            }
            $qryToGetPaymentsQry = 'select * from payment_tbl where billId='. $originalBillId .' and updatedTo is null';
            $qryToGetPaymentsRslt =mysqli_query($conn, $qryToGetPaymentsQry);
            $paymentJsonCount=0;
            $payments = json_decode($_POST['payments']);
            while($qryToGetPaymentsRow=mysqli_fetch_assoc($qryToGetPaymentsRslt)){
                if($paymentJsonCount<count($payments)-1){
                    if($payments[$paymentJsonCount]->id == $qryToGetPaymentsRow['id']){
                        if(!(
                            $payments[$paymentJsonCount]->dateOfPayment == $qryToGetPaymentsRow['dateOfPayment'] &&
                            $payments[$paymentJsonCount]->amount == $qryToGetPaymentsRow['amount']
                        )){
                            $qry = "";
                            if($_POST['purOrSell']==1){
                                $qry='insert into payment_tbl(billId, datetime, dateOfPayment, supId, amount, updatedFrom) values
                                    ('. $originalBillId .',"'. date('Y-m-d H:i:s') .'", "'. 
                                    $payments[$paymentJsonCount]->dateOfPayment
                                    .'", '. $qryToGetPaymentsRow['supId'] .',
                                    '. $payments[$paymentJsonCount]->amount .', '. $qryToGetPaymentsRow['id'] .')'; 
                            }else{
                                $qry='insert into payment_tbl(billId, datetime, dateOfPayment, cusId, amount, updatedFrom) values
                                ('. $originalBillId .',"'. date('Y-m-d H:i:s') .'", "'. 
                                $payments[$paymentJsonCount]->dateOfPayment
                                .'", '. $qryToGetPaymentsRow['cusId'] .',
                                '. $payments[$paymentJsonCount]->amount
                                .', '. $qryToGetPaymentsRow['id'] .')'; 
                            }
                            if($conn->query($qry) === TRUE) {
                                $lastIdOfPayment = $conn->insert_id;
                                $qry = 'update payment_tbl set updatedTo='. $lastIdOfPayment .' where id='. $qryToGetPaymentsRow['id'];
                                mysqli_query($conn, $qry);
                            }else{
                                //echo $qry ;
                                "error7384";
                                return;
                            }
                        }
                        $paymentJsonCount=$paymentJsonCount+1;
                    }else{
                        if(!mysqli_query($conn, "update payment_tbl set updatedTo = 0 where id = ". $qryToGetPaymentsRow['id'])){
                            echo "erorr 29874";return;
                        }
                    }
                }else break;
            }
            if($payments[count($payments)-1]->amount!="0" && $payments[count($payments)-1]->amount!=""){
                if($_POST['purOrSell']==1){
                    $qry='insert into payment_tbl(billId, datetime, dateOfPayment, supId, amount)values
                    ('. $_POST['billId'] .',"'. date('Y-m-d H:i:s') .'", "'. $payments[count($payments)-1]->dateOfPayment .'", '. $cusId .',
                    '. $payments[count($payments)-1]->amount .')'; 
                }else{
                    $qry='insert into payment_tbl(billId, datetime, dateOfPayment, cusId, amount)values
                    ('. $_POST['billId'] .', "'. date('Y-m-d H:i:s') .'", "'. $payments[count($payments)-1]->dateOfPayment .'", '. $cusId .',
                    '. $payments[count($payments)-1]->amount .')'; 
                }
                if(!mysqli_query($conn, $qry)){
                    //echo "error9087";return;
                    echo $qry;return;
                }
            }
            echo $_POST['billId'];
            //echo "5";
        }else{
            $qry='insert into prod_entry_tbl(cusId, dateOfPurchase, dateTimeOfEntry, purOrSell
                , soldUnsold, haveToPay)values(
                '. $cusId .', "'. $_POST['dateOfPurchase'] .'", "'. date('Y-m-d H:i:s') .'", '. $_POST['purOrSell'] .',
                '. $_POST['soldUnsold'].', '. $_POST['haveToPay'] .')';
            if ($conn->query($qry) === TRUE) {
                $last_id = $conn->insert_id;
                if($_POST['purOrSell']==1){
                    $qry='insert into payment_tbl(billId, datetime, dateOfPayment, supId, amount)values
                    ('. $last_id .',"'. date('Y-m-d H:i:s') .'", "'. $_POST['dateOfPurchase'] .'", '. $cusId .',
                    '. $_POST['paid'].')'; 
                }else{
                    $qry='insert into payment_tbl(billId, datetime, dateOfPayment, cusId, amount)values
                    ('. $last_id .', "'. date('Y-m-d H:i:s') .'", "'. $_POST['dateOfPurchase'] .'", '. $cusId .',
                    '. $_POST['paid'].')'; 
                }if(mysqli_query($conn, $qry)==1){
                    echo $last_id;
                }else{
                    echo "-3";
                }
                
            } else {
                echo "-1";
            }
        }        
    }else{
        echo "-2";
    }
?>