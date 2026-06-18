<?php
error_reporting(0);
include 'db_config.php';
header('Content-Type: application/json');

try {
    $sql = "SELECT * FROM announcements ORDER BY id DESC";
    $result = $conn->query($sql);
    $announcements = [];

    if ($result && $result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $announcements[] = $row;
        }
    }
    echo json_encode($announcements);
} catch (Exception $e) {
    echo json_encode([]);
}
?>
