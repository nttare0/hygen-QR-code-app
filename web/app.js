/* ==========================================================================
   QuickPay QR — redesign preview
   Honest implementation: every state the UI shows is a state the system has.
   - Money is integer minor units (halalas). Never floats.
   - QR is genuinely decoded (jsQR) from camera or an on-screen test code.
   - Safety fails CLOSED: unverified merchants need an explicit guardian
     override; block-listed merchants can never be paid.
   - Payments are gated on a real platform authenticator (WebAuthn) when the
     device has one; otherwise an explicitly-labelled demo passcode.
   - One payment per sheet session: the pay button disables while in flight.
   - Wallet, settings and history persist across reloads (localStorage).
   ========================================================================== */
(() => {
'use strict';

/* ---------------- tiny helpers ---------------- */
const $  = (s, r) => (r || document).querySelector(s);
const $$ = (s, r) => Array.from((r || document).querySelectorAll(s));
const esc = (s) => String(s).replace(/[&<>"']/g, (c) => (
  { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const icon = (id, cls) => '<svg class="' + (cls || '') + '" aria-hidden="true"><use href="#' + id + '"></use></svg>';
const uid = () => Math.random().toString(36).slice(2, 10);
const SAR_FMT = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'SAR', minimumFractionDigits: 2, maximumFractionDigits: 2 });
const fmt = (halalas) => SAR_FMT.format(halalas / 100).replace('SAR', 'SAR');
const fmtNum = (halalas) => (halalas / 100).toFixed(2);
const parseSar = (str) => {
  const n = Number(String(str).replace(/[^\d.]/g, ''));
  if (!isFinite(n) || n < 0) return NaN;
  return Math.round(n * 100);
};
const dayKey = (ts) => new Date(ts).toDateString();
const clamp = (n, a, b) => Math.min(b, Math.max(a, n));
const snap25 = (h) => clamp(Math.round(h / 2500) * 2500, 2500, 1000000); // 25 SAR steps

/* ---------------- state ---------------- */
const LS_KEY = 'qpqr.v1';
const QR_PREFIX = 'QUICKPAYV1';

function seed() {
  return {
    accounts: [
      { id: 'acc_1', bankName: 'TechnoBank Saudi', cardType: 'Visa Debit', last4: '8091', balance: 80466000, perTxLimit: 250000, isFrozen: false, isPrimary: true,  gradient: 0 },
      { id: 'acc_2', bankName: 'Reserve Digital',  cardType: 'Credit',     last4: '4719', balance: 2540000,  perTxLimit: 150000, isFrozen: false, isPrimary: false, gradient: 2 },
      { id: 'acc_3', bankName: 'Youth Savings',    cardType: 'Debit',      last4: '0173', balance: 682070,   perTxLimit: 25000,  isFrozen: false, isPrimary: false, gradient: 1 }
    ],
    settings: {
      biometricsRequired: true,
      haptics: true,
      theme: 'auto',
      dailyCap: 20000,        // SAR 200 per day, cumulative
      vaultCap: 50000,        // SAR 500 per transaction, global
      trustedMerchants: ['School Cafeteria', 'University Bookstore'],
      blockedMerchants: ['Phishing Test Merchant']
    },
    transactions: [],
    balanceHidden: false
  };
}
function load() {
  try {
    const raw = localStorage.getItem(LS_KEY);
    if (raw) {
      const s = JSON.parse(raw);
      if (s && Array.isArray(s.accounts) && s.settings && Array.isArray(s.transactions)) return s;
    }
  } catch (e) { /* corrupted storage -> reseed */ }
  return seed();
}
let state = load();
const save = () => { try { localStorage.setItem(LS_KEY, JSON.stringify(state)); } catch (e) {} };

/* non-persisted session */
const session = {
  tab: 'scan',
  testqrOpen: false,
  sheet: null,            // {merchant, category, halalas, source, override, processing, biometricNote}
  camera: { on: false, stream: null, timer: null, torch: false, cooldownUntil: 0 },
  search: '',
  filter: 'all',
  scanStatus: { kind: 'idle', text: 'Decoder idle — no camera, no code in view' }
};

const DEMO = [
  { merchant: 'School Cafeteria',   category: 'Food & Snacks',       halalas: 1500,   note: 'trusted list' },
  { merchant: 'University Bookstore', category: 'Books & Supplies',  halalas: 3250,   note: 'trusted list' },
  { merchant: 'Riverside Arcade',   category: 'Games',               halalas: 26000,  note: 'unverified + over daily cap' }
];

/* ---------------- haptics / theme / motion ---------------- */
function buzz(pattern) { if (state.settings.haptics && navigator.vibrate) { try { navigator.vibrate(pattern); } catch (e) {} } }

const mqDark = window.matchMedia('(prefers-color-scheme: dark)');
const mqReduce = window.matchMedia('(prefers-reduced-motion: reduce)');
function applyTheme() {
  const p = state.settings.theme;
  const dark = p === 'dark' || (p === 'auto' && mqDark.matches);
  document.documentElement.dataset.theme = dark ? 'dark' : 'light';
  document.documentElement.dataset.reduced = mqReduce.matches ? 'true' : 'false';
}
mqDark.addEventListener('change', applyTheme);
mqReduce.addEventListener('change', applyTheme);

/* ---------------- money / guardrails / safety ---------------- */
const defaultAccount = () => state.accounts.find((a) => a.isPrimary) || state.accounts[0];
function spentToday() {
  const tk = dayKey(Date.now());
  return state.transactions
    .filter((t) => t.kind === 'expense' && dayKey(t.ts) === tk)
    .reduce((a, t) => a + t.halalas, 0);
}
function checksFor(card, amount) {
  const s = state.settings;
  const left = Math.max(0, s.dailyCap - spentToday());
  return [
    { pass: !card.isFrozen,                label: card.isFrozen ? 'Card •••• ' + card.last4 + ' is frozen' : 'Card •••• ' + card.last4 + ' is active' },
    { pass: amount <= card.perTxLimit,     label: 'Within card limit of ' + fmt(card.perTxLimit) + ' per transaction' },
    { pass: amount <= s.vaultCap,          label: 'Within family vault cap of ' + fmt(s.vaultCap) + ' per transaction' },
    { pass: amount <= left,                label: 'Within today’s allowance — ' + fmt(left) + ' of ' + fmt(s.dailyCap) + ' left' },
    { pass: amount <= card.balance,        label: 'Sufficient balance on •••• ' + card.last4 }
  ];
}
function safetyFor(merchant) {
  const m = merchant.trim().toLowerCase();
  const s = state.settings;
  if (s.blockedMerchants.some((b) => b.toLowerCase() === m)) return 'blocked';
  if (s.trustedMerchants.some((t) => t.toLowerCase() === m)) return 'trusted';
  return 'unverified';
}

/* ---------------- QR: build, parse, decode ---------------- */
function makePayload(merchant, category, halalas) {
  return [QR_PREFIX, merchant, category, String(halalas)].join('|');
}
function parsePayload(raw) {
  const parts = String(raw).trim().split('|');
  if (parts[0] !== QR_PREFIX || parts.length !== 4) {
    throw new Error('Unsupported code — expected a ' + QR_PREFIX + ' payment code.');
  }
  const merchant = parts[1].trim().slice(0, 60);
  const category = (parts[2].trim() || 'General').slice(0, 40);
  if (!merchant) throw new Error('Code has no merchant name.');
  if (!/^\d{1,9}$/.test(parts[3])) throw new Error('Code has an invalid amount.');
  const halalas = parseInt(parts[3], 10);
  if (halalas <= 0) throw new Error('Amount must be greater than zero.');
  if (halalas > 10000000) throw new Error('Amount exceeds the SAR 100,000 single-payment ceiling.');
  return { merchant, category, halalas };
}
function drawQr(canvas, payload, px) {
  const qr = qrcode(0, 'M');
  qr.addData(payload);
  qr.make();
  const n = qr.getModuleCount();
  const quiet = 4, dim = n + quiet * 2;
  canvas.width = dim; canvas.height = dim;
  const ctx = canvas.getContext('2d');
  ctx.fillStyle = '#fff'; ctx.fillRect(0, 0, dim, dim);
  ctx.fillStyle = '#000';
  for (let y = 0; y < n; y++) for (let x = 0; x < n; x++) {
    if (qr.isDark(y, x)) ctx.fillRect(x + quiet, y + quiet, 1, 1);
  }
  canvas.style.width = px + 'px'; canvas.style.height = px + 'px';
  return canvas;
}
function decodeFromCanvas(src) {
  const c = $('#decode-canvas');
  const scale = Math.min(1, 480 / src.width);
  const w = Math.max(80, Math.round(src.width * scale));
  const h = Math.max(80, Math.round(src.height * scale));
  c.width = w; c.height = h;
  const ctx = c.getContext('2d', { willReadFrequently: true });
  ctx.drawImage(src, 0, 0, w, h);
  const img = ctx.getImageData(0, 0, w, h);
  return jsQR(img.data, w, h);
}

/* ---------------- camera ---------------- */
async function startCamera() {
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    setScanStatus('err', 'No camera API in this browser — use a test code or manual entry.');
    render(); return;
  }
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } });
    session.camera.stream = stream;
    session.camera.on = true;
    setScanStatus('on', 'Camera live — decoding frames with jsQR');
    render();
    const video = $('#cam-video');
    if (video) { video.srcObject = stream; await video.play().catch(() => {}); }
    session.camera.timer = setInterval(() => {
      if (session.sheet) return;                    // never re-trigger while a sheet is open
      if (Date.now() < session.camera.cooldownUntil) return;
      const v = $('#cam-video');
      if (!v || v.readyState < 2) return;
      const res = decodeFromCanvas(v);
      if (res) {
        session.camera.cooldownUntil = Date.now() + 2500;
        buzz([18, 40, 18]);
        handleScan(res.data, 'camera');
      }
    }, 240);
  } catch (e) {
    session.camera.on = false;
    setScanStatus('err', 'Camera unavailable (' + (e && e.name ? e.name : 'error') + ') — use a test code below.');
    render();
  }
}
function stopCamera() {
  if (session.camera.timer) clearInterval(session.camera.timer);
  session.camera.timer = null;
  if (session.camera.stream) session.camera.stream.getTracks().forEach((t) => t.stop());
  session.camera.stream = null;
  session.camera.on = false;
  session.camera.torch = false;
}
async function toggleTorch() {
  const track = session.camera.stream && session.camera.stream.getVideoTracks()[0];
  if (!track) return;
  try {
    const caps = track.getCapabilities ? track.getCapabilities() : {};
    if (!caps.torch) { toast('This camera has no torch.', 'warn'); return; }
    session.camera.torch = !session.camera.torch;
    await track.applyConstraints({ advanced: [{ torch: session.camera.torch }] });
    buzz(12); render();
  } catch (e) { toast('Torch not supported here.', 'warn'); }
}
function setScanStatus(kind, text) { session.scanStatus = { kind, text }; }

