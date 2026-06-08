<?php
include 'db_config.php';
header('Content-Type: application/json');

$sql = "SELECT * FROM health_tips";
$result = $conn->query($sql);
$data = [];

while($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);
?>