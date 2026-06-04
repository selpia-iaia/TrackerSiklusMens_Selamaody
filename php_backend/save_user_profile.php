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
    $name = $conn->real_escape_string($data['name']);
    $birthday = $conn->real_escape_string($data['birthday']);
    $weight = $data['weight'];
    $height = $data['height'];
    $period = $data['period_length'];
    $cycle = $data['cycle_length'];
    $last = $conn->real_escape_string($data['last_period']);

    $sql = "REPLACE INTO user_profile (user_id, name, birthday, weight, height, period_length, cycle_length, last_period)
            VALUES ('$user_id', '$name', '$birthday', '$weight', '$height', '$period', '$cycle', '$last')";

    if ($conn->query($sql)) {
        $response = ["success" => true, "message" => "Profile updated"];
    } else {
        $response = ["success" => false, "message" => $conn->error];
    }
}

ob_end_clean();
echo json_encode($response);
exit;
