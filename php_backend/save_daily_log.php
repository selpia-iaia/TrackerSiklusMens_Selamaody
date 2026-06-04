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
    $date = $conn->real_escape_string($data['log_date']);
    $flow = $conn->real_escape_string($data['flow']);
    $symptoms = $conn->real_escape_string($data['symptoms']);
    $moods = $conn->real_escape_string($data['moods']);
    $medicine = $conn->real_escape_string($data['medicine']);
    $note = $conn->real_escape_string($data['note']);
    $weight = $data['weight_log'] ?? 0;
    $water = $data['water_log'] ?? 0;
    $temp = $data['temp_log'] ?? 0;

    $sql = "REPLACE INTO daily_logs (user_id, log_date, flow, symptoms, moods, medicine, note, weight_log, water_log, temp_log)
            VALUES ('$user_id', '$date', '$flow', '$symptoms', '$moods', '$medicine', '$note', '$weight', '$water', '$temp')";

    if ($conn->query($sql)) {
        $response = ["success" => true, "message" => "Log saved"];
    } else {
        $response = ["success" => false, "message" => $conn->error];
    }
}

ob_end_clean();
echo json_encode($response);
exit;
