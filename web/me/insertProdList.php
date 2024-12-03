<?php
    include('connection.php');
    if(isset($_POST['data'])){
        $jsonArr = json_decode($_POST['data']);
        $prodId=0;
        $qry='insert into prod_list_tbl (prodEntryTblId, prodId, boxQuan, prodQuan, totalAmount, unit) values ';
        for ($x = 0; $x < count($jsonArr); $x++) {
            $prodEntryTblId =$jsonArr[$x]->prodEntryTblId;
            $prodName =$jsonArr[$x]->prodName;
            $prodId= mysqli_fetch_assoc(mysqli_query($conn, "select id from prod_tbl where name='". $prodName ."'"))['id'];
            $prodQuan =$jsonArr[$x]->prodQuan;
            $boxQuan =$jsonArr[$x]->boxQuan;
            $totalAmount =$jsonArr[$x]->totalAmount;
            $unit =$jsonArr[$x]->unit;

            $qry = $qry . '('. $prodEntryTblId .', '. $prodId .', '. $boxQuan .',
                '. $prodQuan .', '. $totalAmount .', "'. $unit .'") ';
            if($x < (count($jsonArr)-1)){
                $qry = $qry . ', ';
            }
        }
        //echo $qry;
        echo mysqli_query($conn, $qry);
    }
?>