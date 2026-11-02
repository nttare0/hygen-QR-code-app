/* Behavioural smoke test for the web redesign preview.
   Run:  npm i jsdom && node web/test/smoke.js   (with the preview server up)
   Server: python3 -m http.server 8080 --bind 0.0.0.0 --directory web
   Asserts the honesty/guardrail contract, not just that pages render. */
const { JSDOM } = require('jsdom');

const URL = process.env.PREVIEW_URL || 'http://127.0.0.1:8080/';
const errors = [];
let failures = 0;
const check = (name, cond) => {
  console.log((cond ? 'PASS  ' : 'FAIL  ') + name);
  if (!cond) failures++;
};

JSDOM.fromURL(URL, {
  runScripts: 'dangerously',
  resources: 'usable',
  pretendToBeVisual: true,
  beforeParse(w) {
    w.matchMedia = w.matchMedia || ((q) => ({ matches: false, media: q, addEventListener() {}, removeEventListener() {} }));
    w.addEventListener('error', (e) => errors.push(e.message));
  }
}).then((dom) => {
  const { window } = dom;
  const doc = window.document;
  const click = (el) => el.dispatchEvent(new window.MouseEvent('click', { bubbles: true }));
  const wait = (ms) => new Promise((r) => setTimeout(r, ms));
  const payBtn = () => doc.getElementById('sheet').querySelector('[data-action="pay"]');

  setTimeout(async () => {
    check('four tabs render', doc.querySelectorAll('.tab').length === 4);
    check('scanner well renders', !!doc.querySelector('.well'));
    check('three demo codes offered', doc.querySelectorAll('.demo-chip').length === 3);

    // trusted merchant -> payable
    click(doc.querySelector('[data-action="demo"][data-i="0"]')); await wait(60);
    check('trusted merchant: sheet open', !doc.getElementById('sheet').hidden);
    check('trusted merchant: 5 guardrails shown', doc.getElementById('sheet').querySelectorAll('.check').length === 5);
    check('trusted merchant: none failed', doc.getElementById('sheet').querySelectorAll('.check.fail').length === 0);
    check('trusted merchant: pay enabled', !payBtn().disabled);
    click(doc.querySelector('[data-action="sheet-close"]')); await wait(40);

    // unverified + over daily cap -> gated even with override
    click(doc.querySelector('[data-action="demo"][data-i="2"]')); await wait(60);
    check('unverified merchant: safety card says unverified',
      doc.getElementById('sheet').querySelector('.safety-card').classList.contains('unverified'));
    check('over-cap amount: a guardrail fails', doc.getElementById('sheet').querySelectorAll('.check.fail').length >= 1);
    check('over-cap amount: pay disabled', payBtn().disabled);
    const ov = doc.getElementById('sheet').querySelector('[data-action="override"]');
    check('unverified merchant: override offered', !!ov);
    ov.checked = true; ov.dispatchEvent(new window.Event('change', { bubbles: true })); await wait(40);
    check('override does not bypass a failed guardrail', payBtn().disabled);
    click(doc.querySelector('[data-action="sheet-close"]')); await wait(40);

    // blocked merchant -> never payable, no override
    doc.getElementById('m-merchant').value = 'Phishing Test Merchant';
    doc.getElementById('m-amt').value = '10';
    click(doc.querySelector('[data-action="manual"]')); await wait(60);
    check('blocked merchant: safety card says blocked',
      doc.getElementById('sheet').querySelector('.safety-card').classList.contains('blocked'));
    check('blocked merchant: no override offered', !doc.getElementById('sheet').querySelector('[data-action="override"]'));
    check('blocked merchant: pay disabled', payBtn().disabled);
    click(doc.querySelector('[data-action="sheet-close"]')); await wait(40);

    // payment path with the biometric lock off: deducts, records, persists
    click(doc.querySelector('[data-action="tab"][data-tab="security"]')); await wait(40);
    click(doc.querySelector('[data-action="set-bio"]')); await wait(40);
    click(doc.querySelector('[data-action="tab"][data-tab="scan"]')); await wait(40);
    const before = doc.getElementById('balance-strip').textContent;
    click(doc.querySelector('[data-action="demo"][data-i="0"]')); await wait(60);
    click(payBtn()); await wait(150);
    check('payment success modal shown', !doc.getElementById('modal').hidden);
    click(doc.querySelector('[data-mi="0"]')); await wait(60);
    const after = doc.getElementById('balance-strip').textContent;
    check('balance deducted by SAR 15.00', before !== after && /836,865\.70/.test(after));
    click(doc.querySelector('[data-action="tab"][data-tab="activity"]')); await wait(60);
    check('history records the payment', doc.querySelectorAll('.tx').length === 1);
    const ls = JSON.parse(window.localStorage.getItem('qpqr.v1') || '{}');
    check('state persisted to localStorage', (ls.transactions || []).length === 1);

    check('no runtime errors', errors.length === 0);
    if (errors.length) console.log(errors);
    console.log(failures === 0 ? '\nALL CHECKS PASSED' : '\n' + failures + ' CHECK(S) FAILED');
    process.exit(failures === 0 ? 0 : 1);
  }, 1200);
});
