const http = require('http');
const fs = require('fs');
const path = require('path');
const os = require('os');

const PORT = 3000;
const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.ico': 'image/x-icon'
};

function getLocalIp() {
  const interfaces = os.networkInterfaces();
  for (const name of Object.keys(interfaces)) {
    for (const iface of interfaces[name]) {
      if (iface.family === 'IPv4' && !iface.internal) {
        return iface.address;
      }
    }
  }
  return 'localhost';
}

const server = http.createServer((req, res) => {
  let reqPath = decodeURI(req.url.split('?')[0]);
  if (reqPath === '/') reqPath = '/index.html';

  const filePath = path.join(__dirname, reqPath);

  // Security check: must be inside directory
  if (!filePath.startsWith(__dirname)) {
    res.writeHead(403);
    res.end('Access Denied');
    return;
  }

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('404 Not Found');
      return;
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    res.writeHead(200, {
      'Content-Type': contentType,
      'Cache-Control': 'no-cache, no-store, must-revalidate',
      'Access-Control-Allow-Origin': '*'
    });

    const stream = fs.createReadStream(filePath);
    stream.pipe(res);
  });
});

server.listen(PORT, '0.0.0.0', () => {
  const localIp = getLocalIp();
  console.log('====================================================');
  console.log('  🚀 NusaPersonal Finance Android Server Berjalan!');
  console.log('====================================================');
  console.log(`\n💻 Akses di Komputer ini:`);
  console.log(`   http://localhost:${PORT}`);
  console.log(`\n📱 Akses dari HP Android Anda (Satu Wi-Fi):`);
  console.log(`   http://${localIp}:${PORT}`);
  console.log(`\n📲 Tips Install di HP Android:`);
  console.log(`   1. Buka http://${localIp}:${PORT} di Google Chrome HP.`);
  console.log(`   2. Ketuk ikon titik tiga (⋮) di kanan atas.`);
  console.log(`   3. Pilih "Tambahkan ke Layar Utama" atau "Install Aplikasi".`);
  console.log(`   4. Aplikasi akan terpasang di HP Anda seperti APK Native!`);
  console.log('\nTekan Ctrl + C untuk menghentikan server.');
});
