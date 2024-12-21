<?php
    require('connection.php');
    
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
            // $qry='update prod_entry_tbl set cusId='. $cusId .', dateOfPurchase= "'. $_POST['dateOfPurchase'] .'", 
            //     dateTimeOfEntry= "'. date('Y-m-d H:i:s') .'", purOrSell='. $_POST['purOrSell'] .',
            //     soldUnsold='. $_POST['soldUnsold'].', haveToPay= '. $_POST['haveToPay'] .' where
            //     id= '. $_POST['billId'];
            // if ($conn->query($qry) === TRUE) {
            //     $last_id = $conn->insert_id;
            //     $payments = json_decode($_POST['payments']);
            //     for ($x = 0; $x < count($payments); $x++) {
            //         $paymentId =$payments[$x]->id;
            //         $dateOfPayment =$payments[$x]->dateOfPayment;
            //         $cusId =$payments[$x]->cusId;
            //         $supId =$payments[$x]->supId;
            //         $amount =$payments[$x]->amount;
            //         $qry='update payment_tbl set datetime="'. date('Y-m-d H:i:s') .'", 
            //             dateOfPayment="'. $dateOfPayment .'", supId='. $cusId .',
            //             cusId='. $cusId .' where id='. $paymentId;
            //         if(mysqli_query($conn, $qry)==1){
            //             echo $last_id;
            //         }else{
            //             echo "error while inserting payment";
            //         }
            //     }
            // } else {
            //     echo "-1";
            // }
            $getProdEntryInfoQry = 'select * from prod_entry_tbl where id ='. $_POST['billId'];
            $getProdEntryInfoRslt = mysqli_query($conn, $getProdEntryInfoQry);
            $getProdEntryInfoRow = mysqli_fetch_assoc($getProdEntryInfoRslt);
            if(!(
                $cusId == $getProdEntryInfoRow['cusId'] && $_POST['dateOfPurchase']== $getProdEntryInfoRow['dateOfPurchase'] &&
                $_POST['soldUnsold']== $getProdEntryInfoRow['soldUnsold'] && $_POST['haveToPay']== $getProdEntryInfoRow['haveToPay']
            )){
                $qry='insert into prod_entry_tbl(cusId, dateOfPurchase, dateTimeOfEntry, purOrSell
                    , soldUnsold, haveToPay, updatedFrom)values(
                    '. $cusId .', "'. $_POST['dateOfPurchase'] .'", "'. date('Y-m-d H:i:s') .'", '. $_POST['purOrSell'] .',
                    '. $_POST['soldUnsold'].', '. $_POST['haveToPay'] .', '. $_POST['billId'] .')';
                if ($conn->query($qry) === TRUE) {
                    $last_id = $conn->insert_id;
                    $qry = 'update prod_entry_tbl set updatedTo = '. $last_id .' where id= '. $_POST['billId'];
                    if(mysqli_query($conn, $qry)){
                        //echo $last_id;
                        $qryToGetPaymentsQry = 'select * from payment_tbl where billId='. $_POST['billId'] .' and updated is null';
                        $qryToGetPaymentsRslt =mysqli_query($conn, $qryToGetPaymentsQry);
                        $paymentJsonCount=0;
                        $payments = json_decode($_POST['payments']);
                        while($qryToGetPaymentsRow=mysqli_fetch_assoc($conn, $qryToGetPaymentsRslt)){
                            if($paymentJsonCount<count($payments)){
                                if($payments[$paymentJsonCount]->id == $qryToGetPaymentsRow['id']){
                                    
                                }else{
                                    mysqli_query($conn, "update payment_tbl set updated = 0 where id = ". $qryToGetPaymentsRow['id']);
                                }
                            }
                        }
                    }else{
                        echo "error updating prod entry";
                    }
                }else{
                    echo "error inserting prod entry";
                }
            }else{
                echo 1;
            }
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
                //echo "Error: " . $qry . "<br>" . $conn->error;
                echo "-1";
            }
        }        
    }else{
        echo "-2";
    }
?>