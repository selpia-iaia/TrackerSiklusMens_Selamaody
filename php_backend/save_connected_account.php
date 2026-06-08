<?php
include 'db_config.php';
header('Content-Type: application/json');

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($data) {
    $user_id = $data['user_id'];
    $provider = $data['provider'];
    $email = $data['email'];

    $sql = "REPLACE INTO connected_accounts (user_id, provider, provider_id)
            VALUES ('$user_id', '$provider', '$email')";

    if ($conn->query($sql)) {
        echo json_encode(["success" => true, "message" => "Account connected"]);
    } else {
        echo json_encode(["success" => false, "message" => $conn->error]);
    }
}
?>