const express = require("express");
const multer = require("multer");
const cors = require("cors");
const sharp = require("sharp");
const { v4: uuidv4 } = require("uuid");
const fs = require("fs");
const path = require("path");

const app = express();
app.use(cors());
app.use(express.json({ limit: "25mb" }));

const DATA_DIR = process.env.DATA_DIR || "/data";
fs.mkdirSync(DATA_DIR, { recursive: true });

const upload = multer({ storage: multer.memoryStorage() });

function filePath(id, version = 0) {
  return path.join(DATA_DIR, `${id}_v${version}.png`);
}

app.get("/health", (_, res) => res.json({ ok: true }));

// 1) Upload ny bild: multipart/form-data med fält "file"
app.post("/images", upload.single("file"), async (req, res) => {
  if (!req.file) return res.status(400).json({ error: "file is required" });

  const id = uuidv4();
  const p = filePath(id, 0);

  await sharp(req.file.buffer).png().toFile(p);
  res.status(201).json({ id, version: 0 });
});

// 2) Hämta senaste versionen av en bild
app.get("/images/:id", (req, res) => {
  const id = req.params.id;

  const files = fs.readdirSync(DATA_DIR).filter(f => f.startsWith(`${id}_v`));
  if (files.length === 0) return res.status(404).json({ error: "not found" });

  const latest = files
    .map(f => ({
      f,
      v: parseInt(f.split("_v")[1].replace(".png", ""), 10)
    }))
    .sort((a, b) => b.v - a.v)[0];

  res.setHeader("Content-Type", "image/png");
  fs.createReadStream(path.join(DATA_DIR, latest.f)).pipe(res);
});

/**
 * 3) Annotera: frontend skickar en transparent overlay-PNG som base64.
 * POST /images/:id/annotate
 * body:
 * {
 *   "overlayPngBase64": "<base64 utan data:image/png;base64, prefix>",
 *   "baseVersion": 0
 * }
 */
app.post("/images/:id/annotate", async (req, res) => {
  const id = req.params.id;
  const baseVersion = Number.isInteger(req.body.baseVersion) ? req.body.baseVersion : 0;
  const overlayPngBase64 = req.body.overlayPngBase64;

  if (!overlayPngBase64) {
    return res.status(400).json({ error: "overlayPngBase64 is required" });
  }

  const base = filePath(id, baseVersion);
  if (!fs.existsSync(base)) return res.status(404).json({ error: "base image not found" });

  const overlayBuffer = Buffer.from(overlayPngBase64, "base64");

  const files = fs.readdirSync(DATA_DIR).filter(f => f.startsWith(`${id}_v`));
  const latestVersion = files.length
    ? Math.max(...files.map(f => parseInt(f.split("_v")[1].replace(".png", ""), 10)))
    : baseVersion;

  const newVersion = latestVersion + 1;
  const out = filePath(id, newVersion);

  await sharp(base)
    .composite([{ input: overlayBuffer }])
    .png()
    .toFile(out);

  res.status(201).json({ id, version: newVersion });
});

const port = process.env.PORT || 8090;
app.listen(port, () => console.log(`image-service listening on ${port}`));