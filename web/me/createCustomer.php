<?php
    include("connection.php");
    if(isset($_POST['name'])){
        $qry="select * from customerr_tbl where name='". $_POST['name'] ."'";
        $rslt=mysqli_query($conn, $qry);
        $count=0;
        while($row=mysqli_fetch_assoc($rslt)){
            $count=1;
            break;
        }
        if($count==0){
            $qry="insert into customerr_tbl(name, datetime) values (
            '". $_POST['name'] ."', '". date('Y-m-d H:i:s') ."')";
            $rsltt= mysqli_query($conn, $qry);
            if($rsltt==1) echo "1";
            else echo "-2";
        }else{
            echo "0";
        }

    }else{
        echo "-1";
    }
?>