function handleScan(raw, source) {
  try {
    const p = parsePayload(raw);
    setScanStatus('on', 'Decoded via ' + source + ': ' + p.merchant + ' — ' + fmt(p.halalas));
    openSheet(p, source);
  } catch (e) {
    setScanStatus('err', e.message);
    toast(e.message, 'warn');
    render();
  }
}

/* ---------------- biometric gate ---------------- */
async function verifyBiometric() {
  if (!state.settings.biometricsRequired) return { ok: true, note: 'Biometric lock is off — payment not gated.' };
  let hasPlatform = false;
  try {
    hasPlatform = window.PublicKeyCredential &&
      await PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable();
  } catch (e) { hasPlatform = false; }

  if (hasPlatform) {
    try {
      await navigator.credentials.get({
        publicKey: {
          challenge: crypto.getRandomValues(new Uint8Array(32)),
          timeout: 25000,
          userVerification: 'required',
          allowCredentials: []
        }
      });
      return { ok: true, note: 'Verified with this device’s screen lock / biometrics (WebAuthn).' };
    } catch (e) {
      if (e && e.name === 'NotAllowedError') return { ok: false, note: 'Verification cancelled — no payment made.' };
      return await passcodeFallback('Device authenticator errored (' + e.name + ').');
    }
  }
  return await passcodeFallback('This browser reports no platform authenticator.');
}
function passcodeFallback(reason) {
  return new Promise((resolve) => {
    openModal({
      title: 'Guardian passcode',
      body: '<p>' + esc(reason) + ' The shipped Android build gates on BiometricPrompt; this browser preview falls back to a demo passcode so the gate is still real.</p>' +
        '<p class="pill warn" style="justify-content:center;width:100%">Demo passcode: <b class="num">&nbsp;4321</b></p>' +
        '<div class="field" style="margin-top:12px"><label class="fl" for="pc">Enter passcode</label>' +
        '<input class="input num" id="pc" inputmode="numeric" autocomplete="one-time-code" placeholder="••••" /></div>',
      actions: [
        { label: 'Cancel', kind: '', value: false },
        { label: 'Verify', kind: 'primary', value: true }
      ],
      onAction: (value, root) => {
        if (!value) { resolve({ ok: false, note: 'Verification cancelled — no payment made.' }); return true; }
        const input = $('#pc', root);
        if (input && input.value.trim() === '4321') {
          resolve({ ok: true, note: 'Guardian passcode accepted (demo fallback).' });
          return true;
        }
        toast('Wrong passcode — no payment made.', 'err');
        if (input) { input.value = ''; input.focus(); }
        return false;
      }
    });
  });
}

