const zlib = require('zlib');
const fs = require('fs');
const path = require('path');

function crc32(buf) {
  let table = new Int32Array(256);
  for (let i = 0; i < 256; i++) {
    let c = i;
    for (let k = 0; k < 8; k++) {
      c = (c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1);
    }
    table[i] = c;
  }
  let c = -1;
  for (let i = 0; i < buf.length; i++) {
    c = table[(c ^ buf[i]) & 0xFF] ^ (c >>> 8);
  }
  return (c ^ -1) >>> 0;
}

function makeChunk(type, data) {
  const len = Buffer.alloc(4);
  len.writeUInt32BE(data.length, 0);
  const typeAndData = Buffer.concat([Buffer.from(type, 'ascii'), data]);
  const crc = Buffer.alloc(4);
  crc.writeUInt32BE(crc32(typeAndData), 0);
  return Buffer.concat([len, typeAndData, crc]);
}

function createPng(width, height, pixelFn) {
  const sig = Buffer.from([0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A]);
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(width, 0);
  ihdr.writeUInt32BE(height, 4);
  ihdr[8] = 8;  // bit depth
  ihdr[9] = 6;  // RGBA
  ihdr[10] = 0; // compression
  ihdr[11] = 0; // filter
  ihdr[12] = 0; // interlace

  const rowLen = 1 + width * 4;
  const rawData = Buffer.alloc(height * rowLen);

  for (let y = 0; y < height; y++) {
    const rowOffset = y * rowLen;
    rawData[rowOffset] = 0; // filter none
    for (let x = 0; x < width; x++) {
      const px = rowOffset + 1 + x * 4;
      const [r, g, b, a] = pixelFn(x, y, width, height);
      rawData[px] = r;
      rawData[px + 1] = g;
      rawData[px + 2] = b;
      rawData[px + 3] = a !== undefined ? a : 255;
    }
  }

  const idatData = zlib.deflateSync(rawData, { level: 9 });
  return Buffer.concat([
    sig,
    makeChunk('IHDR', ihdr),
    makeChunk('IDAT', idatData),
    makeChunk('IEND', Buffer.alloc(0))
  ]);
}

// 1. Icon Renderer (Emerald Gradient & Emblem)
function renderAppIcon(x, y, w, h, isMaskable = false) {
  const cx = w / 2;
  const cy = h / 2;
  const dx = x - cx;
  const dy = y - cy;
  const dist = Math.sqrt(dx * dx + dy * dy);

  // Background: Dark Slate gradient
  const bgR = Math.round(15 + (y / h) * 10);
  const bgG = Math.round(23 + (x / w) * 12);
  const bgB = Math.round(42 + ((x + y) / (w * 2)) * 15);

  const radius = isMaskable ? w * 0.40 : w * 0.44;

  // Outer glowing circle
  if (dist < radius + 6 && dist >= radius) {
    return [16, 185, 129, Math.round(200 * (1 - (dist - radius) / 6))];
  }

  if (dist <= radius) {
    // Inside badge: Emerald / Teal Gradient
    const t = (x + y) / (w * 2);
    let r = Math.round(16 * (1 - t) + 5 * t);
    let g = Math.round(185 * (1 - t) + 150 * t);
    let b = Math.round(129 * (1 - t) + 105 * t);

    // Letter 'N' Emblem in center
    const scale = w / 512;
    const nx = (x - cx) / scale;
    const ny = (y - cy) / scale;

    // Check if point is inside N letter
    const inLeftBar = (nx >= -65 && nx <= -35 && ny >= -85 && ny <= 85);
    const inRightBar = (nx >= 35 && nx <= 65 && ny >= -85 && ny <= 85);
    // Diagonal bar from (-45, -85) to (45, 85)
    const diagDist = Math.abs((170 / 80) * nx - ny);
    const inDiag = (diagDist <= 22 && nx >= -45 && nx <= 45 && ny >= -85 && ny <= 85);

    if (inLeftBar || inRightBar || inDiag) {
      return [255, 255, 255, 255]; // Crisp white emblem
    }

    // Chart trend dots above N
    if (Math.abs(ny - (-110)) < 10 && Math.abs(nx) < 70) {
      return [255, 255, 255, 220];
    }

    return [r, g, b, 255];
  }

  return [bgR, bgG, bgB, 255];
}

