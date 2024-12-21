

<?php 
    if(isset($_POST['billId'])){
        include('getBillId.php'); 
        echo getActualBillId($_POST['billId']);
    }
?>

<form action="http://192.168.206.212/me/a.php", method="POST">
    Name: <input type="text" name="billId"><br>
    <input type="submit">
</form> 