/* ---------------- payment ---------------- */
function openSheet(p, source) {
  session.sheet = Object.assign({ source: source || 'manual', override: false, processing: false, biometricNote: '' }, p);
  render();
}
function closeSheet() { session.sheet = null; render(); }

async function pay() {
  const sh = session.sheet;
  if (!sh || sh.processing) return;                 // idempotency: single flight
  const card = defaultAccount();
  const checks = checksFor(card, sh.halalas);
  const safety = safetyFor(sh.merchant);
  const canPay = checks.every((c) => c.pass) && safety !== 'blocked' && (safety === 'trusted' || sh.override);
  if (!canPay) return;

  sh.processing = true;
  sh.biometricNote = 'Waiting for verification…';
  render();
  buzz(14);

  const v = await verifyBiometric();
  if (!v.ok) {
    sh.processing = false;
    sh.biometricNote = v.note;
    toast(v.note, 'err');
    render();
    return;
  }

  // deduct (integer halalas) + record
  state.accounts = state.accounts.map((a) => a.id === card.id ? Object.assign({}, a, { balance: a.balance - sh.halalas }) : a);
  state.transactions.unshift({
    id: uid(), merchant: sh.merchant, category: sh.category, halalas: sh.halalas,
    ts: Date.now(), cardLast4: card.last4, cardName: card.cardType,
    safety, safetyNote: v.note, kind: 'expense'
  });
  save();
  buzz([20, 60, 20, 60, 40]);
  const summary = { merchant: sh.merchant, halalas: sh.halalas, note: v.note, safety };
  session.sheet = null;
  render();
  openModal({
    title: 'Payment complete',
    body: '<div class="safety-card ' + (summary.safety === 'trusted' ? 'trusted' : 'unverified') + '" style="text-align:left;margin-bottom:14px">' +
      '<span class="ic">' + icon(summary.safety === 'trusted' ? 'i-check' : 'i-alert') + '</span>' +
      '<span><span class="t1">' + esc(fmt(summary.halalas)) + ' to ' + esc(summary.merchant) + '</span>' +
      '<span class="t2">' + esc(summary.note) + '</span></span></div>' +
      '<p>Recorded to on-device history. Balance updated.</p>',
    actions: [{ label: 'Done', kind: 'primary', value: true }],
    onAction: () => true
  });
}

/* ---------------- rendering ---------------- */
function render() {
  applyTheme();
  renderAppBar();
  renderTabBar();
  const screen = $('#screen');
  if (session.tab === 'scan') screen.innerHTML = viewScan();
  else if (session.tab === 'wallet') screen.innerHTML = viewWallet();
  else if (session.tab === 'activity') screen.innerHTML = viewActivity();
  else screen.innerHTML = viewSecurity();
  afterRender();
  renderSheet();
  renderNote();
}

function totalBalance() { return state.accounts.reduce((a, c) => a + c.balance, 0); }

function renderAppBar() {
  const hidden = state.balanceHidden;
  const themeIcon = state.settings.theme === 'dark' ? 'i-moon' : state.settings.theme === 'light' ? 'i-sun' : 'i-auto';
  $('#appbar').innerHTML =
    '<div class="brand"><span class="brand-mark">' + icon('i-qr') + '</span>' +
    '<span class="brand-txt"><span class="b1">QuickPay QR</span>' +
    '<span class="b2">Redesign preview · honest states</span></span></div>' +
    '<span class="spacer"></span>' +
    '<button class="iconbtn" data-action="toggle-hidden" aria-pressed="' + hidden + '" aria-label="' + (hidden ? 'Show balance' : 'Hide balance') + '">' + icon(hidden ? 'i-eye-off' : 'i-eye') + '</button>' +
    '<button class="iconbtn" data-action="cycle-theme" aria-label="Theme: ' + esc(state.settings.theme) + '">' + icon(themeIcon) + '</button>';
  // balance strip lives under the bar
  let strip = $('#balance-strip');
  if (!strip) {
    strip = document.createElement('div');
    strip.id = 'balance-strip';
    strip.className = 'balance-strip';
    $('#appbar').after(strip);
  }
  strip.innerHTML =
    '<div><div class="lbl">Total available · ' + state.accounts.length + ' cards</div>' +
    '<div class="amt num"><span class="cur">SAR</span>' + esc(hidden ? '••••••' : fmtNum(totalBalance()).replace(/\B(?=(\d{3})+(?!\d))/g, ',')) + '</div></div>' +
    '<span class="grow"></span>' +
    '<span class="pill ' + (spentToday() > state.settings.dailyCap ? 'danger' : 'ok') + '">' + icon('i-activity') +
    'Spent today ' + esc(fmt(spentToday())) + ' / ' + esc(fmt(state.settings.dailyCap)) + '</span>';
}

const TABS = [
  { id: 'scan', label: 'Scan', icon: 'i-qr' },
  { id: 'wallet', label: 'Wallet', icon: 'i-wallet' },
  { id: 'activity', label: 'Activity', icon: 'i-activity' },
  { id: 'security', label: 'Security', icon: 'i-shield' }
];
function renderTabBar() {
  $('#tabbar').innerHTML = '<div class="inner">' + TABS.map((t) =>
    '<button class="tab" role="tab" data-action="tab" data-tab="' + t.id + '" aria-selected="' + (session.tab === t.id) + '">' +
    icon(t.icon) + '<span>' + t.label + '</span></button>').join('') + '</div>';
}

