<?php
    function getOriginalId($id, $tableName){
        include('connection.php');
        $qry = 'select * from '. $tableName .' where id='. $id;
        $row=mysqli_fetch_assoc(mysqli_query($conn, $qry));
        if(!$row) return null;
        if($row['updatedFrom']==null) {
            return $id;
        }else{
            return getOriginalId($row['updatedFrom'], $tableName);
        }
    }

    function getModifiedId($id, $tableName){
        include('connection.php');
        $qry = 'select * from '. $tableName .' where id='. $id;
        $row=mysqli_fetch_assoc(mysqli_query($conn, $qry));
        if(!$row) return null;
        if($row['updatedTo']==null) {
            return $id;
        }else{
            return getModifiedId($row['updatedTo'], $tableName);
        }
    }
?>