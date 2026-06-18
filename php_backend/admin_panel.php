<?php
include 'db_config.php';

// --- LOGIKA MANAJEMEN TIPS ---
if (isset($_POST['add_tip'])) {
    $title = $_POST['title'];
    $content = $_POST['content'];

    $stmt = $conn->prepare("INSERT INTO health_tips (title, content) VALUES (?, ?)");
    $stmt->bind_param("ss", $title, $content);

    if ($stmt->execute()) {
        header("Location: admin_panel.php?success=1");
    } else {
        echo "Error: " . $stmt->error;
    }
}
    } else {
        echo "Error: " . $stmt->error;
    }
}

if (isset($_GET['delete_tip'])) {
    $id = $_GET['delete_tip'];
    $conn->query("DELETE FROM health_tips WHERE id = $id");
    header("Location: admin_panel.php");
}

// --- LOGIKA BROADCAST (PENGUMUMAN) ---
if (isset($_POST['send_broadcast'])) {
    $title = $_POST['broadcast_title'];
    $message = $_POST['broadcast_message'];
    $stmt = $conn->prepare("INSERT INTO announcements (title, message) VALUES (?, ?)");
    $stmt->bind_param("ss", $title, $message);
    $stmt->execute();
    echo "<script>alert('Pengumuman berhasil dikirim!'); window.location='admin_panel.php';</script>";
}

// --- AMBIL DATA STATISTIK & LIST ---
$userCountResult = $conn->query("SELECT COUNT(*) as total FROM users");
$userCount = ($userCountResult) ? $userCountResult->fetch_assoc()['total'] : 0;

$tipsCountResult = $conn->query("SELECT COUNT(*) as total FROM health_tips");
$tipsCount = ($tipsCountResult) ? $tipsCountResult->fetch_assoc()['total'] : 0;

$userList = $conn->query("SELECT id, username, email FROM users ORDER BY id DESC LIMIT 10");
$tipsList = $conn->query("SELECT * FROM health_tips ORDER BY id DESC");
?>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Admin Panel - Tracker Siklus</title>
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
        <span class="navbar-brand mb-0 h1">🌸 Admin Panel - Tracker Siklus</span>
    </div>
</nav>

<div class="container">
    <!-- STATISTIK -->
    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card stat-card p-4">
                <h6>Total Pengguna</h6>
                <h2><?php echo $userCount; ?> Orang</h2>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card p-4 border-start border-4 border-info">
                <h6>Total Tips Kesehatan</h6>
                <h2><?php echo $tipsCount; ?> Artikel</h2>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- PENGGUNA & BROADCAST -->
        <div class="col-md-7">
            <div class="card p-4 mb-4">
                <h5>Daftar Pengguna Baru</h5>
                <div class="table-responsive">
                    <table class="table table-sm table-hover">
                        <thead><tr><th>Username</th><th>Email</th></tr></thead>
                        <tbody>
                            <?php if ($userList && $userList->num_rows > 0): ?>
                                <?php while($u = $userList->fetch_assoc()): ?>
                                <tr><td><?php echo $u['username']; ?></td><td><?php echo $u['email']; ?></td></tr>
                                <?php endwhile; ?>
                            <?php else: ?>
                                <tr><td colspan="2" class="text-center">Belum ada pengguna.</td></tr>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="card p-4">
                <h5>📢 Kirim Broadcast</h5>
                <p class="small text-muted">Gunakan <b>{name}</b> untuk menyebut nama user secara otomatis.</p>
                <form method="POST">
                    <input type="text" name="broadcast_title" class="form-control mb-2" placeholder="Judul Pengumuman (Contoh: Halo {name}!)" required>
                    <textarea name="broadcast_message" class="form-control mb-2" placeholder="Isi Pesan (Contoh: Apa kabar {name}?)" rows="3" required></textarea>
                    <button type="submit" name="send_broadcast" class="btn btn-warning w-100 fw-bold">Kirim Sekarang</button>
                </form>
            </div>
        </div>

        <!-- KELOLA TIPS -->
        <div class="col-md-5">
            <div class="card p-4">
                <h5>Tambah Tips</h5>
                <form method="POST">
                    <input type="text" name="title" class="form-control mb-2" placeholder="Judul" required>
                    <textarea name="content" class="form-control mb-2" placeholder="Isi" rows="4" required></textarea>
                    <button type="submit" name="add_tip" class="btn btn-pink w-100 fw-bold">Simpan</button>
                </form>
                <hr>
                <div style="max-height: 400px; overflow-y: auto;">
                    <?php if ($tipsList && $tipsList->num_rows > 0): ?>
                        <?php while($t = $tipsList->fetch_assoc()): ?>
                        <div class="mb-3 p-2 border-bottom">
                            <div class="d-flex justify-content-between align-items-start">
                                <h6 class="mb-1 fw-bold"><?php echo $t['title']; ?></h6>
                                <a href="?delete_tip=<?php echo $t['id']; ?>" class="btn btn-sm btn-outline-danger border-0" onclick="return confirm('Hapus?')">×</a>
                            </div>
                            <p class="small text-muted mb-0"><?php echo substr($t['content'], 0, 100); ?>...</p>
                        </div>
                        <?php endwhile; ?>
                    <?php endif; ?>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