/* ----- scan ----- */
function viewScan() {
  const st = session.scanStatus;
  const camOn = session.camera.on;
  return '' +
  '<div class="well-outer"><div class="well-bezel"><div class="well-lip"><div class="well">' +
    (camOn
      ? '<video id="cam-video" playsinline muted autoplay></video>'
      : '<div class="idle"><div class="emblem">' + icon('i-qr') + '</div>' +
        '<div class="t1">Decoder ready</div>' +
        '<div class="t2">Start the camera, or decode the on-screen test code — both run the real jsQR pipeline.</div></div>') +
    '<div class="reticle"><i></i><i></i><i></i><i></i></div>' +
    (camOn ? '<div class="beam"></div>' : '') +
  '</div></div></div></div>' +
  '<div class="statusline ' + (st.kind === 'on' ? 'on' : st.kind === 'warn' ? 'warn' : st.kind === 'err' ? 'err' : '') + '" role="status">' +
    '<span class="dot"></span><span>' + esc(st.text) + '</span></div>' +
  '<div class="scan-controls">' +
    '<button class="btn" data-action="torch" ' + (camOn ? '' : 'disabled') + ' aria-label="Torch">' + icon('i-flash') + '</button>' +
    '<button class="btn primary" data-action="camera-toggle">' + icon('i-camera') + (camOn ? 'Stop camera' : 'Start camera') + '</button>' +
    '<button class="btn" data-action="testqr-toggle" aria-expanded="false">' + icon('i-qr') + 'Test code</button>' +
  '</div>' +
  '<div class="testqr-wrap" id="testqr-wrap"><div class="testqr-box"><canvas id="testqr-canvas"></canvas></div>' +
    '<div class="row" style="margin-top:12px"><button class="btn block" data-action="decode-test">' + icon('i-search') + 'Decode this on-screen code</button></div>' +
    '<p style="font-size:12px;color:var(--ink-3);margin:10px 2px 0;line-height:1.5">The code above is generated, rasterised and fed through the same decoder the camera uses — no fake "scan" button.</p>' +
  '</div>' +
  '<div class="section-h"><h2>Try a payment</h2><span class="hint">real decode path</span></div>' +
  '<div class="demo-grid">' + DEMO.map((d, i) =>
    '<button class="demo-chip" data-action="demo" data-i="' + i + '">' +
    '<span class="ic">' + icon('i-qr') + '</span>' +
    '<span class="nm">' + esc(d.merchant) + '</span>' +
    '<span class="am num">' + esc(fmt(d.halalas)) + '</span>' +
    '<span class="tag">' + esc(d.note) + '</span></button>').join('') + '</div>' +
  '<div class="section-h"><h2>Manual entry</h2><span class="hint">same validation as a scan</span></div>' +
  '<div class="card sunk"><div class="stack">' +
    '<div class="field"><label class="fl" for="m-merchant">Merchant</label><input class="input" id="m-merchant" placeholder="e.g. School Cafeteria" /></div>' +
    '<div class="row"><div class="field" style="flex:1"><label class="fl" for="m-cat">Category</label><input class="input" id="m-cat" placeholder="Food & Snacks" /></div>' +
    '<div class="field" style="flex:1"><label class="fl" for="m-amt">Amount (SAR)</label><input class="input num" id="m-amt" inputmode="decimal" placeholder="15.00" /></div></div>' +
    '<button class="btn primary block" data-action="manual">' + icon('i-plus') + 'Create payment code & scan it</button>' +
  '</div></div>';
}

/* ----- wallet ----- */
function viewWallet() {
  return '<div class="section-h"><h2>Linked cards</h2><span class="hint">' + state.accounts.length + ' cards</span></div>' +
  state.accounts.map((a) => {
    const badges = (a.isPrimary ? '<span class="pill">' + icon('i-star') + 'Default</span>' : '') +
                   (a.isFrozen ? '<span class="pill">' + icon('i-snow') + 'Frozen</span>' : '');
    return '<div class="bankcard g' + a.gradient + (a.isFrozen ? ' frozen' : '') + '">' +
      '<div class="badges">' + badges + '</div>' +
      '<div class="top"><span class="ctype">' + esc(a.cardType) + '</span><span class="bank">· ' + esc(a.bankName) + '</span></div>' +
      '<div class="bal num">' + esc(fmt(a.balance)) + '</div>' +
      '<div class="meta num"><span>•••• •••• •••• ' + esc(a.last4) + '</span><span>Limit ' + esc(fmt(a.perTxLimit)) + '/tx</span></div>' +
    '</div>' +
    '<div class="card-actions" style="margin-bottom:14px">' +
      (a.isPrimary ? '' : '<button class="btn small" data-action="set-default" data-id="' + a.id + '">' + icon('i-star') + 'Set default</button>') +
      '<button class="btn small" data-action="freeze" data-id="' + a.id + '">' + icon('i-snow') + (a.isFrozen ? 'Unfreeze' : 'Freeze') + '</button>' +
      '<button class="btn small" data-action="limit-sheet" data-id="' + a.id + '">' + icon('i-pencil') + 'Limit</button>' +
      (state.accounts.length > 1 ? '<button class="btn small danger" data-action="unlink" data-id="' + a.id + '">' + icon('i-trash') + 'Unlink</button>' : '') +
    '</div>';
  }).join('') +
  '<div class="section-h"><h2>Family caps</h2><span class="hint">enforced on every payment</span></div>' +
  '<div class="card"><div class="stack">' +
    capRow('Daily allowance (cumulative)', state.settings.dailyCap, 'cap-daily') +
    capRow('Vault cap (per transaction)', state.settings.vaultCap, 'cap-vault') +
  '</div></div>' +
  '<button class="btn primary block" style="margin-top:16px" data-action="link-sheet">' + icon('i-plus') + 'Link a card</button>';
}
function capRow(label, halalas, action) {
  return '<div class="row" style="justify-content:space-between"><div><div style="font:600 14px/1.3 var(--font-body)">' + esc(label) + '</div>' +
    '<div style="font-size:12px;color:var(--ink-3)">' + (action === 'cap-daily' ? 'Resets at midnight, tracked from history' : 'Applies to all cards') + '</div></div>' +
    '<span class="stepper"><button data-action="step" data-what="' + action + '" data-d="-1" aria-label="Decrease">−</button>' +
    '<span class="val num">' + esc(fmt(halalas)) + '</span>' +
    '<button data-action="step" data-what="' + action + '" data-d="1" aria-label="Increase">+</button></span></div>';
}

