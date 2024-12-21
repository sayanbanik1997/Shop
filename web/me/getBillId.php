<?php
    function getActualBillId($billId){
        include('connection.php');
        $qry = 'select * from prod_entry_tbl where id='. $billId;
        $row=mysqli_fetch_assoc(mysqli_query($conn, $qry));
        if(!$row) return null;
        if($row['updatedFrom']==null) {
            return $billId;
        }else{
            return getActualBillId($row['updatedFrom']);
        }
    }
?>