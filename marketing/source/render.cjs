// Renders the listing artwork in marketing/images/ from real in-game screenshots and bundled fonts.
// Usage (from the repository root): NODE_PATH="$(npm root -g)" node marketing/source/render.cjs [name...]
// Requires the `playwright` package with a Chromium build available.
const path = require('path');
const fs = require('fs');
const { chromium } = require('playwright');

const root = path.resolve(__dirname, '..', '..');
const out = path.join(root, 'marketing', 'images');
const shots = path.join(out, 'screenshots');
const fonts = path.join(__dirname, 'fonts');
const url = (file) => 'file://' + file;
const shot = (name) => url(path.join(shots, name));
const source = (name) => url(path.join(__dirname, name));

const css = `
@font-face { font-family: Cinzel; src: url('${url(path.join(fonts, 'cinzel_bold.ttf'))}'); }
@font-face { font-family: Garamond; src: url('${url(path.join(fonts, 'eb-garamond.ttf'))}'); }
:root { --text:#ede7da; --dim:#9a9384; --faint:#6b655b; --header:#f5e2a8; --gold:#e3c98a; --gold-dim:#9c8558;
        --ember:#e8743c; --blue:#7faee8; --red:#e06a5a; --bg:#090807; }
* { box-sizing: border-box; }
html, body { margin: 0; background: var(--bg); color: var(--text); font-family: Garamond, serif; }
.stage { position: relative; overflow: hidden; background: radial-gradient(ellipse at 30% 40%, #1d1712 0%, #090807 70%); }
.display { font-family: Cinzel, serif; letter-spacing: .05em; }
.gold { background: linear-gradient(180deg, #fff3cf 0%, #f5e2a8 35%, #e3c98a 65%, #8a6d3b 100%);
        -webkit-background-clip: text; background-clip: text; color: transparent; }
.glow { filter: drop-shadow(0 0 18px rgba(232,116,60,.35)); }
.kicker { font-family: Cinzel, serif; color: var(--gold); letter-spacing: .22em; font-size: 22px; text-transform: uppercase; }
.rule { height: 1px; background: linear-gradient(90deg, rgba(227,201,138,0), rgba(227,201,138,.9) 20%, rgba(227,201,138,.9) 70%, rgba(227,201,138,0)); }
.diamond { display: inline-block; width: 9px; height: 9px; background: var(--gold); transform: rotate(45deg); margin: 0 12px 3px 0; }
.frame { border: 1px solid rgba(227,201,138,.5); box-shadow: 0 0 0 1px rgba(0,0,0,.6), 0 30px 80px rgba(0,0,0,.65), 0 0 60px rgba(232,116,60,.14); overflow: hidden; }
.frame img { display: block; width: 100%; }
.crop { position: relative; overflow: hidden; }
.crop img { position: absolute; display: block; }
canvas.embers { position: absolute; left: 0; top: 0; width: 100%; height: 100%; pointer-events: none; }
.vignette { position: absolute; inset: 0; background: radial-gradient(ellipse at center, rgba(0,0,0,0) 45%, rgba(0,0,0,.78) 100%); }
.backdrop { position: absolute; inset: -40px; background-size: cover; background-position: center; filter: blur(16px) brightness(.4) saturate(1.1); }
.pill { display: inline-block; border: 1px solid rgba(227,201,138,.55); padding: 6px 14px 4px; margin-right: 10px;
        font-family: Cinzel, serif; font-size: 18px; letter-spacing: .12em; color: var(--header); background: rgba(0,0,0,.35); }
.label { position: absolute; font-family: Cinzel, serif; font-size: 20px; letter-spacing: .12em; color: var(--header);
         background: rgba(9,8,7,.86); border: 1px solid rgba(227,201,138,.8); padding: 7px 14px 5px; white-space: nowrap;
         box-shadow: 0 8px 26px rgba(0,0,0,.6); }
ul.points { list-style: none; padding: 0; margin: 0; }
ul.points li { position: relative; padding-left: 26px; margin: 0 0 14px; font-size: 28px; line-height: 1.3; color: var(--text); }
ul.points li::before { content: ''; position: absolute; left: 2px; top: 14px; width: 8px; height: 8px; background: var(--gold); transform: rotate(45deg); }
.k { display: inline-block; border: 1px solid rgba(227,201,138,.6); padding: 0 8px; margin: 0 2px; background: rgba(0,0,0,.35); font-size: .9em; }
b.hi { color: var(--header); font-weight: normal; }
`;