/* ----- activity ----- */
function viewActivity() {
  const q = session.search.trim().toLowerCase();
  let list = state.transactions.slice();
  if (session.filter === 'in') list = list.filter((t) => t.kind !== 'expense');
  if (session.filter === 'out') list = list.filter((t) => t.kind === 'expense');
  if (q) list = list.filter((t) => (t.merchant + ' ' + t.category).toLowerCase().includes(q));

  const spent = spentToday();
  const pct = clamp(Math.round((spent / Math.max(1, state.settings.dailyCap)) * 100), 0, 100);
  const groups = [];
  list.forEach((t) => {
    const k = dayKey(t.ts);
    let g = groups.find((x) => x.key === k);
    if (!g) { g = { key: k, label: dayLabel(t.ts), items: [] }; groups.push(g); }
    g.items.push(t);
  });

  return '<div class="section-h"><h2>Activity</h2><span class="hint">' + state.transactions.length + ' records on device</span></div>' +
  '<div class="card sunk"><div class="row" style="justify-content:space-between;margin-bottom:8px">' +
    '<span style="font-size:12px;color:var(--ink-2)">Today vs daily allowance</span>' +
    '<span class="num" style="font-size:12px;font-weight:700;color:' + (spent > state.settings.dailyCap ? 'var(--danger-ink)' : 'var(--ok-ink)') + '">' + pct + '%</span></div>' +
    '<div class="meter' + (spent > state.settings.dailyCap ? ' over' : '') + '"><i style="width:' + pct + '%"></i></div></div>' +
  '<div class="row" style="margin:14px 0 10px"><div class="field" style="flex:1;position:relative">' +
    '<input class="input" id="search" placeholder="Search merchant or category" value="' + esc(session.search) + '" style="padding-left:38px" />' +
    '<span style="position:absolute;left:12px;top:13px;color:var(--ink-3)">' + icon('i-search','icn') + '</span></div></div>' +
  '<div class="chips" style="margin-bottom:14px">' +
    [['all', 'All'], ['out', 'Money out'], ['in', 'Money in']].map((f) =>
      '<button class="chip" data-action="filter" data-f="' + f[0] + '" aria-pressed="' + (session.filter === f[0]) + '">' + f[1] + '</button>').join('') +
  '</div>' +
  (groups.length === 0
    ? '<div class="empty"><div class="emblem">' + icon('i-activity') + '</div><div class="t1">Nothing here yet</div>' +
      '<p style="margin:6px 0 14px;font-size:13px">Payments you make appear here instantly, stored on this device only.</p>' +
      '<button class="btn primary" data-action="tab" data-tab="scan">' + icon('i-qr') + 'Make a payment</button></div>'
    : groups.map((g) => '<div class="section-h" style="margin-top:18px"><h2 style="font-size:14px">' + esc(g.label) + '</h2></div>' +
      '<div class="txlist">' + g.items.map((t) =>
        '<div class="tx"><span class="tic ' + (t.kind === 'expense' ? 'out' : 'in') + '">' + icon(t.kind === 'expense' ? 'i-qr' : 'i-plus') + '</span>' +
        '<span class="body"><span class="m">' + esc(t.merchant) + '</span>' +
        '<span class="s">' + esc(t.category) + ' · •••• ' + esc(t.cardLast4) + ' · ' + safetyWord(t.safety) + '</span></span>' +
        '<span class="amt num ' + (t.kind === 'expense' ? 'out' : 'in') + '">' + (t.kind === 'expense' ? '−' : '+') + esc(fmt(t.halalas)) + '</span>' +
        '<button class="iconbtn" style="width:34px;height:34px;box-shadow:none;background:transparent" data-action="del-tx" data-id="' + t.id + '" aria-label="Delete record">' + icon('i-trash') + '</button></div>'
      ).join('') + '</div>').join(''));
}
function safetyWord(s) { return s === 'trusted' ? 'trusted' : s === 'blocked' ? 'blocked' : 'unverified · override'; }
function dayLabel(ts) {
  const k = dayKey(ts);
  if (k === dayKey(Date.now())) return 'Today';
  if (k === dayKey(Date.now() - 86400000)) return 'Yesterday';
  return new Date(ts).toLocaleDateString(undefined, { weekday: 'short', day: 'numeric', month: 'short' });
}

/* ----- security ----- */
function viewSecurity() {
  const s = state.settings;
  return '<div class="section-h"><h2>Security</h2><span class="hint">every toggle here is wired</span></div>' +
  '<div class="card"><div class="stack">' +
    toggleRow('Biometric lock', 'Gate every payment on this device’s authenticator', 'set-bio', s.biometricsRequired, 'i-finger') +
    toggleRow('Tactile haptics', 'Vibration feedback on taps and payments', 'set-haptics', s.haptics, 'i-flash') +
  '</div></div>' +
  '<div class="section-h"><h2>Appearance</h2></div>' +
  '<div class="card"><div class="chips">' +
    [['auto', 'Auto', 'i-auto'], ['light', 'Light', 'i-sun'], ['dark', 'Dark', 'i-moon']].map((t) =>
      '<button class="chip" data-action="theme" data-t="' + t[0] + '" aria-pressed="' + (s.theme === t[0]) + '">' + icon(t[2]) + ' ' + t[1] + '</button>').join('') +
  '</div><p style="font-size:12px;color:var(--ink-3);margin:10px 2px 0;line-height:1.5">Dark theme uses an inverted soft-UI shadow recipe (light top-left, near-black bottom-right) rather than inverted hexes.</p></div>' +
  '<div class="section-h"><h2>Merchant lists</h2><span class="hint">the safety rulebook</span></div>' +
  '<div class="card"><div class="stack">' +
    listEditor('Parent-approved (trusted)', s.trustedMerchants, 'add-trusted', 'del-trusted', 'ok') +
    listEditor('Family block list (always refused)', s.blockedMerchants, 'add-blocked', 'del-blocked', 'danger') +
  '</div></div>' +
  '<div class="safety-card unverified" style="margin-top:16px"><span class="ic">' + icon('i-alert') + '</span>' +
    '<span><span class="t1">Verification service: local rules only</span>' +
    '<span class="t2">No external AI verification API is connected in this preview, so unknown merchants are treated as unverified and fail closed — they cannot be paid without an explicit guardian override. The previous build stamped these "Verified Merchant"; that was false.</span></span></div>' +
  '<div class="section-h"><h2>Data</h2></div>' +
  '<div class="card"><div class="row" style="justify-content:space-between">' +
    '<span style="font-size:13px;color:var(--ink-2)">Wallet, caps and history persist on this device.</span>' +
    '<button class="btn small danger" data-action="reset">' + icon('i-refresh') + 'Reset demo</button></div></div>';
}
function toggleRow(title, sub, action, checked, ic) {
  return '<div class="row" style="justify-content:space-between"><div class="row"><span style="width:34px;height:34px;border-radius:12px;display:grid;place-items:center;background:var(--accent-soft);color:var(--accent-ink)">' + icon(ic, 'icn') + '</span>' +
    '<span><div style="font:600 14px/1.3 var(--font-body)">' + esc(title) + '</div><div style="font-size:12px;color:var(--ink-3)">' + esc(sub) + '</div></span></div>' +
    '<span class="switch"><input type="checkbox" data-action="' + action + '" ' + (checked ? 'checked' : '') + ' role="switch" aria-label="' + esc(title) + '" /><span class="track"></span></span></div>';
}
function listEditor(title, items, addAction, delAction, tone) {
  return '<div><div style="font:600 13px/1.3 var(--font-body);margin-bottom:8px">' + esc(title) + '</div>' +
    '<div class="chips" style="margin-bottom:8px">' + (items.length ? items.map((m) =>
      '<span class="pill ' + tone + '">' + esc(m) + '<button data-action="' + delAction + '" data-m="' + esc(m) + '" aria-label="Remove ' + esc(m) + '" style="border:0;background:none;cursor:pointer;color:inherit;padding:0;display:grid">' + icon('i-x','icn sm') + '</button></span>').join('')
      : '<span style="font-size:12px;color:var(--ink-3)">Empty</span>') + '</div>' +
    '<div class="row"><input class="input" id="' + addAction + '-in" placeholder="Merchant name" />' +
    '<button class="btn small" data-action="' + addAction + '">' + icon('i-plus') + 'Add</button></div></div>';
}

