<?php
include 'db_config.php';

// 1. Logika Tambah Tips
if (isset($_POST['add_tip'])) {
    $title = $_POST['title'];
    $content = $_POST['content'];
    $image_url = $_POST['image_url'];
    $category = $_POST['category'];

    $stmt = $conn->prepare("INSERT INTO health_tips (title, content, image_url, category) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssss", $title, $content, $image_url, $category);
    $stmt->execute();
    header("Location: admin_panel.php");
}

// 2. Logika Hapus Tips
if (isset($_GET['delete'])) {
    $id = $_GET['delete'];
    $conn->query("DELETE FROM health_tips WHERE id = $id");
    header("Location: admin_panel.php");
}

// 3. Ambil Data dari Database
$tips = $conn->query("SELECT * FROM health_tips ORDER BY id DESC");
?>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Panel - Tracker Siklus</title>
    <!-- Menggunakan Bootstrap agar tampilan langsung rapi -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #FDF7F8; }
        .navbar { background-color: #FF5C7A; }
        .btn-pink { background-color: #FF5C7A; color: white; border-radius: 20px; }
        .btn-pink:hover { background-color: #e04a68; color: white; }
        .card { border-radius: 15px; border: none; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
    </style>
</head>
<body>

<nav class="navbar navbar-dark mb-4">
    <div class="container">
        <span class="navbar-brand mb-0 h1">Admin Panel - Kelola Tips Kesehatan</span>
    </div>
</nav>

<div class="container">
    <div class="row">
        <!-- Kolom Kiri: Form Input -->
        <div class="col-md-4">
            <div class="card p-4 mb-4">
                <h5 class="mb-4">Tambah Artikel Baru</h5>
                <form method="POST">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Judul Artikel</label>
                        <input type="text" name="title" class="form-control" placeholder="Contoh: Tips Menghadapi Nyeri" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Kategori</label>
                        <select name="category" class="form-select">
                            <option value="Kesehatan">Kesehatan</option>
                            <option value="Nutrisi">Nutrisi</option>
                            <option value="Gaya Hidup">Gaya Hidup</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">URL Gambar (Opsional)</label>
                        <input type="text" name="image_url" class="form-control" placeholder="https://image.url/foto.jpg">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Isi Konten</label>
                        <textarea name="content" class="form-control" rows="6" placeholder="Tulis penjelasan tips di sini..." required></textarea>
                    </div>
                    <button type="submit" name="add_tip" class="btn btn-pink w-100 fw-bold">Simpan ke Aplikasi</button>
                </form>
            </div>
        </div>

        <!-- Kolom Kanan: Daftar Artikel -->
        <div class="col-md-8">
            <div class="card p-4">
                <h5 class="mb-4">Daftar Tips yang Tayang di Aplikasi</h5>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Judul</th>
                                <th>Kategori</th>
                                <th class="text-center">Aksi</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php if ($tips && $tips->num_rows > 0): ?>
                                <?php while($row = $tips->fetch_assoc()): ?>
                                <tr>
                                    <td><?php echo $row['id']; ?></td>
                                    <td><strong><?php echo $row['title']; ?></strong></td>
                                    <td><span class="badge rounded-pill bg-light text-dark border"><?php echo $row['category']; ?></span></td>
                                    <td class="text-center">
                                        <a href="?delete=<?php echo $row['id']; ?>" class="btn btn-outline-danger btn-sm px-3" onclick="return confirm('Hapus artikel ini?')">Hapus</a>
                                    </td>
                                </tr>
                                <?php endwhile; ?>
                            <?php else: ?>
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-4">Belum ada tips kesehatan. Silakan tambah di kolom kiri.</td>
                                </tr>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
