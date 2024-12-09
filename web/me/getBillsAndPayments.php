<?php
    include('connection.php');
    $qry=" select e.dateOfPurchase, e.dateTimeOfEntry, e.purOrSell, 
        e.soldUnsold, e.haveToPay, p.id, p.billId, p.datetime, p.dateOfPayment, p.cusId, 
        p.supId, p.amount from prod_entry_tbl as e right join payment_tbl as p on e.id = p.billId";
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

        $myObj->paymentId = $row['id'];
        $myObj->billId = $row['billId'];
        $myObj->haveToPay=$row['haveToPay'];
        $myObj->cusId=$row['cusId'];
        $myObj->supId=$row['supId'];
        $myObj->totalAmount="";
        $myObj->due="";
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

            $qry='select * from payment_tbl where billId='. $row['billId'];
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

            $myObj->due=$row['haveToPay'] - $totalPaid;
        }
        $myObj->cusSupName="";

        if($row['cusId']!=null){
            $qry='select name from customerr_tbl where id='. $row['cusId'];
        }else{
            $qry='select name from supplier_tbl where id='. $row['supId'];
        }
        $myObj->cusSupName = mysqli_fetch_assoc(mysqli_query($conn, $qry))['name'];
        //$myObj->cusSupName;
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