/* ---------------- sheet ---------------- */
function renderSheet() {
  const sheet = $('#sheet'), scrim = $('#scrim');
  const sh = session.sheet;
  if (!sh) { sheet.hidden = true; scrim.hidden = true; sheet.innerHTML = ''; return; }
  sheet.hidden = false; scrim.hidden = false;

  const card = defaultAccount();
  const checks = checksFor(card, sh.halalas);
  const safety = safetyFor(sh.merchant);
  const allPass = checks.every((c) => c.pass);
  const canPay = allPass && safety !== 'blocked' && (safety === 'trusted' || sh.override) && !sh.processing;

  const safetyCard = safety === 'trusted'
    ? '<div class="safety-card trusted"><span class="ic">' + icon('i-check') + '</span><span><span class="t1">Trusted merchant</span><span class="t2">“' + esc(sh.merchant) + '” is on the parent-approved list. No override needed.</span></span></div>'
    : safety === 'blocked'
      ? '<div class="safety-card blocked"><span class="ic">' + icon('i-x') + '</span><span><span class="t1">Blocked merchant</span><span class="t2">“' + esc(sh.merchant) + '” is on the family block list. Payment is refused and cannot be overridden.</span></span></div>'
      : '<div class="safety-card unverified"><span class="ic">' + icon('i-alert') + '</span><span><span class="t1">Unverified merchant</span><span class="t2">No verification service is connected, so this merchant fails closed. A guardian must explicitly approve this one payment.</span></span>' +
        '<label class="row" style="margin-top:10px;font-size:13px;font-weight:600;color:var(--warn-ink);cursor:pointer">' +
        '<input type="checkbox" data-action="override" ' + (sh.override ? 'checked' : '') + ' style="width:18px;height:18px" /> I am the guardian — approve this payment</label></div>';

  sheet.innerHTML =
    '<div class="grip"></div>' +
    '<h2 id="sheet-title">' + esc(sh.merchant) + '</h2>' +
    '<div class="sub">' + esc(sh.category) + ' · decoded via ' + esc(sh.source) + '</div>' +
    '<div class="payline"><span style="font-size:13px;color:var(--ink-3)">Amount</span><span class="amt num">' + esc(fmt(sh.halalas)) + '</span></div>' +
    '<div class="card sunk" style="margin-bottom:14px"><div style="font:600 13px/1.3 var(--font-body);margin-bottom:6px">Paying from</div>' +
      '<div class="row" style="justify-content:space-between"><span style="font-size:14px;font-weight:600">' + esc(card.cardType) + ' •••• ' + esc(card.last4) + '</span>' +
      '<span class="num" style="font-size:13px;color:var(--ink-2)">' + esc(fmt(card.balance)) + '</span></div></div>' +
    '<div style="font:600 13px/1.3 var(--font-body);margin-bottom:4px">Guardrails</div>' +
    checks.map((c) => '<div class="check ' + (c.pass ? 'pass' : 'fail') + '"><span class="ic">' + icon(c.pass ? 'i-check' : 'i-x') + '</span><span>' + esc(c.label) + '</span></div>').join('') +
    '<div style="height:14px"></div>' + safetyCard +
    (sh.biometricNote ? '<p style="font-size:12px;color:var(--ink-2);margin:12px 2px 0;line-height:1.5">' + icon('i-finger', 'icn sm') + ' ' + esc(sh.biometricNote) + '</p>' : '') +
    '<div class="row" style="margin-top:16px;gap:10px">' +
      '<button class="btn" data-action="sheet-close" style="flex:1">Cancel</button>' +
      '<button class="btn primary" data-action="pay" style="flex:2" ' + (canPay ? '' : 'disabled aria-disabled="true"') + '>' +
        icon('i-lock') + (sh.processing ? 'Verifying…' : 'Verify & pay ' + esc(fmt(sh.halalas))) + '</button>' +
    '</div>' +
    (!allPass ? '<p style="font-size:12px;color:var(--danger-ink);margin:10px 2px 0">One or more guardrails failed — the payment cannot proceed.</p>' : '') +
    (safety === 'blocked' ? '<p style="font-size:12px;color:var(--danger-ink);margin:10px 2px 0">Blocked merchants are always refused.</p>' : '') +
    (safety === 'unverified' && !sh.override ? '<p style="font-size:12px;color:var(--warn-ink);margin:10px 2px 0">Guardian approval required.</p>' : '');
}

/* ---------------- modal / toast ---------------- */
let modalHandler = null;
function openModal(cfg) {
  const root = $('#modal');
  root.hidden = false;
  root.innerHTML = '<div class="box"><h3>' + esc(cfg.title) + '</h3>' + cfg.body +
    '<div class="row" style="justify-content:center;gap:10px;margin-top:6px">' +
    cfg.actions.map((a, i) => '<button class="btn ' + (a.kind || '') + '" data-mi="' + i + '">' + esc(a.label) + '</button>').join('') +
    '</div></div>';
  modalHandler = (i) => {
    const close = cfg.onAction(cfg.actions[i].value, root);
    if (close !== false) { root.hidden = true; root.innerHTML = ''; modalHandler = null; }
  };
  const input = $('input', root);
  if (input) setTimeout(() => input.focus(), 30);
}
function toast(msg, kind) {
  const t = document.createElement('div');
  t.className = 'toast ' + (kind || '');
  t.innerHTML = icon(kind === 'ok' ? 'i-check' : kind === 'err' ? 'i-x' : 'i-alert') + '<span>' + esc(msg) + '</span>';
  $('#toasts').appendChild(t);
  setTimeout(() => { t.style.opacity = '0'; t.style.transition = 'opacity .3s'; setTimeout(() => t.remove(), 320); }, 3200);
}

