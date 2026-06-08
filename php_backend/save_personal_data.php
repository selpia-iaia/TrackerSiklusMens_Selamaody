<?php
include 'db_config.php';
header('Content-Type: application/json');

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($data) {
    $user_id = $data['user_id'];
    $period = $data['period_length'];
    $cycle = $data['cycle_length'];
    $last = $conn->real_escape_string($data['last_period']);

    $sql = "REPLACE INTO personal_data (user_id, period_length, cycle_length, last_period)
            VALUES ('$user_id', '$period', '$cycle', '$last')";

    if ($conn->query($sql)) {
        echo json_encode(["success" => true, "message" => "Personal data saved"]);
    } else {
        echo json_encode(["success" => false, "message" => $conn->error]);
    }
}
?>