// Drifting embers, the Ember Shrine's ambience. Deterministic so renders are reproducible.
const embers = `<canvas class="embers"></canvas><script>
(() => { const c = document.querySelector('canvas.embers'); const r = c.getBoundingClientRect();
  c.width = r.width; c.height = r.height; const g = c.getContext('2d'); let s = 11;
  const rnd = () => (s = (s * 16807) % 2147483647) / 2147483647;
  for (let i = 0; i < 110; i++) { const x = rnd() * c.width, y = c.height * (.25 + .75 * rnd()), a = .2 + rnd() * .6, z = 1 + rnd() * 2.4;
    const h = g.createRadialGradient(x, y, 0, x, y, z * 5); h.addColorStop(0, 'rgba(240,140,70,' + a * .5 + ')'); h.addColorStop(1, 'rgba(240,140,70,0)');
    g.fillStyle = h; g.beginPath(); g.arc(x, y, z * 5, 0, Math.PI * 2); g.fill();
    g.fillStyle = 'rgba(255,214,150,' + a + ')'; g.fillRect(x - z / 2, y - z / 2, z, z); } })();
</script>`;

const page = (width, height, body, transparent = false) => `<!doctype html><html><head><meta charset="utf-8"><style>${css}
body { width: ${width}px; height: ${height}px; } .stage { width: ${width}px; height: ${height}px; }
${transparent ? 'html, body, .stage { background: transparent; }' : ''}</style></head>
<body><div class="stage">${body}</div></body></html>`;

const header = (title) => page(1400, 120, `
  <div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:28px;background:linear-gradient(90deg,rgba(0,0,0,0),rgba(18,15,12,.9) 30%,rgba(18,15,12,.9) 70%,rgba(0,0,0,0))">
    <div class="rule" style="width:300px;transform:scaleX(-1)"></div>
    <span class="diamond" style="margin:0"></span>
    <div class="display gold glow" style="font-size:46px;white-space:nowrap">${title}</div>
    <span class="diamond" style="margin:0"></span>
    <div class="rule" style="width:300px"></div>
  </div>`, true);

// A screenshot region: x, y, w are percentages of the 1920x1080 capture, shown in a box `width` px wide.
const region = (file, x, y, w, h, width) => {
  const scale = width / (1920 * w / 100);
  const height = Math.round(1080 * h / 100 * scale);
  return `<div class="frame crop" style="width:${width}px;height:${height}px">
    <img src="${shot(file)}" style="width:${1920 * scale}px;left:${-1920 * x / 100 * scale}px;top:${-1080 * y / 100 * scale}px"></div>`;
};

// A feature card: copy on the left, artwork on the right.
const card = ({ kicker, title, points, art, width = 1600, height = 900, artWidth = 980, backdrop = 'shrine.jpg', titleSize = 56 }) => page(width, height, `
  <div class="backdrop" style="background-image:url('${shot(backdrop)}');opacity:.6"></div>
  <div class="vignette"></div>${embers}
  <div style="position:absolute;left:80px;top:0;bottom:0;width:${width - artWidth - 150}px;display:flex;flex-direction:column;justify-content:center">
    <div class="kicker"><span class="diamond"></span>${kicker}</div>
    <div class="display gold glow" style="font-size:${titleSize}px;line-height:1.1;margin:18px 0 22px">${title}</div>
    <div class="rule" style="width:80%;margin-bottom:30px"></div>
    <ul class="points">${points.map((point) => `<li>${point}</li>`).join('')}</ul>
  </div>
  <div style="position:absolute;right:60px;top:50%;transform:translateY(-50%);width:${artWidth}px">${art}</div>`);