function renderNote() {
  $('#stage-note').innerHTML = '<h2>What is real in this preview</h2><ul>' +
    '<li><b>QR decoding</b> — jsQR over camera frames, or over the generated on-screen test code. No fake scan button.</li>' +
    '<li><b>Biometric gate</b> — WebAuthn platform authenticator when the device has one; labelled demo passcode <code>4321</code> otherwise.</li>' +
    '<li><b>Fail-closed safety</b> — unknown merchants are <i>unverified</i> and need guardian override; block list always refuses.</li>' +
    '<li><b>Integer money</b> — balances and limits are halalas; daily allowance is cumulative and tracked from history.</li>' +
    '<li><b>Persistence</b> — wallet, caps and history survive reload (localStorage).</li>' +
    '<li><b>One payment per sheet</b> — the pay button disables while verifying.</li>' +
    '</ul><p style="margin-top:10px">This is the runnable expression of the redesign. The Android/Compose port uses the same tokens (see <code>DESIGN.md</code>).</p>';
}

/* ---------------- after-render hooks ---------------- */
function afterRender() {
  if (session.camera.on) {
    const v = $('#cam-video');
    if (v && session.camera.stream) { v.srcObject = session.camera.stream; v.play().catch(() => {}); }
  }
  if (session.tab === 'scan' && session.testqrOpen) {
    const wrap = $('#testqr-wrap');
    if (wrap) {
      wrap.classList.add('show');
      const btn = $('[data-action="testqr-toggle"]');
      if (btn) btn.setAttribute('aria-expanded', 'true');
      drawQr($('#testqr-canvas'), makePayload(DEMO[0].merchant, DEMO[0].category, DEMO[0].halalas), 190);
    }
  }
  const search = $('#search');
  if (search) {
    search.addEventListener('input', (e) => {
      session.search = e.target.value;
      const pos = e.target.selectionStart;
      render();
      const again = $('#search');
      if (again) { again.focus(); again.setSelectionRange(pos, pos); }
    });
  }
}