// 2. Screenshot 1: Dashboard UI Preview
function renderScreenshot1(x, y, w, h) {
  // Dark Background
  let r = 15, g = 23, b = 42;

  // Header Bar (y: 0 - 60)
  if (y < 60) {
    r = 24; g = 33; b = 54;
    // Green logo dot
    if (x >= 25 && x <= 45 && y >= 20 && y <= 40) return [16, 185, 129, 255];
    // Header title line
    if (x >= 55 && x <= 160 && y >= 25 && y <= 35) return [255, 255, 255, 255];
    return [r, g, b, 255];
  }

  // Card 1: Banner Scan Nota (y: 80 - 150, x: 20 - w-20)
  if (y >= 80 && y <= 150 && x >= 20 && x <= w - 20) {
    const t = (x - 20) / (w - 40);
    return [Math.round(5 + 11 * t), Math.round(150 + 35 * t), Math.round(105 + 24 * t), 255];
  }

  // Card 2: Net Worth Hero Card (y: 165 - 280, x: 20 - w-20)
  if (y >= 165 && y <= 280 && x >= 20 && x <= w - 20) {
    // Inside Hero Card
    if (y >= 200 && y <= 230 && x >= 40 && x <= 260) {
      return [16, 185, 129, 255]; // Emerald Big Net Worth text
    }
    return [30, 41, 59, 255];
  }

  // Card 3: Cashflow 3 columns (y: 295 - 390, x: 20 - w-20)
  if (y >= 295 && y <= 390 && x >= 20 && x <= w - 20) {
    return [30, 41, 59, 255];
  }

  // Card 4: Donut Chart area (y: 405 - 570, x: 20 - w-20)
  if (y >= 405 && y <= 570 && x >= 20 && x <= w - 20) {
    const cx = w / 2;
    const cy = 490;
    const dist = Math.sqrt((x - cx) ** 2 + (y - cy) ** 2);
    if (dist >= 45 && dist <= 68) {
      const angle = Math.atan2(y - cy, x - cx);
      if (angle < 0) return [16, 185, 129, 255]; // Green segment
      if (angle < 1.5) return [59, 130, 246, 255]; // Blue segment
      return [245, 158, 11, 255]; // Amber segment
    }
    return [30, 41, 59, 255];
  }

  // Bottom Navigation Bar (y: h - 65 to h)
  if (y >= h - 65) {
    return [15, 23, 42, 255];
  }

  return [r, g, b, 255];
}

// 3. Screenshot 2: Transaction List & Scan
function renderScreenshot2(x, y, w, h) {
  let r = 15, g = 23, b = 42;

  // Header Bar (y: 0 - 60)
  if (y < 60) return [24, 33, 54, 255];

  // Search & Filter Box (y: 80 - 130, x: 20 - w-20)
  if (y >= 80 && y <= 130 && x >= 20 && x <= w - 20) return [30, 41, 59, 255];

  // List of Transactions (y: 145 - 630)
  for (let top = 145; top <= 580; top += 70) {
    if (y >= top && y <= top + 55 && x >= 20 && x <= w - 20) {
      // Date badge on left
      if (x >= 30 && x <= 65 && y >= top + 10 && y <= top + 45) {
        return [15, 23, 42, 255];
      }
      // Green amount on right
      if (x >= w - 120 && x <= w - 35 && y >= top + 15 && y <= top + 35) {
        return [16, 185, 129, 255];
      }
      return [30, 41, 59, 255];
    }
  }

  // Floating Scan Button (bottom right)
  const btnX = w - 60;
  const btnY = h - 100;
  if (Math.sqrt((x - btnX) ** 2 + (y - btnY) ** 2) <= 28) {
    return [16, 185, 129, 255];
  }

  // Bottom Nav
  if (y >= h - 65) return [15, 23, 42, 255];

  return [r, g, b, 255];
}

const iconsDir = path.join(__dirname, 'icons');
if (!fs.existsSync(iconsDir)) fs.mkdirSync(iconsDir, { recursive: true });

console.log('Generating PNG icons for PWA & APK Builder...');

// icon-192.png
const png192 = createPng(192, 192, (x, y, w, h) => renderAppIcon(x, y, w, h, false));
fs.writeFileSync(path.join(iconsDir, 'icon-192.png'), png192);
console.log('✓ icon-192.png (' + png192.length + ' bytes)');

// icon-512.png
const png512 = createPng(512, 512, (x, y, w, h) => renderAppIcon(x, y, w, h, false));
fs.writeFileSync(path.join(iconsDir, 'icon-512.png'), png512);
console.log('✓ icon-512.png (' + png512.length + ' bytes)');

// icon-maskable-512.png
const pngMaskable512 = createPng(512, 512, (x, y, w, h) => renderAppIcon(x, y, w, h, true));
fs.writeFileSync(path.join(iconsDir, 'icon-maskable-512.png'), pngMaskable512);
console.log('✓ icon-maskable-512.png (' + pngMaskable512.length + ' bytes)');

// screenshot1.png (540x720)
const ss1 = createPng(540, 720, renderScreenshot1);
fs.writeFileSync(path.join(iconsDir, 'screenshot1.png'), ss1);
console.log('✓ screenshot1.png (' + ss1.length + ' bytes)');

// screenshot2.png (540x720)
const ss2 = createPng(540, 720, renderScreenshot2);
fs.writeFileSync(path.join(iconsDir, 'screenshot2.png'), ss2);
console.log('✓ screenshot2.png (' + ss2.length + ' bytes)');

console.log('All PWA assets generated successfully!');
