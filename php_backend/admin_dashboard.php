<?php
include 'db_config.php';

// Handle Add Tip
if (isset($_POST['add_tip'])) {
    $title = $_POST['title'];
    $content = $_POST['content'];
    $image_url = $_POST['image_url'];
    $category = $_POST['category'];

    $stmt = $conn->prepare("INSERT INTO health_tips (title, content, image_url, category) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssss", $title, $content, $image_url, $category);
    $stmt->execute();
    header("Location: admin_dashboard.php");
}

// Handle Delete Tip
if (isset($_GET['delete'])) {
    $id = $_GET['delete'];
    $conn->query("DELETE FROM health_tips WHERE id = $id");
    header("Location: admin_dashboard.php");
}

$tips = $conn->query("SELECT * FROM health_tips ORDER BY id DESC");
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
        .btn-pink { background-color: #FF5C7A; color: white; }
        .btn-pink:hover { background-color: #e04a68; color: white; }
    </style>
</head>
<body>

<nav class="navbar navbar-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="#">Admin Dashboard - Tracker Siklus Menstruasi</a>
    </div>
</nav>

<div class="container">
    <div class="row">
        <!-- Form Tambah -->
        <div class="col-md-4">
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-body">
                    <h5 class="card-title mb-4">Tambah Tips Kesehatan</h5>
                    <form method="POST">
                        <div class="mb-3">
                            <label class="form-label">Judul</label>
                            <input type="text" name="title" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Kategori</label>
                            <select name="category" class="form-control">
                                <option value="Kesehatan">Kesehatan</option>
                                <option value="Gaya Hidup">Gaya Hidup</option>
                                <option value="Nutrisi">Nutrisi</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">URL Gambar</label>
                            <input type="text" name="image_url" class="form-control" placeholder="https://...">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Konten</label>
                            <textarea name="content" class="form-control" rows="5" required></textarea>
                        </div>
                        <button type="submit" name="add_tip" class="btn btn-pink w-100">Simpan Tips</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Daftar Tips -->
        <div class="col-md-8">
            <div class="card shadow-sm border-0 text-center mb-4">
                <div class="card-body">
                    <h5 class="card-title text-start mb-4">Daftar Tips Kesehatan</h5>
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Judul</th>
                                    <th>Kategori</th>
                                    <th>Aksi</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php while($row = $tips->fetch_assoc()): ?>
                                <tr>
                                    <td><?php echo $row['id']; ?></td>
                                    <td class="text-start"><?php echo $row['title']; ?></td>
                                    <td><span class="badge bg-info text-dark"><?php echo $row['category']; ?></span></td>
                                    <td>
                                        <a href="?delete=<?php echo $row['id']; ?>" class="btn btn-danger btn-sm" onclick="return confirm('Hapus tips ini?')">Hapus</a>
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
</div>

</body>
</html>
