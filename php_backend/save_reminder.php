<?php
include 'db_config.php';
header('Content-Type: application/json');

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($data) {
    $user_id = $data['user_id'];
    $type = $data['reminder_type'];
    $time = $data['reminder_time'];
    $enabled = $data['is_enabled'] ? 1 : 0;

    $sql = "REPLACE INTO reminders (user_id, reminder_type, reminder_time, is_active)
            VALUES ('$user_id', '$type', '$time', '$enabled')";

    if ($conn->query($sql)) {
        echo json_encode(["success" => true, "message" => "Reminder saved"]);
    } else {
        echo json_encode(["success" => false, "message" => $conn->error]);
    }
}
?>