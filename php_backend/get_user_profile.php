<?php
include 'db_config.php';
header('Content-Type: application/json');

$user_id = $_GET['user_id'] ?? 1;

$sql = "SELECT * FROM user_profiles WHERE user_id = '$user_id'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    echo json_encode($result->fetch_assoc());
} else {
    echo json_encode(null);
}
?>