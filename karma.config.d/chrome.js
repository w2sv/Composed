// Kotlin/Wasm browser tests run through Karma, whose Chrome launcher expects the
// browser executable to be available via CHROME_BIN. KGP does not reliably detect
// system-installed Chrome/Chromium (for example Snap Chromium), so this config
// resolves a suitable executable from PATH and sets CHROME_BIN automatically.

const fs = require("fs");
const path = require("path");

if (!process.env.CHROME_BIN) {
    const candidates = [
        "google-chrome",
        "google-chrome-stable",
        "chromium",
        "chromium-browser",
    ];

    const directories = (process.env.PATH || "").split(path.delimiter);

    outer:
    for (const candidate of candidates) {
        for (const directory of directories) {
            const executable = path.join(directory, candidate);

            if (fs.existsSync(executable)) {
                process.env.CHROME_BIN = executable;
                break outer;
            }
        }
    }
}
