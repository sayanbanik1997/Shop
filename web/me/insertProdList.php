<?php
    include('connection.php');include("getBillId.php");
    if(isset($_POST['data'])){
        
        $jsonArr = json_decode($_POST['data']);
        $prodId=0;
        if($_POST['billId']==0){
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
            mysqli_query($conn, $qry);
        }else{
            $originalBillId = getOriginalBillId($_POST['billId']);
            $modifiedBillId = getModifiedBillId($_POST['billId']);
            $qry='select * from prod_list_tbl where prodEntryTblId='. $originalBillId .
                ' and updatedTo is null';
            $rsltOfProdListOfBill=mysqli_query($conn, $qry);
            while($rowOfProdListOfBill=mysqli_fetch_assoc($rsltOfProdListOfBill)){
                for ($x = 0; $x < count($jsonArr); $x++) {
                    if($jsonArr[$x]->id == $rowOfProdListOfBill['id']){
                        $prodEntryTblId = $jsonArr[$x]->prodEntryTblId;
                        $prodName =$jsonArr[$x]->prodName;
                        $prodId= mysqli_fetch_assoc(mysqli_query($conn, "select id from prod_tbl where name='". $prodName ."'"))['id'];
                        $prodQuan =$jsonArr[$x]->prodQuan;
                        $boxQuan =$jsonArr[$x]->boxQuan;
                        $totalAmount =$jsonArr[$x]->totalAmount;
                        $unit =$jsonArr[$x]->unit;
                        
                        if(!($prodId == $rowOfProdListOfBill['prodId'] && $prodQuan == $rowOfProdListOfBill['prodQuan']
                            && $boxQuan == $rowOfProdListOfBill['boxQuan'] && $totalAmount == $rowOfProdListOfBill['totalAmount']
                            && $unit == $rowOfProdListOfBill['unit']
                        )){
                            $qry='insert into prod_list_tbl (prodEntryTblId, prodId, boxQuan, prodQuan, totalAmount,
                                unit, updatedFrom) values ('. $prodEntryTblId .', '. $prodId .', '. $boxQuan .',
                                '. $prodQuan .', '. $totalAmount .', "'. $unit .'", '. $rowOfProdListOfBill['id'] .') ';
                            if ($conn->query($qry) === TRUE) {
                                $last_id = $conn->insert_id;
                                $qry='update prod_list_tbl set updatedTo ='. $last_id .' where id='. $rowOfProdListOfBill['id'];
                                if(mysqli_query($conn, $qry)){
                                   
                                }else{
                                    echo "error";return;
                                }
                            }else{
                                echo "error";return;
                            }
                        }
                        break;
                    }
                }
            }
            $qry='insert into prod_list_tbl (prodEntryTblId, prodId, boxQuan, prodQuan, totalAmount, unit) values ';
            for ($x = 0; $x < count($jsonArr); $x++) {
                if($jsonArr[$x]->id == ""){
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
            }
            mysqli_query($conn, $qry);
        }
        echo "1";
    }
?>