<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "db_menstruasi";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Gagal koneksi database"]));
}
