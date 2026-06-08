<?php
include 'db_config.php';
header('Content-Type: application/json');

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($data) {
    $user_id = $data['user_id'];
    $week_start = $data['week_start'];
    $time_format = $data['time_format'];
    $water_target = $data['water_target'];
    $cup_volume = $data['cup_volume'];
    $bmi = $data['is_bmi_enabled'] ? 1 : 0;

    $sql = "REPLACE INTO app_settings (user_id, week_start, time_format, water_target, cup_volume, is_bmi_enabled)
            VALUES ('$user_id', '$week_start', '$time_format', '$water_target', '$cup_volume', '$bmi')";

    if ($conn->query($sql)) {
        echo json_encode(["success" => true, "message" => "Settings saved"]);
    } else {
        echo json_encode(["success" => false, "message" => $conn->error]);
    }
}
?>