/* ---------------- events (delegated) ---------------- */
document.addEventListener('click', async (e) => {
  const t = e.target.closest('[data-action]');
  if (!t) return;
  const a = t.dataset.action;
  const id = t.dataset.id;

  switch (a) {
    case 'tab': session.tab = t.dataset.tab; stopCamera(); setScanStatus('idle', 'Decoder idle — no camera, no code in view'); buzz(8); render(); break;
    case 'toggle-hidden': state.balanceHidden = !state.balanceHidden; save(); buzz(8); render(); break;
    case 'cycle-theme': {
      const order = ['auto', 'light', 'dark'];
      state.settings.theme = order[(order.indexOf(state.settings.theme) + 1) % 3];
      save(); buzz(8); render(); break;
    }
    case 'theme': state.settings.theme = t.dataset.t; save(); render(); break;
    case 'camera-toggle': if (session.camera.on) { stopCamera(); setScanStatus('idle', 'Camera stopped.'); } else { await startCamera(); } render(); break;
    case 'torch': await toggleTorch(); break;
    case 'testqr-toggle': {
      session.testqrOpen = !session.testqrOpen;
      const wrap = $('#testqr-wrap');
      if (wrap) {
        wrap.classList.toggle('show', session.testqrOpen);
        t.setAttribute('aria-expanded', String(session.testqrOpen));
        if (session.testqrOpen) drawQr($('#testqr-canvas'), makePayload(DEMO[0].merchant, DEMO[0].category, DEMO[0].halalas), 190);
      }
      break;
    }
    case 'decode-test': {
      const src = $('#testqr-canvas');
      if (!src) break;
      buzz(14);
      const res = decodeFromCanvas(src);
      if (res) { setScanStatus('on', 'On-screen code decoded by jsQR.'); handleScan(res.data, 'on-screen decode'); }
      else { setScanStatus('err', 'Decoder found no code in the raster.'); render(); }
      break;
    }
    case 'demo': { const d = DEMO[Number(t.dataset.i)]; buzz(12); handleScan(makePayload(d.merchant, d.category, d.halalas), 'demo code'); break; }
    case 'manual': {
      const m = $('#m-merchant').value.trim();
      const c = $('#m-cat').value.trim() || 'General';
      const h = parseSar($('#m-amt').value);
      if (!m) { toast('Enter a merchant name.', 'warn'); break; }
      if (!isFinite(h) || h <= 0) { toast('Enter a valid amount in SAR.', 'warn'); break; }
      buzz(12);
      handleScan(makePayload(m, c, h), 'manual entry');
      break;
    }
    case 'sheet-close': closeSheet(); break;
    case 'pay': await pay(); break;
    case 'set-default':
      state.accounts = state.accounts.map((x) => Object.assign({}, x, { isPrimary: x.id === id }));
      save(); buzz(10); toast('Default card updated.', 'ok'); render(); break;
    case 'freeze':
      state.accounts = state.accounts.map((x) => x.id === id ? Object.assign({}, x, { isFrozen: !x.isFrozen }) : x);
      save(); buzz(14); render(); break;
    case 'limit-sheet': {
      const card = state.accounts.find((x) => x.id === id);
      openModal({
        title: 'Per-transaction limit',
        body: '<p>Snaps to SAR 25 steps, SAR 25 – SAR 10,000.</p>' +
          '<div style="display:flex;justify-content:center;margin:6px 0 4px"><span class="stepper">' +
          '<button data-ls="-1" aria-label="Decrease">−</button><span class="val num" id="lim-val">' + esc(fmt(card.perTxLimit)) + '</span>' +
          '<button data-ls="1" aria-label="Increase">+</button></span></div>',
        actions: [{ label: 'Cancel', kind: '', value: false }, { label: 'Save', kind: 'primary', value: true }],
        onAction: (v, root) => {
          if (v) {
            const shown = parseSar($('#lim-val', root).textContent);
            if (isFinite(shown)) {
              state.accounts = state.accounts.map((x) => x.id === id ? Object.assign({}, x, { perTxLimit: shown }) : x);
              save(); toast('Limit saved.', 'ok');
            }
          }
          render(); return true;
        }
      });
      const root = $('#modal');
      break;
    }
    case 'unlink': {
      const card = state.accounts.find((x) => x.id === id);
      openModal({
        title: 'Unlink card?',
        body: '<p>•••• ' + esc(card.last4) + ' will be removed from this wallet. History is kept.</p>',
        actions: [{ label: 'Keep', kind: '', value: false }, { label: 'Unlink', kind: 'danger', value: true }],
        onAction: (v) => {
          if (v) {
            const wasDefault = card.isPrimary;
            state.accounts = state.accounts.filter((x) => x.id !== id);
            if (wasDefault) state.accounts[0].isPrimary = true;
            save(); toast('Card unlinked.', 'ok');
          }
          render(); return true;
        }
      });
      break;
    }
    case 'link-sheet': {
      openModal({
        title: 'Link a card',
        body: '<div class="stack" style="text-align:left">' +
          '<div class="field"><label class="fl" for="l-bank">Bank</label><input class="input" id="l-bank" placeholder="National Digital Bank" /></div>' +
          '<div class="row"><div class="field" style="flex:1"><label class="fl" for="l-type">Type</label><select class="input" id="l-type"><option>Visa Debit</option><option>Mastercard</option><option>Credit</option><option>Debit</option></select></div>' +
          '<div class="field" style="flex:1"><label class="fl" for="l-last4">Last 4</label><input class="input num" id="l-last4" inputmode="numeric" maxlength="4" placeholder="1234" /></div></div>' +
          '<div class="row"><div class="field" style="flex:1"><label class="fl" for="l-bal">Balance (SAR)</label><input class="input num" id="l-bal" inputmode="decimal" placeholder="1000.00" /></div>' +
          '<div class="field" style="flex:1"><label class="fl" for="l-lim">Limit (SAR)</label><input class="input num" id="l-lim" inputmode="decimal" placeholder="500.00" /></div></div>' +
          '<label class="row" style="font-size:13px;font-weight:600"><input type="checkbox" id="l-def" style="width:18px;height:18px" /> Make default</label></div>',
        actions: [{ label: 'Cancel', kind: '', value: false }, { label: 'Link card', kind: 'primary', value: true }],
        onAction: (v, root) => {
          if (!v) { render(); return true; }
          const last4 = ($('#l-last4', root).value || '').replace(/\D/g, '').slice(-4);
          const bal = parseSar($('#l-bal', root).value);
          const lim = parseSar($('#l-lim', root).value);
          if (last4.length !== 4) { toast('Last 4 must be four digits.', 'warn'); return false; }
          if (!isFinite(bal) || bal < 0) { toast('Enter a valid balance.', 'warn'); return false; }
          const makeDef = $('#l-def', root).checked;
          const acc = {
            id: 'acc_' + uid(), bankName: $('#l-bank', root).value.trim() || 'National Digital Bank',
            cardType: $('#l-type', root).value, last4, balance: bal,
            perTxLimit: isFinite(lim) ? snap25(lim) : 50000, isFrozen: false, isPrimary: makeDef,
            gradient: state.accounts.length % 3
          };
          state.accounts = state.accounts.map((x) => makeDef ? Object.assign({}, x, { isPrimary: false }) : x);
          state.accounts.push(acc);
          save(); toast('Card linked.', 'ok'); render(); return true;
        }
      });
      break;
    }
    case 'step': {
      const d = Number(t.dataset.d) * 2500;
      if (t.dataset.what === 'cap-daily') state.settings.dailyCap = snap25(state.settings.dailyCap + d);
      else state.settings.vaultCap = snap25(state.settings.vaultCap + d);
      save(); buzz(8); render(); break;
    }
    case 'set-bio': state.settings.biometricsRequired = t.checked; save(); buzz(10); toast(t.checked ? 'Payments now require verification.' : 'Biometric lock off — payments ungated.', t.checked ? 'ok' : 'warn'); break;
    case 'set-haptics': state.settings.haptics = t.checked; save(); if (t.checked) buzz(20); break;
    case 'add-trusted': case 'add-blocked': {
      const input = $('#' + a + '-in');
      const name = (input.value || '').trim();
      if (!name) { toast('Enter a merchant name.', 'warn'); break; }
      const key = a === 'add-trusted' ? 'trustedMerchants' : 'blockedMerchants';
      const other = a === 'add-trusted' ? 'blockedMerchants' : 'trustedMerchants';
      state.settings[key] = state.settings[key].filter((m) => m.toLowerCase() !== name.toLowerCase());
      state.settings[other] = state.settings[other].filter((m) => m.toLowerCase() !== name.toLowerCase());
      state.settings[key].push(name);
      save(); toast(name + (key === 'trustedMerchants' ? ' marked trusted.' : ' blocked.'), 'ok'); render(); break;
    }
    case 'del-trusted': state.settings.trustedMerchants = state.settings.trustedMerchants.filter((m) => m !== t.dataset.m); save(); render(); break;
    case 'del-blocked': state.settings.blockedMerchants = state.settings.blockedMerchants.filter((m) => m !== t.dataset.m); save(); render(); break;
    case 'filter': session.filter = t.dataset.f; render(); break;
    case 'del-tx': {
      openModal({
        title: 'Delete record?',
        body: '<p>This removes the entry from on-device history. Balances are not rewound.</p>',
        actions: [{ label: 'Keep', kind: '', value: false }, { label: 'Delete', kind: 'danger', value: true }],
        onAction: (v) => { if (v) { state.transactions = state.transactions.filter((x) => x.id !== id); save(); toast('Record deleted.', 'ok'); } render(); return true; }
      });
      break;
    }
    case 'reset': {
      openModal({
        title: 'Reset demo data?',
        body: '<p>Wallet, caps, lists and history return to their seeded state.</p>',
        actions: [{ label: 'Cancel', kind: '', value: false }, { label: 'Reset', kind: 'danger', value: true }],
        onAction: (v) => { if (v) { localStorage.removeItem(LS_KEY); location.reload(); } return true; }
      });
      break;
    }
  }
});
document.addEventListener('change', (e) => {
  const t = e.target.closest('[data-action]');
  if (!t) return;
  if (t.dataset.action === 'override' && session.sheet) {
    session.sheet.override = t.checked;
    buzz(8);
    renderSheet();
  }
});
$('#scrim').addEventListener('click', closeSheet);
$('#modal').addEventListener('click', (e) => {
  const step = e.target.closest('[data-ls]');
  if (step) {
    const val = $('#lim-val', $('#modal'));
    if (val) val.textContent = fmt(snap25(parseSar(val.textContent) + Number(step.dataset.ls) * 2500));
    return;
  }
  const b = e.target.closest('[data-mi]');
  if (b && modalHandler) modalHandler(Number(b.dataset.mi));
});
$('#modal').addEventListener('keydown', (e) => {
  if (e.key !== 'Enter') return;
  const primary = $('[data-mi].primary', $('#modal'));
  if (primary) { e.preventDefault(); primary.click(); }
});
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    if (!$('#modal').hidden) return;          // gates must be answered explicitly
    if (session.sheet) closeSheet();
  }
});

/* boot */
render();
})();
