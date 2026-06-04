<?php
error_reporting(0);
ini_set('display_errors', 0);
ob_start();

include 'db_config.php';

header('Content-Type: application/json');

$json = file_get_contents('php://input');
$data = json_decode($json, true);

$response = ["success" => false, "message" => "No data"];

if ($data) {
    $user_id = $data['user_id'];
    $start = $conn->real_escape_string($data['start_date']);
    $end = $conn->real_escape_string($data['end_date']);

    $sql = "REPLACE INTO periods (user_id, start_date, end_date) VALUES ('$user_id', '$start', '$end')";

    if ($conn->query($sql)) {
        $response = ["success" => true, "message" => "Period saved"];
    } else {
        $response = ["success" => false, "message" => $conn->error];
    }
}

ob_end_clean();
echo json_encode($response);
exit;
