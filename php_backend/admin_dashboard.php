<?php
include 'db_config.php';

// --- LOGIKA MANAJEMEN TIPS ---
if (isset($_POST['add_tip'])) {
    $title = $_POST['title'];
    $content = $_POST['content'];
    $stmt = $conn->prepare("INSERT INTO health_tips (title, content) VALUES (?, ?)");
    $stmt->bind_param("ss", $title, $content);
    $stmt->execute();
    header("Location: admin_dashboard.php");
}

if (isset($_GET['delete_tip'])) {
    $id = $_GET['delete_tip'];
    $conn->query("DELETE FROM health_tips WHERE id = $id");
    header("Location: admin_dashboard.php");
}

// --- LOGIKA BROADCAST (PENGUMUMAN) ---
if (isset($_POST['send_broadcast'])) {
    $title = $_POST['broadcast_title'];
    $message = $_POST['broadcast_message'];

    // Pastikan tabel 'announcements' sudah dibuat di database
    $stmt = $conn->prepare("INSERT INTO announcements (title, message) VALUES (?, ?)");
    $stmt->bind_param("ss", $title, $message);
    $stmt->execute();
    echo "<script>alert('Pengumuman berhasil dikirim ke aplikasi!');</script>";
}

// --- AMBIL DATA STATISTIK & LIST ---
$userCount = $conn->query("SELECT COUNT(*) as total FROM users")->fetch_assoc()['total'];
$tipsCount = $conn->query("SELECT COUNT(*) as total FROM health_tips")->fetch_assoc()['total'];
$userList = $conn->query("SELECT id, username, email FROM users ORDER BY id DESC");
$tipsList = $conn->query("SELECT * FROM health_tips ORDER BY id DESC");
$announcements = $conn->query("SELECT * FROM announcements ORDER BY id DESC LIMIT 5");
?>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Tracker Siklus</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #FDF7F8; }
        .navbar { background-color: #FF5C7A; }
        .card { border: none; border-radius: 15px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .btn-pink { background-color: #FF5C7A; color: white; border-radius: 10px; }
        .btn-pink:hover { background-color: #e04a68; color: white; }
        .stat-card { background: linear-gradient(45deg, #FF5C7A, #FF8E9E); color: white; }
    </style>
</head>
<body>

<nav class="navbar navbar-dark mb-4 p-3">
    <div class="container">
        <span class="navbar-brand mb-0 h1">🌸 Tracker Siklus Admin</span>
        <a href="admin_dashboard.php" class="btn btn-outline-light btn-sm">Refresh Data</a>
    </div>
</nav>

<div class="container">
    <!-- SEKSI 1: STATISTIK -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card stat-card p-4">
                <h6>Total Pengguna Terdaftar</h6>
                <h2><?php echo $userCount; ?> Orang</h2>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-4 border-start border-4 border-info">
                <h6>Total Artikel Tips</h6>
                <h2><?php echo $tipsCount; ?> Tips</h2>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-4 border-start border-4 border-warning">
                <h6>Status Server</h6>
                <h2 class="text-success">Online</h2>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- SEKSI 2: MANAJEMEN PENGGUNA -->
        <div class="col-md-7">
            <div class="card p-4 mb-4">
                <h5 class="mb-3">Daftar Pengguna Aplikasi</h5>
                <div class="table-responsive" style="max-height: 300px; overflow-y: auto;">
                    <table class="table table-sm table-hover">
                        <thead class="table-light sticky-top">
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Email</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php if ($userList->num_rows > 0): ?>
                                <?php while($u = $userList->fetch_assoc()): ?>
                                <tr>
                                    <td>#<?php echo $u['id']; ?></td>
                                    <td><?php echo $u['username']; ?></td>
                                    <td><?php echo $u['email']; ?></td>
                                </tr>
                                <?php endwhile; ?>
                            <?php else: ?>
                                <tr><td colspan="3" class="text-center">Belum ada pengguna.</td></tr>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- SEKSI 3: BROADCAST (PENGUMUMAN) -->
            <div class="card p-4 mb-4">
                <h5 class="mb-3">Kirim Pengumuman (Broadcast)</h5>
                <form method="POST">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Judul Pesan</label>
                        <input type="text" name="broadcast_title" class="form-control" placeholder="Contoh: Update Fitur Baru!" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Isi Pesan</label>
                        <textarea name="broadcast_message" class="form-control" rows="3" placeholder="Tulis pesan untuk semua pengguna..." required></textarea>
                    </div>
                    <button type="submit" name="send_broadcast" class="btn btn-warning w-100 fw-bold">
                        📢 Kirim ke Semua Pengguna
                    </button>
                </form>

                <hr>
                <h6>Riwayat Broadcast Terakhir:</h6>
                <ul class="list-group list-group-flush small">
                    <?php while($a = $announcements->fetch_assoc()): ?>
                        <li class="list-group-item">
                            <strong><?php echo $a['title']; ?></strong> - <span class="text-muted"><?php echo $a['created_at']; ?></span>
                        </li>
                    <?php endwhile; ?>
                </ul>
            </div>
        </div>

        <!-- SEKSI 4: KELOLA TIPS -->
        <div class="col-md-5">
            <div class="card p-4 mb-4">
                <h5 class="mb-3">Tambah Tips Baru</h5>
                <form method="POST">
                    <div class="mb-2">
                        <label class="small fw-bold">Judul</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="mb-2">
                        <label class="small fw-bold">Konten</label>
                        <textarea name="content" class="form-control" rows="4" required></textarea>
                    </div>
                    <button type="submit" name="add_tip" class="btn btn-pink w-100 fw-bold mt-2">Simpan Tips</button>
                </form>
            </div>

            <div class="card p-4">
                <h5 class="mb-3">Daftar Tips</h5>
                <div class="table-responsive" style="max-height: 200px;">
                    <table class="table table-sm">
                        <tbody>
                            <?php while($t = $tipsList->fetch_assoc()): ?>
                            <tr>
                                <td><small><?php echo $t['title']; ?></small></td>
                                <td class="text-end">
                                    <a href="?delete_tip=<?php echo $t['id']; ?>" class="text-danger" onclick="return confirm('Hapus?')">×</a>
                                </td>
                            </tr>
                            <?php endwhile; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
