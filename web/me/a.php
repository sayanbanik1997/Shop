<?php
    $url = 'http://localhost/me/insertProd.php';
    $data = ['name' => 'prod12'];
    
    // Prepare POST data
    $options = [
        'http' => [
            'method'  => 'POST',
            'header'  => 'Content-type: application/x-www-form-urlencoded',
            'content' => http_build_query($data),
        ],
    ];
    
    // Create stream context
    $context  = stream_context_create($options);
    
    // Perform POST request
    $response = file_get_contents($url, false, $context);
    
    // Display the response
    echo $response;
    
    
?>