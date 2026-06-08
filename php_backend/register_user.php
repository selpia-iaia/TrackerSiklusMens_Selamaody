<?php
include 'db_config.php';
header('Content-Type: application/json');

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($data) {
    $username = $data['username'];
    $email = $data['email'];
    $pass = $data['password_hash'];

    $sql = "INSERT INTO users (username, email, password) VALUES ('$username', '$email', '$pass')";

    if ($conn->query($sql)) {
        echo json_encode(["success" => true, "message" => "User registered", "user_id" => $conn->insert_id]);
    } else {
        echo json_encode(["success" => false, "message" => "Registration failed: " . $conn->error]);
    }
}
?>