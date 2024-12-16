<?php
    include('connection.php');
    $qry=" select e.id as billId, e.dateOfPurchase, e.dateTimeOfEntry, e.purOrSell, 
        e.soldUnsold, e.haveToPay, e.cusId as cusSupId from prod_entry_tbl as e";
        // right join payment_tbl as p on e.id = p.billId";
        //, p.id, p.billId, p.datetime, p.dateOfPayment, p.cusId, p.supId, p.amount 
        if(isset($_POST['billId'])){
                $qry = $qry . " where e.id=". $_POST['billId'];
        }
        $qry = $qry . " order by id desc";
        
    $rslt=mysqli_query($conn, $qry);
    $rsltt='[';
    $count =0;
    while($row = mysqli_fetch_assoc($rslt)){
        $myObj = new stdClass();
        $myObj->dateOfPurchase = $row['dateOfPurchase'];
        $myObj->dateTimeOfEntry = $row['dateTimeOfEntry'];
        $myObj->purOrSell = $row['purOrSell'];
        $myObj->soldUnsold = $row['soldUnsold'];

        $myObj->billId = $row['billId'];
        $myObj->haveToPay=$row['haveToPay'];

        $myObj->cusId=null;
        $myObj->supId=null;
        if($row['purOrSell']==1){
            $myObj->supId=$row['cusSupId'];
            $myObj->supName=mysqli_fetch_assoc(mysqli_query($conn, "select * from supplier_tbl where id=". $row['cusSupId']))['name'];
        }else{
            $myObj->cusId=$row['cusSupId'];
            $myObj->cusName=mysqli_fetch_assoc(mysqli_query($conn, "select * from customerr_tbl where id=". $row['cusSupId']))['name'];
        }
        $myObj->totalAmount="";
        $myObj->due="";
        if($row['billId']!=null){
            $qry='select 
            pl.id as plid, pl.prodEntryTblId, prodId, boxQuan, prodQuan, totalAmount,
            unit, p.name as prodName
            from prod_list_tbl as pl left join prod_tbl as p on pl.prodId = p.id where pl.prodEntryTblId='. $row['billId'];
            $rsltProdList=mysqli_query($conn, $qry);
            $totalAmount=0;
            $prodListRslt='[';
            $countt=0;
            while($rowProdList=mysqli_fetch_assoc($rsltProdList)){
                $totalAmount=$totalAmount+$rowProdList['totalAmount'];
                if($countt!=0){
                    $prodListRslt=$prodListRslt.', ';
                }
                $qry='select * from prod_tbl where id = '. $rowProdList['prodId'];
                $prodName = mysqli_fetch_assoc( mysqli_query($conn, $qry))['name'];
                $prodListRslt=$prodListRslt.'{"plid": "'. $rowProdList['plid'] .'", "prodId": "'. $rowProdList['prodId'].'", "prodName" : "'. $prodName .'", "boxQuan" :"'.$rowProdList['boxQuan'].'", "prodQuan": "'. $rowProdList['prodQuan'].'", "totalAmount" :"'. $rowProdList['totalAmount'].'", "unit": "'. $rowProdList['unit'].'"}';
                $countt=$countt+1;
            }
            $prodListRslt=$prodListRslt.']';
            $myObj->totalAmount = $totalAmount;
            $myObj->prodListDtls=$prodListRslt;

            $qry='select * from payment_tbl where billId='. $row['billId'];
            $rsltPayment=mysqli_query($conn, $qry);
            $paymentRslt='[';
            $count1=0;
            $totalPaid=0;
            while($rowPayment=mysqli_fetch_assoc($rsltPayment)){
                if($count1!=0){
                    $paymentRslt=$paymentRslt.', ';
                }
                $paymentRslt=$paymentRslt.'{"id": "'. $rowPayment['id'] .'",
                "datetime": "'. $rowPayment['datetime'].'", "dateOfPayment" :"'.$rowPayment['dateOfPayment'].'",
                "cusId": "'. $rowPayment['cusId'].'", "supId" :"'. $rowPayment['supId'].'",
                "amount": "'. $rowPayment['amount'].'"}';
                $count1=$count1+1;
                $totalPaid=$totalPaid+$rowPayment['amount'];
            }
            $paymentRslt=$paymentRslt.']';
            $myObj->paymentDtls=$paymentRslt;
            $due = $row['haveToPay'] - $totalPaid;
            $myObj->due= $due;
            //'"' . $due .'"';
        }
        $myObj->cusSupName="";

        if($myObj->cusId!=null){
            $qry='select name from customerr_tbl where id='. $myObj->cusId;
        }else{
            $qry='select name from supplier_tbl where id='. $myObj->supId;
        }
        $myObj->cusSupName = mysqli_fetch_assoc(mysqli_query($conn, $qry))['name'];
        //$myObj->cusSupName;
        //$myObj->amount = $row['amount'];

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