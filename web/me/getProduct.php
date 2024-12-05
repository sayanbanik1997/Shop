<?php
    include('connection.php');
    if(isset($_POST['name'])){
        $nameArr = (explode(" ",$_POST['name']));
        $qry= 'select * from prod_tbl ';
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
        while($row= mysqli_fetch_assoc($rsltOfQry)){
            if($count!= 0)$rslt=$rslt . ',';
            $rslt=$rslt . '{"id": "'. $row['id'] .'", "name": "'. $row['name'] .'"}';
            $count=$count+1;
        }
        $rslt= $rslt . ']';
        echo $rslt;
    }
?>