const full = (file) => `<div class="frame"><img src="${shot(file)}"></div>`;

const assets = {
  banner: () => page(1800, 600, `
    <div class="backdrop" style="background-image:url('${shot('shrine.jpg')}');filter:blur(6px) brightness(.55) saturate(1.15)"></div>
    <div style="position:absolute;inset:0;background:linear-gradient(90deg,rgba(9,8,7,.95) 0%,rgba(9,8,7,.8) 40%,rgba(9,8,7,.1) 100%)"></div>
    <div class="vignette"></div>${embers}
    <div style="position:absolute;right:-70px;top:66px;width:990px;transform:perspective(1600px) rotateY(-16deg) rotateX(3deg);transform-origin:left center">
      ${full('levelup.jpg')}</div>
    <div style="position:absolute;left:90px;top:92px;width:900px">
      <div class="kicker"><span class="diamond"></span>3.0 · Rise of the Runeborn</div>
      <div class="display gold glow" style="font-size:96px;line-height:1.04;margin:20px 0 24px;white-space:nowrap">Runeborn</div>
      <div class="rule" style="width:640px;margin-bottom:26px"></div>
      <div style="font-size:36px;color:var(--header);line-height:1.3">Rune levels. Seventy-four armaments.<br>Seven Sovereigns.</div>
      <div style="margin-top:36px"><span class="pill">Fabric</span><span class="pill">Forge</span><span class="pill">NeoForge</span>
        <span style="font-size:24px;color:var(--dim);margin-left:8px">1.20.1 · 1.21.1 · 26.2 · 26.3</span></div>
    </div>`),

  icon: () => page(512, 512, `
    <div style="position:absolute;inset:0;background:radial-gradient(circle at 50% 45%,#2e2116 0%,#120d09 55%,#070605 100%)"></div>${embers}
    <div style="position:absolute;left:56px;top:40px;width:400px;height:400px;border-radius:50%;border:3px solid var(--gold);box-shadow:0 0 40px rgba(232,116,60,.4), inset 0 0 40px rgba(227,201,138,.22)"></div>
    <div style="position:absolute;left:78px;top:62px;width:356px;height:356px;border-radius:50%;border:1px solid rgba(227,201,138,.55)"></div>
    ${[[256, 40], [456, 240], [56, 240]].map(([x, y]) => `<div style="position:absolute;left:${x - 12}px;top:${y - 12}px;width:24px;height:24px;background:var(--gold);transform:rotate(45deg);box-shadow:0 0 16px rgba(240,170,90,.8)"></div>`).join('')}
    <div style="position:absolute;left:116px;top:100px;width:280px;height:280px;background:radial-gradient(circle,rgba(240,150,80,.35),rgba(240,150,80,0) 65%)"></div>
    <img src="${source('blade.png')}" style="position:absolute;left:136px;top:110px;width:240px;height:240px;image-rendering:pixelated;filter:drop-shadow(0 10px 24px rgba(0,0,0,.7))">
    <div class="display gold" style="position:absolute;left:0;right:0;top:392px;text-align:center;font-size:54px;letter-spacing:.1em">RUNEBORN</div>`),

  'card-levelup': () => card({
    kicker: 'Runes & levels', title: 'Grow by runes,<br>not experience', titleSize: 48,
    points: ['Every kill pays runes; champions and Sovereigns pay far more', 'Raise eight attributes at an <b class="hi">Ember Shrine</b>',
      'Every stat and your attack power preview <b class="hi">current → after</b>', 'Die, and your runes wait where you fell'],
    art: region('levelup.jpg', 15, 14, 70, 72, 980) }),

  'card-armaments': () => card({
    kicker: '74 armaments · 19 classes', title: 'Seventy-four<br>ways to fight',
    points: ['Five damage types, S–E scaling and attribute requirements', 'Bleed, frost, poison and blight build up with every hit',
      '23 weapon arts on <span class="k">R</span>, paid with FP', '12 sorceries and incantations for staves and seals'],
    art: `<div style="position:relative;height:600px">
      <div style="position:absolute;left:170px;top:0">${region('tooltips.jpg', 1.7, 1, 31.9, 27.6, 640)}</div>
      <div style="position:absolute;left:0;bottom:0">${region('tooltips.jpg', 35, 1, 24.1, 22, 490)}</div>
      <div style="position:absolute;right:0;bottom:0">${region('tooltips.jpg', 68.3, 1, 23.3, 22, 476)}</div></div>` }),

  'card-forge': () => card({
    kicker: 'Reinforce & infuse', title: 'Temper your<br>blade to +25',
    points: ['Tempering stones to <b class="hi">+25</b>, lustrous stones to <b class="hi">+10</b>', '13 affinities reshape damage and scaling',
      'See the new attack power and grades before you pay'],
    art: `<div style="position:relative;height:760px">
      <div style="position:absolute;left:0;top:0">${region('smithing.jpg', 15, 14, 70, 72, 760)}</div>
      <div style="position:absolute;right:0;bottom:0">${region('infuse.jpg', 15, 14, 70, 72, 760)}</div></div>` }),

  'card-shrine': () => card({
    kicker: 'Ember Shrines', title: 'Rest at<br>the shrine',
    points: ['Kindled shrines take a name from the land', 'Rest to heal, refill flasks and bind your respawn',
      'Level up, reinforce, infuse, trade and travel', 'Exchange relics, equip crown shards, seek rebirth'],
    art: `<div style="position:relative;height:700px">
      <div class="frame" style="position:absolute;left:0;top:0;width:82%"><img src="${shot('kindled.jpg')}"></div>
      <div style="position:absolute;right:0;bottom:0">${region('merchant.jpg', 15, 14, 70, 72, 600)}</div></div>` }),

  'card-sovereigns': () => card({
    kicker: 'Seven Sovereigns', title: 'Seven crowns,<br>sundered', titleSize: 48,
    points: ['Craft a sigil from six fragments and a token', 'Every Sovereign has its own abilities and a second phase',
      'Relics become legendary armaments', 'Crown shards grant lasting blessings'], backdrop: 'boss-morgrath.jpg',
    art: `<div style="position:relative;height:720px">
      <div class="frame" style="position:absolute;left:0;top:0;width:70%"><img src="${shot('boss-morgrath.jpg')}"></div>
      <div class="frame" style="position:absolute;right:0;top:170px;width:52%"><img src="${shot('boss-kaelthar-hud.jpg')}"></div>
      <div class="frame" style="position:absolute;left:60px;bottom:0;width:52%"><img src="${shot('boss-sylvara.jpg')}"></div></div>` }),

  'card-ras': () => card({
    kicker: 'Better together', title: 'Runeborn ×<br>RPG Attribute<br>System', titleSize: 46,
    points: ['Shrine level-ups need RPG Attribute System', 'Every rune level also grants <b class="hi">RPG levels</b>',
      'Spend their points in its Level Up screen on <span class="k">K</span>', 'Both HUDs stack cleanly'],
    art: `<div style="position:relative;height:720px">
      <div class="frame" style="position:absolute;left:0;top:0;width:80%"><img src="${shot('ras-screen.jpg')}"></div>
      <div style="position:absolute;right:0;bottom:0">${region('ras-hud.jpg', 0, 0, 46, 22, 560)}</div>
      <div class="label" style="right:20px;bottom:150px">Both HUDs, stacked</div></div>` }),

  'card-codex': () => card({
    kicker: 'The Codex', title: 'Everything<br>explained',
    points: ['Chapters on every system, from runes to Sovereigns', 'Crafting grids for shrines, flasks, stones, whetstones and sigils',
      'Recipes are added to your recipe book', '22 feats in a Runeborn advancement tab'],
    art: region('codex-sovereigns.jpg', 12, 8.5, 76, 79, 980) }),

  'card-hud': () => card({
    kicker: 'A clean HUD', title: 'Read the fight<br>at a glance', titleSize: 48,
    points: ['Bars that grow with your stats', 'Runes, flasks, weapon art and target level', 'Sovereign boss bars and status buildup',
      'Dodge roll on <span class="k">Left Alt</span>'], backdrop: 'boss-morgrath.jpg',
    art: full('boss-morgrath-hud.jpg') }),

  'card-config': () => card({
    kicker: 'Make it yours', title: 'Every number<br>is yours',
    points: ['Rune, level and smithing costs', 'Stamina, PvP and mob level curves', 'Champions, Dusk Rider and shrine travel', 'The RPG Attribute System link'],
    art: `<div class="frame" style="background:rgba(12,10,8,.92);padding:40px 50px;font-family:monospace;font-size:28px;line-height:1.55;color:var(--text)">
      <div style="color:var(--dim)">config/runeborn-server.json</div>
      <pre style="margin:16px 0 0;font-family:inherit">{
  <span style="color:var(--gold)">"runeGainMultiplier"</span>: <span style="color:var(--blue)">1.0</span>,
  <span style="color:var(--gold)">"levelCostMultiplier"</span>: <span style="color:var(--blue)">1.0</span>,
  <span style="color:var(--gold)">"dropRunesOnDeath"</span>: <span style="color:var(--blue)">true</span>,
  <span style="color:var(--gold)">"levelUpRequiresRpgAttributeSystem"</span>: <span style="color:var(--blue)">true</span>,
  <span style="color:var(--gold)">"rpgLevelsPerRuneLevel"</span>: <span style="color:var(--blue)">1</span>,
  <span style="color:var(--gold)">"staminaEnabled"</span>: <span style="color:var(--blue)">true</span>,
  <span style="color:var(--gold)">"blocksPerLevel"</span>: <span style="color:var(--blue)">150</span>,
  <span style="color:var(--gold)">"eliteChance"</span>: <span style="color:var(--blue)">0.04</span>,
  <span style="color:var(--gold)">"shrineFastTravel"</span>: <span style="color:var(--blue)">true</span>
}</pre></div>` }),
};

