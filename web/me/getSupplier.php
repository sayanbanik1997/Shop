<?php
    include('connection.php');
    if(isset($_POST['name'])){
        $nameArr = (explode(" ",$_POST['name']));
        $qry= 'select * from supplier_tbl ';
        for ($x = 0; $x < count($nameArr); $x++) {
            if($x==0){
                $qry=$qry.'where ';
            }else{
                $qry=$qry.'and ';
            }
            $qry=$qry.'name like "%'. $nameArr[$x] .'%" ';
        }

        $rsltOfQry = mysqli_query($conn, $qry);
        $rslt='[';
        $count=0;
        $haveToPay=0;
        $totalPaid=0;
        while($row= mysqli_fetch_assoc($rsltOfQry)){
            if($count!= 0)$rslt=$rslt . ',';
            $qry = 'select * from prod_entry_tbl where cusId='. $row['id'] .' and purOrSell=1';
            $rsltOfprodEntryQry = mysqli_query($conn, $qry);
            while($prodEntryRow=mysqli_fetch_assoc($rsltOfprodEntryQry)){
                $haveToPay=$haveToPay+$prodEntryRow['haveToPay'];
            }
            $qry='select * from payment_tbl where supId='. $row['id'];
            $rsltOfProdListQry = mysqli_query($conn, $qry);
            while($prodListRow=mysqli_fetch_assoc($rsltOfProdListQry)){
                $totalPaid=$totalPaid+$prodListRow['amount'];
            }
            $rslt=$rslt . '{"id": "'. $row['id'] .'", "name": "'. $row['name'] .'"
            , "due": '. ($haveToPay-$totalPaid) .'}';
            $count=$count+1;
            $haveToPay=0;
            $totalPaid=0;
        }
        $rslt= $rslt . ']';
        echo $rslt;
    }
?>