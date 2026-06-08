<?php
include 'db_config.php';
header('Content-Type: application/json');

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($data) {
    $user_id = $data['user_id'];
    $provider = $data['provider'];
    $acc = $data['account_number'];
    $holder = $data['holder_name'];

    $sql = "REPLACE INTO payment_methods (user_id, method_name, account_info)
            VALUES ('$user_id', '$provider', '$acc')";

    if ($conn->query($sql)) {
        echo json_encode(["success" => true, "message" => "Payment method saved"]);
    } else {
        echo json_encode(["success" => false, "message" => $conn->error]);
    }
}
?>