const headers = { 'header-runes': 'Runes & Levels', 'header-armaments': 'Armaments', 'header-shrines': 'Ember Shrines',
  'header-sovereigns': 'Sovereigns', 'header-ras': 'Better Together', 'header-codex': 'The Codex',
  'header-everything': 'Everything Else', 'header-config': 'Configuration', 'header-compatibility': 'Downloads & Compatibility',
  'header-faq': 'Questions', 'header-gallery': 'Gallery' };
for (const [name, title] of Object.entries(headers)) assets[name] = () => header(title);
assets.divider = () => page(1400, 40, `<div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:18px">
  <div class="rule" style="width:520px;transform:scaleX(-1)"></div><span class="diamond" style="margin:0"></span><div class="rule" style="width:520px"></div></div>`, true);

(async () => {
  const wanted = process.argv.slice(2);
  const browser = await chromium.launch();
  fs.mkdirSync(out, { recursive: true });
  for (const [name, build] of Object.entries(assets)) {
    if (wanted.length && !wanted.includes(name)) continue;
    const html = build();
    const size = html.match(/body \{ width: (\d+)px; height: (\d+)px; \}/);
    const tab = await browser.newPage({ viewport: { width: +size[1], height: +size[2] }, deviceScaleFactor: 1 });
    const file = path.join(__dirname, `.render-${name}.html`);
    fs.writeFileSync(file, html);
    await tab.goto(url(file));
    await tab.evaluate(() => document.fonts.ready);
    await tab.waitForLoadState('networkidle');
    await tab.screenshot({ path: path.join(out, `${name}.png`), omitBackground: name === 'divider' || name.startsWith('header-') });
    fs.unlinkSync(file);
    await tab.close();
    console.log('rendered', name);
  }
  await browser.close();
})().catch((error) => { console.error(error); process.exit(1); });
