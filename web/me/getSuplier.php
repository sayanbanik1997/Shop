<?php
    include('connection.php');
    if(isset($_POST['name'])){
        $qry= 'select * from suplier_tbl where name like "%'. $_POST['name'] .'%"';
        $rsltOfQry = mysqli_query($conn, $qry)
        $rslt='';
        $count=0;
        while($row= mysqli_fetch_assoc($rsltOfQry)){
            if($count==0)$rslt=$rslt . '[';
            else $rslt=$rslt . ',';
            $rslt=$rslt . '{"id": "'. $row['id'] .'", "name": "'. $row['name'] .'"}';
            $count=$count+1;
        }
        if($count>0) rslt= $rslt . ']';
        echo $rslt;
    }
?>