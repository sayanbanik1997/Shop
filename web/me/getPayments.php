<?php
    include('connection.php');include('getActualId.php');
    $qry=" select e.id as billId, e.dateOfPurchase, e.dateTimeOfEntry, e.purOrSell, 
        e.soldUnsold, e.haveToPay, p.id, p.billId, p.datetime, p.dateOfPayment, p.cusId, 
        p.supId, p.amount from prod_entry_tbl as e right join payment_tbl as p on e.id = p.billId 
        
        where";
        if(isset($_POST['billId'])){
                $qry = $qry . " e.id=". $_POST['billId'] ." and " ;
        }
        $qry = $qry . " ( e.updatedTo != 0 and p.updatedTo is null) order by id desc";
        
    $rslt=mysqli_query($conn, $qry);
    $rsltt='[';
    $count =0;
    while($row = mysqli_fetch_assoc($rslt)){
        $myObj = new stdClass();
        // thik korte hobe 
        $originalBillId=getOriginalId($row['billId'], "prod_entry_tbl");
        $modifiedBillId=getModifiedId($row['billId'], "prod_entry_tbl");
        
        $qry = 'select * from prod_entry_tbl where id='. $modifiedBillId;
        $rowOfBillInfo=mysqli_fetch_assoc(mysqli_query($conn, $qry));

        $myObj->dateOfPurchase = $rowOfBillInfo['dateOfPurchase'];
        $myObj->dateTimeOfEntryOfBill = $rowOfBillInfo['dateTimeOfEntry'];

        $myObj->dateTimeOfPaymentEntry = $row['datetime'];
        $myObj->dateOfPayment = $row['dateOfPayment'];


        $myObj->purOrSell = $rowOfBillInfo['purOrSell'];
        $myObj->soldUnsold = $rowOfBillInfo['soldUnsold'];

        $myObj->paymentId = $row['id'];
        $myObj->billId = $row['billId'];
        $myObj->haveToPay=$rowOfBillInfo['haveToPay'];
        $myObj->cusId=$row['cusId'];
        $myObj->supId=$row['supId'];
        $myObj->totalAmount="";
        if($row['billId']!=null){
            $qry='select * from prod_list_tbl where prodEntryTblId='. $row['billId'];
            $rsltProdList=mysqli_query($conn, $qry);
            $totalAmount=0;
            $prodListRslt='[';
            $countt=0;
            while($rowProdList=mysqli_fetch_assoc($rsltProdList)){
                $totalAmount=$totalAmount+$rowProdList['totalAmount'];
                if($countt!=0){
                    $prodListRslt=$prodListRslt.', ';
                }
                $prodListRslt=$prodListRslt.'"id": "'. $rowProdList['id'] .'",
                "prodId": "'. $rowProdList['prodId'].'", "boxQuan" :"'.$rowProdList['boxQuan'].'",
                "prodQuan": "'. $rowProdList['prodQuan'].'", "totalAmount" :"'. $rowProdList['totalAmount'].'",
                "unit": "'. $rowProdList['unit'].'"}';
                $countt=$countt+1;
            }
            $prodListRslt=$prodListRslt.']';
            $myObj->totalAmount = $totalAmount;
            //$myObj->prodListDtls=$prodListRslt;

            $qry='select * from payment_tbl where billId='. $row['billId'] .' and updatedTo is null';
            $rsltPayment=mysqli_query($conn, $qry);
            $paymentRslt='[';
            $count1=0;
            $totalPaid=0;
            while($rowPayment=mysqli_fetch_assoc($rsltPayment)){
                if($count1!=0){
                    $paymentRslt=$paymentRslt.', ';
                }
                $paymentRslt=$paymentRslt.'"id": "'. $rowPayment['id'] .'",
                "datetime": "'. $rowPayment['datetime'].'", "dateOfPayment" :"'.$rowPayment['dateOfPayment'].'",
                "cusId": "'. $rowPayment['cusId'].'", "supId" :"'. $rowPayment['supId'].'",
                "amount": "'. $rowPayment['amount'].'"}';
                $count1=$count1+1;
                $totalPaid=$totalPaid+$rowPayment['amount'];
            }
            $paymentRslt=$paymentRslt.']';
            //$myObj->paymentDtls=$paymentRslt;
            $myObj->dueOfThisBill=$row['haveToPay'] - $totalPaid;
        }
        $qryOfBillsOfThisCusOrSup='';$qryOfPaymentsOfThisCusOrSup='';
        if($rowOfBillInfo['purOrSell']==1){
            $qryOfBillsOfThisCusOrSup = 'select * from prod_entry_tbl where supId='. $row['supId'] .' 
                and updatedTo is null';
            $qryOfPaymentsOfThisCusOrSup ='select * from payment_tbl where supId='. $row['supId'] .' 
                and updatedTo is null';
        }else{
            $qryOfBillsOfThisCusOrSup = 'select * from prod_entry_tbl where cusId='. $row['cusId'] .' 
                and updatedTo is null';
            $qryOfPaymentsOfThisCusOrSup ='select * from payment_tbl where cusId='. $row['cusId'] .' 
                and updatedTo is null';
        }
        $totalHaveToPayToThisCusOrSup=0;
        $rsltOfBillsOfThisCusOrSup = mysqli_query($conn, $qryOfBillsOfThisCusOrSup);
        while($rowOfBillsOfThisCusOrSup = mysqli_fetch_assoc($rsltOfBillsOfThisCusOrSup)){
            $totalHaveToPayToThisCusOrSup = $totalHaveToPayToThisCusOrSup+$rowOfBillsOfThisCusOrSup['haveToPay'];
        }
        $myObj->totalHaveToPayToThisCusOrSup = $totalHaveToPayToThisCusOrSup;

        $totalPaidToThisCusOrSup=0;
        $rsltOfPaymentsOfThisCusOrSup = mysqli_query($conn, $qryOfPaymentsOfThisCusOrSup);
        while($rowOfPaymentsOfThisCusOrSup = mysqli_fetch_assoc($rsltOfPaymentsOfThisCusOrSup)){
            $totalPaidToThisCusOrSup = $totalPaidToThisCusOrSup+$rowOfPaymentsOfThisCusOrSup['amount'];
        }
        $myObj->totalPaidToThisCusOrSup = $totalPaidToThisCusOrSup;

        $myObj->cusSupName="";
        if($row['cusId']!=null){
            $qry='select name from customerr_tbl where id='. $row['cusId'];
        }else{
            $qry='select name from supplier_tbl where id='. $row['supId'];
        }
        $myObj->cusSupName = mysqli_fetch_assoc(mysqli_query($conn, $qry))['name'];
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