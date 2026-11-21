// ============================================================================
// HUPISAN (H.H.H.H) — SCRIPTER DESIGN & PROTOTYPE GENERATOR
// ============================================================================

print("Memulai pembuatan 10 layar Hupisan & Interactive Prototype...");

// 1. Bersihkan canvas
for (const child of [...figma.currentPage.children]) {
  child.remove();
}

// 2. Load Fonts
await figma.loadFontAsync({ family: "Inter", style: "Regular" });
await figma.loadFontAsync({ family: "Inter", style: "Medium" });
await figma.loadFontAsync({ family: "Inter", style: "Semi Bold" });
await figma.loadFontAsync({ family: "Inter", style: "Bold" });

// 3. Color Palette Hupisan
const C = {
  primary: { r: 0.133, g: 0.659, b: 0.447 },        // #22A872
  primaryDark: { r: 0.090, g: 0.533, b: 0.353 },    // #17885A
  primarySurface: { r: 0.910, g: 0.973, b: 0.941 }, // #E8F8F0
  primaryBorder: { r: 0.820, g: 0.949, b: 0.886 },  // #D1F2E2
  bg: { r: 0.965, g: 0.973, b: 0.969 },             // #F6F8F7
  white: { r: 1, g: 1, b: 1 },
  cardStroke: { r: 0.933, g: 0.949, b: 0.965 },     // #EEF2F6
  textPrimary: { r: 0.118, g: 0.161, b: 0.231 },    // #1E293B
  textSecondary: { r: 0.392, g: 0.455, b: 0.545 },  // #64748B
  textMuted: { r: 0.580, g: 0.639, b: 0.722 },      // #94A3B8
  debtRed: { r: 0.937, g: 0.267, b: 0.267 },        // #EF4444
  debtBg: { r: 0.996, g: 0.886, b: 0.886 },         // #FEE2E2
  debtText: { r: 0.863, g: 0.149, b: 0.149 },       // #DC2626
  recGreen: { r: 0.063, g: 0.725, b: 0.506 },       // #10B981
  recBg: { r: 0.820, g: 0.980, b: 0.898 },          // #D1FAE5
  recText: { r: 0.020, g: 0.588, b: 0.412 },        // #059669
  partialOrange: { r: 0.961, g: 0.620, b: 0.043 },  // #F59E0B
  partialBg: { r: 0.996, g: 0.953, b: 0.780 },      // #FEF3C7
  blue: { r: 0.231, g: 0.510, b: 0.965 },           // #3B82F6
  blueBg: { r: 0.937, g: 0.965, b: 1.0 },           // #EFF6FF
};

function solid(color, opacity = 1) {
  return [{ type: 'SOLID', color, opacity }];
}

function makeText(parent, text, size = 14, weight = 'Regular', color = C.textPrimary, options = {}) {
  const t = figma.createText();
  t.fontName = { family: 'Inter', style: weight };
  t.characters = text;
  t.fontSize = size;
  t.fills = solid(color);
  if (options.lineHeight) t.lineHeight = { value: options.lineHeight, unit: 'PIXELS' };
  if (options.letterSpacing) t.letterSpacing = { value: options.letterSpacing, unit: 'PIXELS' };
  if (options.name) t.name = options.name;
  if (parent) parent.appendChild(t);
  if (options.align) t.textAlignHorizontal = options.align;
  return t;
}

function makeAutoLayout(parent, dir = 'VERTICAL', options = {}) {
  const f = figma.createAutoLayout(dir);
  if (options.name) f.name = options.name;
  if (options.pad !== undefined) {
    f.paddingLeft = options.pad;
    f.paddingRight = options.pad;
    f.paddingTop = options.pad;
    f.paddingBottom = options.pad;
  }
  if (options.px !== undefined) { f.paddingLeft = options.px; f.paddingRight = options.px; }
  if (options.py !== undefined) { f.paddingTop = options.py; f.paddingBottom = options.py; }
  if (options.gap !== undefined) f.itemSpacing = options.gap;
  if (options.bg) f.fills = solid(options.bg, options.bgOpacity || 1);
  else f.fills = [];
  if (options.radius) f.cornerRadius = options.radius;
  if (options.stroke) {
    f.strokes = solid(options.stroke);
    f.strokeWeight = options.strokeWeight || 1;
  }
  if (options.width && options.height) {
    f.resize(options.width, options.height);
  }
  if (options.alignItems) {
    let a = options.alignItems;
    if (a === 'FLEX_END' || a === 'MAX') f.counterAxisAlignItems = 'MAX';
    else if (a === 'CENTER') f.counterAxisAlignItems = 'CENTER';
    else if (a === 'BASELINE') f.counterAxisAlignItems = 'BASELINE';
    else f.counterAxisAlignItems = 'MIN';
  }
  if (options.justifyContent) {
    let j = options.justifyContent;
    if (j === 'SPACE_BETWEEN') f.primaryAxisAlignItems = 'SPACE_BETWEEN';
    else if (j === 'CENTER') f.primaryAxisAlignItems = 'CENTER';
    else if (j === 'MAX' || j === 'FLEX_END') f.primaryAxisAlignItems = 'MAX';
    else f.primaryAxisAlignItems = 'MIN';
  }
  if (parent) parent.appendChild(f);
  return f;
}

function makeScreen(name, x, y) {
  const s = figma.createFrame();
  s.name = name;
  s.resize(390, 844);
  s.x = x;
  s.y = y;
  s.fills = solid(C.bg);
  s.clipsContent = true;
  s.cornerRadius = 40;
  s.strokes = solid(C.cardStroke);
  s.strokeWeight = 1;
  figma.currentPage.appendChild(s);
  return s;
}

function addStatusBar(screen, dark = false) {
  const bar = makeAutoLayout(screen, 'HORIZONTAL', {
    name: 'Status Bar',
    px: 24,
    py: 12,
    justifyContent: 'SPACE_BETWEEN',
    alignItems: 'CENTER'
  });
  bar.resize(390, 44);
  const color = dark ? C.white : C.textPrimary;
  makeText(bar, '09:41', 14, 'Semi Bold', color);
  
  const icons = makeAutoLayout(bar, 'HORIZONTAL', { gap: 6, alignItems: 'CENTER' });
  makeText(icons, '􀙇', 12, 'Semi Bold', color);
  makeText(icons, '􀛨', 12, 'Semi Bold', color);
  return bar;
}

function addBottomNav(screen, activeTab = 'beranda') {
  const nav = makeAutoLayout(screen, 'HORIZONTAL', {
    name: 'Bottom Navigation Bar',
    px: 16,
    py: 10,
    justifyContent: 'SPACE_BETWEEN',
    alignItems: 'CENTER',
    bg: C.white,
    stroke: C.cardStroke,
    strokeWeight: 1
  });
  nav.resize(390, 72);
  nav.y = 844 - 72;

  const tabs = [
    { id: 'nav_beranda', label: 'Beranda', icon: '🏠', key: 'beranda' },
    { id: 'nav_transaksi', label: 'Transaksi', icon: '📋', key: 'transaksi' },
    { id: 'nav_lunas', label: 'Lunas', icon: '✅', key: 'lunas' },
    { id: 'nav_statistik', label: 'Statistik', icon: '📊', key: 'statistik' },
    { id: 'nav_pengaturan', label: 'Pengaturan', icon: '⚙️', key: 'pengaturan' }
  ];

  const map = {};
  tabs.forEach(t => {
    const isActive = t.key === activeTab;
    const item = makeAutoLayout(nav, 'VERTICAL', {
      name: t.id,
      gap: 3,
      alignItems: 'CENTER',
      px: 8,
      py: 4
    });
    makeText(item, t.icon, 18, 'Regular', isActive ? C.primary : C.textMuted);
    makeText(item, t.label, 10, isActive ? 'Semi Bold' : 'Medium', isActive ? C.primary : C.textMuted);
    map[t.key] = item;
  });

  return { nav, tabs: map };
}

function addHeaderWithBack(screen, title) {
  const hdr = makeAutoLayout(screen, 'HORIZONTAL', {
    name: 'Screen Header',
    px: 20,
    py: 12,
    justifyContent: 'SPACE_BETWEEN',
    alignItems: 'CENTER'
  });
  hdr.resize(390, 56);
  hdr.y = 44;

  const left = makeAutoLayout(hdr, 'HORIZONTAL', { gap: 12, alignItems: 'CENTER' });
  const btnBack = makeAutoLayout(left, 'HORIZONTAL', {
    name: 'btn_back',
    width: 36,
    height: 36,
    radius: 18,
    bg: C.white,
    stroke: C.cardStroke,
    alignItems: 'CENTER',
    justifyContent: 'CENTER'
  });
  makeText(btnBack, '‹', 22, 'Bold', C.textPrimary);
  makeText(left, title, 18, 'Bold', C.textPrimary);

  const rightAction = makeAutoLayout(hdr, 'HORIZONTAL', {
    name: 'header_right_icon',
    width: 36,
    height: 36,
    radius: 18,
    bg: C.white,
    stroke: C.cardStroke,
    alignItems: 'CENTER',
    justifyContent: 'CENTER'
  });
  makeText(rightAction, '⋮', 18, 'Bold', C.textSecondary);

  return { hdr, btnBack, rightAction };
}

// 1. SCREEN 01: ONBOARDING / SPLASH
const s1 = makeScreen('01_Onboarding_Screen', 0, 0);
addStatusBar(s1, true);

const heroBg = figma.createFrame();
heroBg.name = 'Hero Illustration Area';
heroBg.resize(390, 480);
heroBg.fills = solid(C.primaryDark);
s1.appendChild(heroBg);

const decCircle = figma.createEllipse();
decCircle.resize(260, 260);
decCircle.x = 65;
decCircle.y = 80;
decCircle.fills = solid(C.primary, 0.4);
heroBg.appendChild(decCircle);

const logoCard = makeAutoLayout(heroBg, 'VERTICAL', {
  name: 'Logo Group',
  pad: 20,
  bg: C.white,
  radius: 28,
  alignItems: 'CENTER',
  gap: 12
});
logoCard.resize(130, 130);
logoCard.x = 130;
logoCard.y = 150;
makeText(logoCard, '💸', 44, 'Regular');
makeText(logoCard, 'HUPISAN', 13, 'Bold', C.primary);

const s1Content = makeAutoLayout(s1, 'VERTICAL', {
  name: 'Content Container',
  px: 24,
  py: 32,
  gap: 20,
  bg: C.white,
  radius: 32,
  alignItems: 'CENTER'
});
s1Content.resize(390, 380);
s1Content.y = 464;

const textWrap = makeAutoLayout(s1Content, 'VERTICAL', { gap: 10, alignItems: 'CENTER' });
makeText(textWrap, 'Kelola Catatan Keuangan\nLebih Rapi & Tenang', 22, 'Bold', C.textPrimary, { align: 'CENTER' });
makeText(textWrap, 'Catat hutang, piutang, pelunasan cicilan, serta kelola kocokan arisan komunitas dalam satu aplikasi aman.', 13, 'Regular', C.textSecondary, { align: 'CENTER', lineHeight: 20 });

const pillsRow = makeAutoLayout(s1Content, 'HORIZONTAL', { gap: 8, justifyContent: 'CENTER' });
['🛡️ 100% Aman', '🔔 Pengingat Tempo', '🎲 Kocok Arisan'].forEach(txt => {
  const pill = makeAutoLayout(pillsRow, 'HORIZONTAL', { px: 10, py: 6, bg: C.primarySurface, radius: 100 });
  makeText(pill, txt, 11, 'Semi Bold', C.primaryDark);
});

const btnGetStarted = makeAutoLayout(s1Content, 'HORIZONTAL', {
  name: 'btn_get_started',
  px: 24,
  py: 16,
  bg: C.primary,
  radius: 16,
  alignItems: 'CENTER',
  justifyContent: 'CENTER'
});
btnGetStarted.resize(342, 54);
makeText(btnGetStarted, 'Mulai Sekarang  ➔', 16, 'Bold', C.white);


// 2. SCREEN 02: MAIN DASHBOARD (BERANDA)
const s2 = makeScreen('02_Dashboard_Screen', 450, 0);
addStatusBar(s2, false);

const s2Header = makeAutoLayout(s2, 'HORIZONTAL', {
  name: 'Top Header',
  px: 20,
  py: 8,
  justifyContent: 'SPACE_BETWEEN',
  alignItems: 'CENTER'
});
s2Header.resize(390, 60);
s2Header.y = 44;

const userInfo = makeAutoLayout(s2Header, 'HORIZONTAL', { gap: 12, alignItems: 'CENTER' });
const avatar = makeAutoLayout(userInfo, 'VERTICAL', {
  name: 'Avatar',
  width: 42,
  height: 42,
  radius: 21,
  bg: C.primarySurface,
  alignItems: 'CENTER',
  justifyContent: 'CENTER'
});
makeText(avatar, '👨‍💼', 20, 'Regular');

const greetWrap = makeAutoLayout(userInfo, 'VERTICAL', { gap: 2 });
makeText(greetWrap, 'Selamat Datang,', 12, 'Medium', C.textMuted);
makeText(greetWrap, 'Andre Septiar 👋', 16, 'Bold', C.textPrimary);

const hdrIcons = makeAutoLayout(s2Header, 'HORIZONTAL', { gap: 8, alignItems: 'CENTER' });
const notifBtn = makeAutoLayout(hdrIcons, 'VERTICAL', {
  name: 'btn_notification',
  width: 36,
  height: 36,
  radius: 18,
  bg: C.white,
  stroke: C.cardStroke,
  alignItems: 'CENTER',
  justifyContent: 'CENTER'
});
makeText(notifBtn, '🔔', 15, 'Regular');

const settingsBtn = makeAutoLayout(hdrIcons, 'VERTICAL', {
  name: 'btn_header_settings',
  width: 36,
  height: 36,
  radius: 18,
  bg: C.white,
  stroke: C.cardStroke,
  alignItems: 'CENTER',
  justifyContent: 'CENTER'
});
makeText(settingsBtn, '⚙️', 15, 'Regular');

const s2Scroll = makeAutoLayout(s2, 'VERTICAL', {
  name: 'Dashboard Scroll Content',
  px: 20,
  py: 8,
  gap: 14
});
s2Scroll.resize(390, 660);
s2Scroll.y = 104;

const balCard = makeAutoLayout(s2Scroll, 'VERTICAL', {
  name: 'Ringkasan Saldo Card',
  pad: 18,
  bg: C.primaryDark,
  radius: 20,
  gap: 14
});
balCard.resize(350, 150);

const balHeader = makeAutoLayout(balCard, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
balHeader.resize(314, 20);
makeText(balHeader, 'Total Saldo Bersih', 12, 'Medium', { r: 0.8, g: 0.95, b: 0.88 });
const syncBadge = makeAutoLayout(balHeader, 'HORIZONTAL', { px: 8, py: 3, bg: C.primary, radius: 10 });
makeText(syncBadge, '● Terupdate', 10, 'Semi Bold', C.white);

makeText(balCard, 'Rp 14.500.000', 26, 'Bold', C.white);

const balMetrics = makeAutoLayout(balCard, 'HORIZONTAL', {
  justifyContent: 'SPACE_BETWEEN',
  px: 12,
  py: 8,
  bg: { r: 0.06, g: 0.42, b: 0.28 },
  radius: 12
});
balMetrics.resize(314, 48);

const piuWrap = makeAutoLayout(balMetrics, 'VERTICAL', { gap: 2 });
makeText(piuWrap, '↙ Piutang (Saya Pinjamkan)', 10, 'Medium', { r: 0.8, g: 0.95, b: 0.88 });
makeText(piuWrap, 'Rp 18.200.000', 13, 'Bold', C.white);

const hutWrap = makeAutoLayout(balMetrics, 'VERTICAL', { gap: 2, alignItems: 'MAX' });
makeText(hutWrap, '↗ Hutang (Kewajiban)', 10, 'Medium', { r: 0.8, g: 0.95, b: 0.88 });
makeText(hutWrap, 'Rp 3.700.000', 13, 'Bold', { r: 1, g: 0.75, b: 0.75 });

// Actions
const actionGrid = makeAutoLayout(s2Scroll, 'HORIZONTAL', {
  name: 'Primary Action Menu Cards',
  justifyContent: 'SPACE_BETWEEN'
});
actionGrid.resize(350, 78);

const btnAddHutang = makeAutoLayout(actionGrid, 'VERTICAL', { name: 'btn_add_hutang', alignItems: 'CENTER', gap: 6 });
const icHutang = makeAutoLayout(btnAddHutang, 'VERTICAL', { width: 50, height: 50, radius: 16, bg: C.debtBg, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(icHutang, '💸', 22, 'Regular');
makeText(btnAddHutang, 'Catat Hutang', 11, 'Semi Bold', C.textPrimary);

const btnAddPiutang = makeAutoLayout(actionGrid, 'VERTICAL', { name: 'btn_add_piutang', alignItems: 'CENTER', gap: 6 });
const icPiutang = makeAutoLayout(btnAddPiutang, 'VERTICAL', { width: 50, height: 50, radius: 16, bg: C.recBg, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(icPiutang, '💰', 22, 'Regular');
makeText(btnAddPiutang, 'Catat Piutang', 11, 'Semi Bold', C.textPrimary);

const btnMenuLunas = makeAutoLayout(actionGrid, 'VERTICAL', { name: 'btn_menu_lunas', alignItems: 'CENTER', gap: 6 });
const icLunas = makeAutoLayout(btnMenuLunas, 'VERTICAL', { width: 50, height: 50, radius: 16, bg: C.primarySurface, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(icLunas, '✅', 22, 'Regular');
makeText(btnMenuLunas, 'Data Lunas', 11, 'Semi Bold', C.textPrimary);

const btnQuickStats = makeAutoLayout(actionGrid, 'VERTICAL', { name: 'btn_quick_stats', alignItems: 'CENTER', gap: 6 });
const icStats = makeAutoLayout(btnQuickStats, 'VERTICAL', { width: 50, height: 50, radius: 16, bg: C.blueBg, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(icStats, '📊', 22, 'Regular');
makeText(btnQuickStats, 'Statistik', 11, 'Semi Bold', C.textPrimary);

// Banner Arisan
const reminderBanner = makeAutoLayout(s2Scroll, 'HORIZONTAL', {
  name: 'banner_arisan_alert',
  px: 14,
  py: 12,
  bg: C.primarySurface,
  stroke: C.primary,
  strokeWeight: 1,
  radius: 14,
  alignItems: 'CENTER',
  justifyContent: 'SPACE_BETWEEN'
});
reminderBanner.resize(350, 56);
const remLeft = makeAutoLayout(reminderBanner, 'HORIZONTAL', { gap: 10, alignItems: 'CENTER' });
makeText(remLeft, '🎯', 20, 'Regular');
const remText = makeAutoLayout(remLeft, 'VERTICAL', { gap: 2 });
makeText(remText, 'Arisan Keluarga Besar • Putaran ke-5', 12, 'Bold', C.primaryDark);
makeText(remText, 'Kocokan undian siap dimulai hari ini!', 10, 'Medium', C.textSecondary);
makeText(reminderBanner, 'Buka ➔', 11, 'Bold', C.primary);

// Filter
const filterRow = makeAutoLayout(s2Scroll, 'HORIZONTAL', { gap: 6 });
filterRow.resize(350, 32);
const fChips = [
  { label: 'Semua (14)', active: true },
  { label: 'Hutang (4)', active: false },
  { label: 'Piutang (8)', active: false },
  { label: 'Lunas (2)', active: false }
];
fChips.forEach(fc => {
  const cp = makeAutoLayout(filterRow, 'HORIZONTAL', {
    px: 12,
    py: 6,
    bg: fc.active ? C.primary : C.white,
    radius: 16,
    stroke: fc.active ? null : C.cardStroke
  });
  makeText(cp, fc.label, 11, fc.active ? 'Bold' : 'Medium', fc.active ? C.white : C.textSecondary);
});

// Recent Transactions
const txHeader = makeAutoLayout(s2Scroll, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
txHeader.resize(350, 20);
makeText(txHeader, 'Transaksi Terbaru', 13, 'Bold', C.textPrimary);
const btnSeeAllTx = makeText(txHeader, 'Lihat Semua ➔', 11, 'Semi Bold', C.primary, { name: 'btn_see_all_tx' });

const txList = makeAutoLayout(s2Scroll, 'VERTICAL', { gap: 8 });
txList.resize(350, 190);

const cardTxBudi = makeAutoLayout(txList, 'HORIZONTAL', {
  name: 'card_tx_budi',
  px: 14,
  py: 10,
  bg: C.white,
  radius: 14,
  stroke: C.cardStroke,
  strokeWeight: 1,
  justifyContent: 'SPACE_BETWEEN',
  alignItems: 'CENTER'
});
cardTxBudi.resize(350, 56);
const budiLeft = makeAutoLayout(cardTxBudi, 'HORIZONTAL', { gap: 10, alignItems: 'CENTER' });
const budiAv = makeAutoLayout(budiLeft, 'VERTICAL', { width: 34, height: 34, radius: 17, bg: C.debtBg, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(budiAv, '🔴', 13, 'Regular');
const budiInfo = makeAutoLayout(budiLeft, 'VERTICAL', { gap: 2 });
makeText(budiInfo, 'Budi Santoso', 13, 'Bold', C.textPrimary);
makeText(budiInfo, 'Tempo: 25 Nov 2026', 10, 'Regular', C.textSecondary);
const budiRight = makeAutoLayout(cardTxBudi, 'VERTICAL', { gap: 2, alignItems: 'MAX' });
makeText(budiRight, '- Rp 1.500.000', 12, 'Bold', C.debtText);
const budiBadge = makeAutoLayout(budiRight, 'HORIZONTAL', { px: 6, py: 2, bg: C.debtBg, radius: 6 });
makeText(budiBadge, 'Belum Lunas', 9, 'Bold', C.debtText);

const cardTxSiti = makeAutoLayout(txList, 'HORIZONTAL', {
  name: 'card_tx_siti',
  px: 14,
  py: 10,
  bg: C.white,
  radius: 14,
  stroke: C.cardStroke,
  strokeWeight: 1,
  justifyContent: 'SPACE_BETWEEN',
  alignItems: 'CENTER'
});
cardTxSiti.resize(350, 56);
const sitiLeft = makeAutoLayout(cardTxSiti, 'HORIZONTAL', { gap: 10, alignItems: 'CENTER' });
const sitiAv = makeAutoLayout(sitiLeft, 'VERTICAL', { width: 34, height: 34, radius: 17, bg: C.recBg, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(sitiAv, '🟢', 13, 'Regular');
const sitiInfo = makeAutoLayout(sitiLeft, 'VERTICAL', { gap: 2 });
makeText(sitiInfo, 'Siti Rahma', 13, 'Bold', C.textPrimary);
makeText(sitiInfo, 'Dicicil Rp 300rb / 500rb', 10, 'Regular', C.textSecondary);
const sitiRight = makeAutoLayout(cardTxSiti, 'VERTICAL', { gap: 2, alignItems: 'MAX' });
makeText(sitiRight, '+ Rp 200.000', 12, 'Bold', C.recText);
const sitiBadge = makeAutoLayout(sitiRight, 'HORIZONTAL', { px: 6, py: 2, bg: C.partialBg, radius: 6 });
makeText(sitiBadge, 'Sebagian', 9, 'Bold', C.partialOrange);

const s2Nav = addBottomNav(s2, 'beranda');


// 3. SCREEN 03: TRANSACTIONS FEED
const s3 = makeScreen('03_Transactions_Screen', 900, 0);
addStatusBar(s3, false);

const s3Top = makeAutoLayout(s3, 'HORIZONTAL', {
  name: 'Screen Header',
  px: 20,
  py: 12,
  justifyContent: 'SPACE_BETWEEN',
  alignItems: 'CENTER'
});
s3Top.resize(390, 56);
s3Top.y = 44;
makeText(s3Top, 'Daftar Transaksi Aktif', 18, 'Bold', C.textPrimary);
const s3Search = makeAutoLayout(s3Top, 'HORIZONTAL', { width: 36, height: 36, radius: 18, bg: C.white, stroke: C.cardStroke, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(s3Search, '🔍', 14, 'Regular');

const s3Tabs = makeAutoLayout(s3, 'HORIZONTAL', { name: 'Tab Selector', px: 20, gap: 8 });
s3Tabs.resize(390, 38);
s3Tabs.y = 104;
['Semua (14)', 'Hutang (4)', 'Piutang (8)'].forEach((t, i) => {
  const tab = makeAutoLayout(s3Tabs, 'HORIZONTAL', { px: 14, py: 8, bg: i === 0 ? C.primary : C.white, stroke: i === 0 ? null : C.cardStroke, radius: 20 });
  makeText(tab, t, 12, i === 0 ? 'Bold' : 'Medium', i === 0 ? C.white : C.textSecondary);
});

const s3List = makeAutoLayout(s3, 'VERTICAL', { name: 'Transactions List Container', px: 20, py: 10, gap: 10 });
s3List.resize(390, 610);
s3List.y = 148;

const allTrans = [
  { id: 'tx_item_budi', name: 'Budi Santoso', type: 'HUTANG', desc: 'Pinjaman modal usaha warung', due: 'Tempo: 25 Nov 2026', amount: 'Rp 1.500.000', paid: 'Sisa: Rp 1.000.000', status: 'Belum Lunas', bg: C.debtBg, col: C.debtText },
  { id: 'tx_item_siti', name: 'Siti Rahma', type: 'PIUTANG', desc: 'Talangan belanja bulanan', due: 'Tempo: 30 Nov 2026', amount: 'Rp 500.000', paid: 'Sisa: Rp 200.000', status: 'Sebagian', bg: C.partialBg, col: C.partialOrange },
  { id: 'tx_item_ahmad', name: 'Ahmad Fauzi', type: 'HUTANG', desc: 'Pinjaman service motor', due: 'Tempo: 05 Des 2026', amount: 'Rp 750.000', paid: 'Sisa: Rp 750.000', status: 'Belum Lunas', bg: C.debtBg, col: C.debtText },
  { id: 'tx_item_dewi', name: 'Dewi Lestari', type: 'PIUTANG', desc: 'Pinjaman pembelian kain', due: 'Tempo: 12 Des 2026', amount: 'Rp 2.000.000', paid: 'Sisa: Rp 2.000.000', status: 'Belum Lunas', bg: C.recBg, col: C.recText }
];

const s3Cards = {};
allTrans.forEach(t => {
  const c = makeAutoLayout(s3List, 'VERTICAL', { name: t.id, pad: 14, bg: C.white, radius: 16, stroke: C.cardStroke, strokeWeight: 1, gap: 10 });
  c.resize(350, 100);
  const top = makeAutoLayout(c, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
  top.resize(322, 22);
  const nameCol = makeAutoLayout(top, 'HORIZONTAL', { gap: 8, alignItems: 'CENTER' });
  const tag = makeAutoLayout(nameCol, 'HORIZONTAL', { px: 6, py: 2, bg: t.bg, radius: 4 });
  makeText(tag, t.type, 9, 'Bold', t.col);
  makeText(nameCol, t.name, 13, 'Bold', C.textPrimary);
  makeText(top, t.amount, 14, 'Bold', t.col);

  const bot = makeAutoLayout(c, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
  bot.resize(322, 18);
  makeText(bot, t.desc, 11, 'Regular', C.textSecondary);
  makeText(bot, t.paid, 11, 'Semi Bold', C.textPrimary);

  const foot = makeAutoLayout(c, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
  foot.resize(322, 16);
  makeText(foot, `📅 ${t.due}`, 10, 'Medium', C.textMuted);
  const stBadge = makeAutoLayout(foot, 'HORIZONTAL', { px: 6, py: 2, bg: t.bg, radius: 6 });
  makeText(stBadge, t.status, 9, 'Bold', t.col);
  s3Cards[t.id] = c;
});

const fabAdd = makeAutoLayout(s3, 'HORIZONTAL', { name: 'fab_add_transaction', px: 18, py: 14, bg: C.primary, radius: 28, alignItems: 'CENTER', gap: 8 });
fabAdd.resize(130, 48);
fabAdd.x = 240;
fabAdd.y = 844 - 72 - 60;
makeText(fabAdd, '➕', 14, 'Bold', C.white);
makeText(fabAdd, 'Catat Baru', 13, 'Bold', C.white);

const s3Nav = addBottomNav(s3, 'transaksi');


// 4. SCREEN 04: ADD TRANSACTION
const s4 = makeScreen('04_AddTransaction_Screen', 1350, 0);
addStatusBar(s4, false);
const s4Hdr = addHeaderWithBack(s4, 'Tambah Catatan Baru');

const s4Content = makeAutoLayout(s4, 'VERTICAL', { name: 'Form Container', px: 20, py: 12, gap: 14 });
s4Content.resize(390, 740);
s4Content.y = 100;

const toggleWrap = makeAutoLayout(s4Content, 'HORIZONTAL', { name: 'Type Toggle Selector', pad: 4, bg: C.cardStroke, radius: 14 });
toggleWrap.resize(350, 48);
const optHutang = makeAutoLayout(toggleWrap, 'HORIZONTAL', { name: 'opt_hutang', px: 20, py: 8, bg: C.debtRed, radius: 10, alignItems: 'CENTER', justifyContent: 'CENTER' });
optHutang.resize(171, 40);
makeText(optHutang, '🔴 Hutang (Saya Pinjam)', 12, 'Bold', C.white);
const optPiutang = makeAutoLayout(toggleWrap, 'HORIZONTAL', { name: 'opt_piutang', px: 20, py: 8, bg: null, radius: 10, alignItems: 'CENTER', justifyContent: 'CENTER' });
optPiutang.resize(171, 40);
makeText(optPiutang, '🟢 Piutang (Dipinjam)', 12, 'Medium', C.textSecondary);

const amountBox = makeAutoLayout(s4Content, 'VERTICAL', { pad: 16, bg: C.white, radius: 16, stroke: C.cardStroke, gap: 8 });
amountBox.resize(350, 100);
makeText(amountBox, 'Jumlah Nominal Transaksi', 11, 'Medium', C.textSecondary);
makeText(amountBox, 'Rp 1.500.000', 26, 'Bold', C.textPrimary);
const chipRow = makeAutoLayout(amountBox, 'HORIZONTAL', { gap: 6 });
['+50rb', '+100rb', '+500rb', '+1jt'].forEach(c => {
  const cp = makeAutoLayout(chipRow, 'HORIZONTAL', { px: 8, py: 4, bg: C.bg, radius: 8 });
  makeText(cp, c, 10, 'Semi Bold', C.textPrimary);
});

const nameBox = makeAutoLayout(s4Content, 'VERTICAL', { pad: 14, bg: C.white, radius: 14, stroke: C.cardStroke, gap: 6 });
nameBox.resize(350, 68);
makeText(nameBox, 'Nama Peminjam / Pemberi Pinjaman', 10, 'Medium', C.textSecondary);
makeText(nameBox, 'Budi Santoso', 14, 'Semi Bold', C.textPrimary);

const dateRow = makeAutoLayout(s4Content, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN' });
dateRow.resize(350, 68);
const dt1 = makeAutoLayout(dateRow, 'VERTICAL', { pad: 12, bg: C.white, radius: 14, stroke: C.cardStroke, gap: 4 });
dt1.resize(168, 68);
makeText(dt1, 'Tanggal Pinjam', 10, 'Medium', C.textSecondary);
makeText(dt1, '📅 20 Nov 2026', 12, 'Semi Bold', C.textPrimary);
const dt2 = makeAutoLayout(dateRow, 'VERTICAL', { pad: 12, bg: C.white, radius: 14, stroke: C.cardStroke, gap: 4 });
dt2.resize(168, 68);
makeText(dt2, 'Jatuh Tempo', 10, 'Medium', C.textSecondary);
makeText(dt2, '⏰ 25 Nov 2026', 12, 'Semi Bold', C.debtText);

const catBox = makeAutoLayout(s4Content, 'VERTICAL', { pad: 14, bg: C.white, radius: 14, stroke: C.cardStroke, gap: 8 });
catBox.resize(350, 76);
makeText(catBox, 'Pilih Kategori Transaksi', 10, 'Medium', C.textSecondary);
const catRow = makeAutoLayout(catBox, 'HORIZONTAL', { gap: 6 });
['Modal Usaha (Active)', 'Pribadi', 'Konsumsi', 'Darurat'].forEach((ct, idx) => {
  const cp = makeAutoLayout(catRow, 'HORIZONTAL', { px: 10, py: 6, bg: idx === 0 ? C.primarySurface : C.bg, radius: 8, stroke: idx === 0 ? C.primary : null });
  makeText(cp, ct, 10, idx === 0 ? 'Bold' : 'Regular', idx === 0 ? C.primaryDark : C.textSecondary);
});

const btnSaveTx = makeAutoLayout(s4Content, 'HORIZONTAL', { name: 'btn_save_transaction', px: 20, py: 16, bg: C.primary, radius: 16, alignItems: 'CENTER', justifyContent: 'CENTER' });
btnSaveTx.resize(350, 54);
makeText(btnSaveTx, 'Simpan Transaksi 💾', 15, 'Bold', C.white);


// 5. SCREEN 05: DETAIL TRANSACTION
const s5 = makeScreen('05_DetailTransaction_Screen', 1800, 0);
addStatusBar(s5, false);
const s5Hdr = addHeaderWithBack(s5, 'Detail Transaksi');

const s5Content = makeAutoLayout(s5, 'VERTICAL', { name: 'Detail Container', px: 20, py: 12, gap: 14 });
s5Content.resize(390, 740);
s5Content.y = 100;

const profileCard = makeAutoLayout(s5Content, 'HORIZONTAL', { pad: 16, bg: C.white, radius: 18, stroke: C.cardStroke, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
profileCard.resize(350, 74);
const pLeft = makeAutoLayout(profileCard, 'HORIZONTAL', { gap: 12, alignItems: 'CENTER' });
const pAv = makeAutoLayout(pLeft, 'VERTICAL', { width: 44, height: 44, radius: 22, bg: C.debtBg, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(pAv, '👨', 22, 'Regular');
const pInfo = makeAutoLayout(pLeft, 'VERTICAL', { gap: 2 });
makeText(pInfo, 'Budi Santoso', 15, 'Bold', C.textPrimary);
makeText(pInfo, '+62 812-3456-7890 • Warung', 11, 'Regular', C.textSecondary);
const pBadge = makeAutoLayout(profileCard, 'HORIZONTAL', { px: 8, py: 4, bg: C.debtBg, radius: 8 });
makeText(pBadge, 'Hutang Saya', 10, 'Bold', C.debtText);

const finCard = makeAutoLayout(s5Content, 'VERTICAL', { pad: 18, bg: C.white, radius: 20, stroke: C.cardStroke, gap: 14 });
finCard.resize(350, 160);
const finTop = makeAutoLayout(finCard, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
finTop.resize(314, 20);
makeText(finTop, 'Sisa Hutang Belum Lunas', 12, 'Medium', C.textSecondary);
const finAlert = makeAutoLayout(finTop, 'HORIZONTAL', { px: 8, py: 2, bg: C.debtBg, radius: 6 });
makeText(finAlert, 'Jatuh Tempo 5 Hari', 9, 'Bold', C.debtText);
makeText(finCard, 'Rp 1.000.000', 26, 'Bold', C.debtText);

const progBg = figma.createFrame();
progBg.name = 'Progress Bar Background';
progBg.resize(314, 8);
progBg.cornerRadius = 4;
progBg.fills = solid(C.cardStroke);
finCard.appendChild(progBg);

const progFill = figma.createFrame();
progFill.name = 'Progress Fill (33% Lunas)';
progFill.resize(105, 8);
progFill.cornerRadius = 4;
progFill.fills = solid(C.recGreen);
progBg.appendChild(progFill);

const finRow = makeAutoLayout(finCard, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN' });
finRow.resize(314, 20);
makeText(finRow, 'Total: Rp 1.500.000', 11, 'Medium', C.textSecondary);
makeText(finRow, 'Terbayar: Rp 500.000 (33%)', 11, 'Bold', C.recText);

const actRow = makeAutoLayout(s5Content, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN' });
actRow.resize(350, 50);
const btnPayPartial = makeAutoLayout(actRow, 'HORIZONTAL', { name: 'btn_pay_partial', px: 16, py: 14, bg: C.white, stroke: C.primary, strokeWeight: 1.5, radius: 14, alignItems: 'CENTER', justifyContent: 'CENTER' });
btnPayPartial.resize(168, 50);
makeText(btnPayPartial, 'Catat Cicilan 💳', 13, 'Bold', C.primary);
const btnPayFull = makeAutoLayout(actRow, 'HORIZONTAL', { name: 'btn_pay_full', px: 16, py: 14, bg: C.primary, radius: 14, alignItems: 'CENTER', justifyContent: 'CENTER' });
btnPayFull.resize(168, 50);
makeText(btnPayFull, 'Lunasi Transaksi ✨', 13, 'Bold', C.white);

const histList = makeAutoLayout(s5Content, 'VERTICAL', { gap: 8 });
histList.resize(350, 140);
const hist1 = makeAutoLayout(histList, 'HORIZONTAL', { pad: 12, bg: C.white, radius: 12, stroke: C.cardStroke, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
hist1.resize(350, 52);
const h1L = makeAutoLayout(hist1, 'HORIZONTAL', { gap: 8, alignItems: 'CENTER' });
makeText(h1L, '✅', 14, 'Regular');
const h1T = makeAutoLayout(h1L, 'VERTICAL', { gap: 2 });
makeText(h1T, 'Cicilan Ke-1 (Transfer BCA)', 12, 'Bold', C.textPrimary);
makeText(h1T, '10 Nov 2026 • 14:30 WIB', 10, 'Regular', C.textSecondary);
makeText(hist1, '+ Rp 500.000', 13, 'Bold', C.recText);


// 6. SCREEN 06: DATA TRANSAKSI LUNAS
const s6 = makeScreen('06_LunasHistory_Screen', 2250, 0);
addStatusBar(s6, false);
const s6Hdr = addHeaderWithBack(s6, 'Data Transaksi Lunas');

const s6Content = makeAutoLayout(s6, 'VERTICAL', { name: 'Lunas Container', px: 20, py: 12, gap: 14 });
s6Content.resize(390, 660);
s6Content.y = 100;

const lunasBanner = makeAutoLayout(s6Content, 'HORIZONTAL', { pad: 16, bg: C.recBg, radius: 18, stroke: C.recGreen, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
lunasBanner.resize(350, 76);
const lbL = makeAutoLayout(lunasBanner, 'VERTICAL', { gap: 4 });
makeText(lbL, 'Total Kewajiban & Hak Terselesaikan', 11, 'Medium', C.recText);
makeText(lbL, 'Rp 16.500.000 Lunas', 18, 'Bold', C.recText);
makeText(lunasBanner, '🏆', 30, 'Regular');

const lToggle = makeAutoLayout(s6Content, 'HORIZONTAL', { pad: 4, bg: C.cardStroke, radius: 12 });
lToggle.resize(350, 40);
const lt1 = makeAutoLayout(lToggle, 'HORIZONTAL', { px: 16, py: 6, bg: C.white, radius: 8, alignItems: 'CENTER', justifyContent: 'CENTER' });
lt1.resize(171, 32);
makeText(lt1, 'Semua Lunas (8)', 11, 'Bold', C.textPrimary);
const lt2 = makeAutoLayout(lToggle, 'HORIZONTAL', { px: 16, py: 6, bg: null, radius: 8, alignItems: 'CENTER', justifyContent: 'CENTER' });
lt2.resize(171, 32);
makeText(lt2, 'Piutang Selesai (5)', 11, 'Medium', C.textSecondary);

const lunasList = makeAutoLayout(s6Content, 'VERTICAL', { gap: 10 });
lunasList.resize(350, 480);
[
  { name: 'Doni Pratama', desc: 'Pelunasan sewa sound system', date: 'Lunas 18 Nov 2026', amount: 'Rp 3.000.000' },
  { name: 'Ibu Fatimah', desc: 'Arisan & belanja bahan kue', date: 'Lunas 15 Nov 2026', amount: 'Rp 1.200.000' },
  { name: 'Rian Hidayat', desc: 'Talangan servis laptop kantor', date: 'Lunas 04 Nov 2026', amount: 'Rp 850.000' }
].forEach(li => {
  const card = makeAutoLayout(lunasList, 'HORIZONTAL', { pad: 14, bg: C.white, radius: 14, stroke: C.cardStroke, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
  card.resize(350, 68);
  const left = makeAutoLayout(card, 'HORIZONTAL', { gap: 10, alignItems: 'CENTER' });
  const av = makeAutoLayout(left, 'VERTICAL', { width: 38, height: 38, radius: 19, bg: C.recBg, alignItems: 'CENTER', justifyContent: 'CENTER' });
  makeText(av, '✅', 16, 'Regular');
  const inf = makeAutoLayout(left, 'VERTICAL', { gap: 2 });
  makeText(inf, li.name, 13, 'Bold', C.textPrimary);
  makeText(inf, li.desc, 10, 'Regular', C.textSecondary);
  makeText(inf, li.date, 10, 'Medium', C.textMuted);
  const right = makeAutoLayout(card, 'VERTICAL', { gap: 4, alignItems: 'MAX' });
  makeText(right, li.amount, 13, 'Bold', C.recText);
  const bgL = makeAutoLayout(right, 'HORIZONTAL', { px: 6, py: 2, bg: C.recBg, radius: 6 });
  makeText(bgL, 'Lunas 100%', 9, 'Bold', C.recText);
});

const s6Nav = addBottomNav(s6, 'lunas');


// 7. SCREEN 07: ARISAN KOMUNITAS
const s7 = makeScreen('07_ArisanCommunity_Screen', 2700, 0);
addStatusBar(s7, false);
const s7Top = makeAutoLayout(s7, 'HORIZONTAL', { name: 'Screen Header', px: 20, py: 12, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
s7Top.resize(390, 56);
s7Top.y = 44;
makeText(s7Top, 'Arisan Komunitas', 18, 'Bold', C.textPrimary);
const s7Add = makeAutoLayout(s7Top, 'HORIZONTAL', { name: 'btn_add_arisan_group', px: 10, py: 6, bg: C.primarySurface, radius: 12, alignItems: 'CENTER', gap: 4 });
makeText(s7Add, '➕ Buat Grup', 11, 'Bold', C.primaryDark);

const s7Scroll = makeAutoLayout(s7, 'VERTICAL', { name: 'Arisan Scroll Content', px: 20, py: 8, gap: 14 });
s7Scroll.resize(390, 660);
s7Scroll.y = 104;

const abBanner = makeAutoLayout(s7Scroll, 'HORIZONTAL', { pad: 16, bg: C.primary, radius: 18, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
abBanner.resize(350, 76);
const abLeft = makeAutoLayout(abBanner, 'VERTICAL', { gap: 4 });
makeText(abLeft, 'Total Arisan Aktif', 11, 'Medium', { r: 0.85, g: 0.98, b: 0.92 });
makeText(abLeft, '2 Grup • 32 Anggota', 16, 'Bold', C.white);
makeText(abBanner, '🎲', 32, 'Regular');

const cardArisan1 = makeAutoLayout(s7Scroll, 'VERTICAL', { name: 'card_arisan_keluarga', pad: 16, bg: C.white, radius: 18, stroke: C.cardStroke, strokeWeight: 1, gap: 12 });
cardArisan1.resize(350, 155);
const c1Top = makeAutoLayout(cardArisan1, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
c1Top.resize(318, 24);
const c1Title = makeAutoLayout(c1Top, 'HORIZONTAL', { gap: 8, alignItems: 'CENTER' });
makeText(c1Title, '🏠', 18, 'Regular');
makeText(c1Title, 'Arisan Keluarga Besar', 14, 'Bold', C.textPrimary);
const c1Status = makeAutoLayout(c1Top, 'HORIZONTAL', { px: 8, py: 3, bg: C.recBg, radius: 6 });
makeText(c1Status, 'Siap Kocok 🎲', 10, 'Bold', C.recText);

const c1Met = makeAutoLayout(cardArisan1, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', bg: C.bg, pad: 10, radius: 10 });
c1Met.resize(318, 48);
const m1 = makeAutoLayout(c1Met, 'VERTICAL', { gap: 2 });
makeText(m1, 'Iuran/Bulan', 10, 'Medium', C.textSecondary);
makeText(m1, 'Rp 200.000', 12, 'Bold', C.textPrimary);
const m2 = makeAutoLayout(c1Met, 'VERTICAL', { gap: 2 });
makeText(m2, 'Anggota', 10, 'Medium', C.textSecondary);
makeText(m2, '12 Orang', 12, 'Bold', C.textPrimary);
const m3 = makeAutoLayout(c1Met, 'VERTICAL', { gap: 2, alignItems: 'MAX' });
makeText(m3, 'Pool Hadiah', 10, 'Medium', C.textSecondary);
makeText(m3, 'Rp 2.400.000', 12, 'Bold', C.primaryDark);

const c1Foot = makeAutoLayout(cardArisan1, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
c1Foot.resize(318, 20);
makeText(c1Foot, 'Putaran ke-5 dari 12 putaran', 11, 'Medium', C.textSecondary);
makeText(c1Foot, 'Buka & Kocok ➔', 12, 'Bold', C.primary);

const s7Nav = addBottomNav(s7, 'beranda');


// 8. SCREEN 08: KOCOK ARISAN
const s8 = makeScreen('08_KocokArisan_Screen', 3150, 0);
addStatusBar(s8, false);
const s8Hdr = addHeaderWithBack(s8, 'Kocok Undian Arisan');

const s8Content = makeAutoLayout(s8, 'VERTICAL', { name: 'Kocok Container', px: 20, py: 12, gap: 16 });
s8Content.resize(390, 740);
s8Content.y = 100;

const kInfo = makeAutoLayout(s8Content, 'VERTICAL', { pad: 16, bg: C.primaryDark, radius: 18, gap: 6 });
kInfo.resize(350, 84);
makeText(kInfo, 'Arisan Keluarga Besar', 16, 'Bold', C.white);
makeText(kInfo, 'Putaran ke-5 • Total Hadiah: Rp 2.400.000', 12, 'Medium', { r: 0.85, g: 0.98, b: 0.92 });

const tumblerBox = makeAutoLayout(s8Content, 'VERTICAL', { name: 'Tumbler Box Container', pad: 24, bg: C.white, radius: 24, stroke: C.primary, strokeWeight: 2, alignItems: 'CENTER', gap: 16 });
tumblerBox.resize(350, 230);
const diceIc = makeAutoLayout(tumblerBox, 'VERTICAL', { width: 72, height: 72, radius: 36, bg: C.primarySurface, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(diceIc, '🎲', 36, 'Regular');
const rollTitle = makeAutoLayout(tumblerBox, 'VERTICAL', { gap: 4, alignItems: 'CENTER' });
makeText(rollTitle, 'Siapakah Pemenang Beruntung?', 15, 'Bold', C.textPrimary);
makeText(rollTitle, '7 dari 12 anggota belum pernah menang', 11, 'Regular', C.textSecondary);
const btnRoll = makeAutoLayout(tumblerBox, 'HORIZONTAL', { name: 'btn_kocok_sekarang', px: 24, py: 14, bg: C.primary, radius: 14, alignItems: 'CENTER', justifyContent: 'CENTER', gap: 8 });
btnRoll.resize(280, 48);
makeText(btnRoll, 'Kocok Arisan Sekarang 🎲', 14, 'Bold', C.white);


// 9. SCREEN 09: STATISTIK
const s9 = makeScreen('09_Statistics_Screen', 3600, 0);
addStatusBar(s9, false);
const s9Top = makeAutoLayout(s9, 'HORIZONTAL', { name: 'Screen Header', px: 20, py: 12, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
s9Top.resize(390, 56);
s9Top.y = 44;
makeText(s9Top, 'Laporan & Statistik', 18, 'Bold', C.textPrimary);

const s9Scroll = makeAutoLayout(s9, 'VERTICAL', { name: 'Stats Scroll Content', px: 20, py: 8, gap: 14 });
s9Scroll.resize(390, 660);
s9Scroll.y = 104;

const healthCard = makeAutoLayout(s9Scroll, 'VERTICAL', { pad: 16, bg: C.white, radius: 18, stroke: C.cardStroke, gap: 10 });
healthCard.resize(350, 110);
const hTop = makeAutoLayout(healthCard, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
hTop.resize(318, 20);
makeText(hTop, 'Rasio Kesehatan Keuangan', 12, 'Bold', C.textPrimary);
const hGrade = makeAutoLayout(hTop, 'HORIZONTAL', { px: 8, py: 2, bg: C.recBg, radius: 6 });
makeText(hGrade, 'SEHAT (A)', 10, 'Bold', C.recText);
makeText(healthCard, 'Piutang Lebih Besar Dari Hutang', 13, 'Bold', C.primaryDark);
makeText(healthCard, 'Total piutang Anda mengcover 4.9x dari seluruh kewajiban hutang yang tercatat.', 11, 'Regular', C.textSecondary, { lineHeight: 16 });

const ratioCard = makeAutoLayout(s9Scroll, 'VERTICAL', { pad: 16, bg: C.white, radius: 18, stroke: C.cardStroke, gap: 14 });
ratioCard.resize(350, 130);
const rTop = makeAutoLayout(ratioCard, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN' });
rTop.resize(318, 20);
makeText(rTop, 'Perbandingan Komposisi', 12, 'Bold', C.textPrimary);
makeText(rTop, 'Total Rp 21.900.000', 11, 'Medium', C.textSecondary);

const dualBar = makeAutoLayout(ratioCard, 'HORIZONTAL', { radius: 6, clipsContent: true });
dualBar.resize(318, 14);
const barPiu = figma.createFrame(); barPiu.resize(264, 14); barPiu.fills = solid(C.recGreen); dualBar.appendChild(barPiu);
const barHut = figma.createFrame(); barHut.resize(54, 14); barHut.fills = solid(C.debtRed); dualBar.appendChild(barHut);

const legRow = makeAutoLayout(ratioCard, 'HORIZONTAL', { justifyContent: 'SPACE_BETWEEN' });
legRow.resize(318, 24);
const l1 = makeAutoLayout(legRow, 'HORIZONTAL', { gap: 6, alignItems: 'CENTER' });
const c1 = figma.createEllipse(); c1.resize(8, 8); c1.fills = solid(C.recGreen); l1.appendChild(c1);
makeText(l1, 'Piutang: 83%', 11, 'Bold', C.textPrimary);
const l2 = makeAutoLayout(legRow, 'HORIZONTAL', { gap: 6, alignItems: 'CENTER' });
const c2 = figma.createEllipse(); c2.resize(8, 8); c2.fills = solid(C.debtRed); l2.appendChild(c2);
makeText(l2, 'Hutang: 17%', 11, 'Bold', C.textPrimary);

const s9Nav = addBottomNav(s9, 'statistik');


// 10. SCREEN 10: PENGATURAN
const s10 = makeScreen('10_Settings_Screen', 4050, 0);
addStatusBar(s10, false);
const s10Hdr = addHeaderWithBack(s10, 'Pengaturan Aplikasi');

const s10Content = makeAutoLayout(s10, 'VERTICAL', { name: 'Settings Scroll Content', px: 20, py: 12, gap: 14 });
s10Content.resize(390, 660);
s10Content.y = 100;

const userCard = makeAutoLayout(s10Content, 'HORIZONTAL', { pad: 16, bg: C.white, radius: 18, stroke: C.cardStroke, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
userCard.resize(350, 74);
const uL = makeAutoLayout(userCard, 'HORIZONTAL', { gap: 12, alignItems: 'CENTER' });
const uAv = makeAutoLayout(uL, 'VERTICAL', { width: 44, height: 44, radius: 22, bg: C.primarySurface, alignItems: 'CENTER', justifyContent: 'CENTER' });
makeText(uAv, '👨‍💼', 22, 'Regular');
const uT = makeAutoLayout(uL, 'VERTICAL', { gap: 2 });
makeText(uT, 'Andre Septiar', 14, 'Bold', C.textPrimary);
makeText(uT, 'andriyan354354@gmail.com', 11, 'Regular', C.textSecondary);
makeText(userCard, 'Edit ➔', 12, 'Bold', C.primary);

const setGroup = makeAutoLayout(s10Content, 'VERTICAL', { bg: C.white, radius: 16, stroke: C.cardStroke, pad: 4 });
setGroup.resize(350, 200);
[
  { icon: '🌐', title: 'Bahasa (Language)', sub: 'Indonesia', action: 'Ganti ➔' },
  { icon: '🔔', title: 'Notifikasi & Pengingat Tempo', sub: 'Aktif setiap 09:00 WIB', action: 'ON' },
  { icon: '💾', title: 'Backup & Sinkronisasi Cloud', sub: 'Tersimpan otomatis', action: 'Backup' },
  { icon: '🔒', title: 'Kunci Aplikasi (Fingerprint/PIN)', sub: 'Keamanan biometric aktif', action: 'ON' }
].forEach(sm => {
  const r = makeAutoLayout(setGroup, 'HORIZONTAL', { pad: 12, justifyContent: 'SPACE_BETWEEN', alignItems: 'CENTER' });
  r.resize(342, 48);
  const rl = makeAutoLayout(r, 'HORIZONTAL', { gap: 10, alignItems: 'CENTER' });
  makeText(rl, sm.icon, 16, 'Regular');
  const rt = makeAutoLayout(rl, 'VERTICAL', { gap: 2 });
  makeText(rt, sm.title, 12, 'Bold', C.textPrimary);
  makeText(rt, sm.sub, 10, 'Regular', C.textSecondary);
  makeText(r, sm.action, 11, 'Semi Bold', C.primary);
});

const s10Nav = addBottomNav(s10, 'pengaturan');


// PROTOTYPE WIRE-UP
print("Menghubungkan Interactive Prototype...");
figma.currentPage.flowStartingPoints = [{ nodeId: s1.id, name: "Hupisan Mobile App Flow" }];

function linkClick(sourceNode, targetScreenNode) {
  if (!sourceNode || !targetScreenNode) return;
  sourceNode.reactions = [
    {
      trigger: { type: 'ON_CLICK' },
      actions: [
        {
          type: 'NODE',
          destinationId: targetScreenNode.id,
          navigation: 'NAVIGATE',
          transition: { type: 'SMART_ANIMATE', easing: { type: 'EASE_OUT' }, duration: 0.3 }
        }
      ]
    }
  ];
}

linkClick(btnGetStarted, s2);
linkClick(settingsBtn, s10);
linkClick(btnAddHutang, s4);
linkClick(btnAddPiutang, s4);
linkClick(btnMenuLunas, s6);
linkClick(btnQuickStats, s9);
linkClick(reminderBanner, s7);
linkClick(btnSeeAllTx, s3);
linkClick(cardTxBudi, s5);

linkClick(s2Nav.tabs['transaksi'], s3);
linkClick(s2Nav.tabs['lunas'], s6);
linkClick(s2Nav.tabs['statistik'], s9);
linkClick(s2Nav.tabs['pengaturan'], s10);

linkClick(s3Nav.tabs['beranda'], s2);
linkClick(s3Nav.tabs['lunas'], s6);
linkClick(s3Nav.tabs['statistik'], s9);
linkClick(s3Nav.tabs['pengaturan'], s10);
linkClick(fabAdd, s4);
if (s3Cards['tx_item_budi']) linkClick(s3Cards['tx_item_budi'], s5);
if (s3Cards['tx_item_siti']) linkClick(s3Cards['tx_item_siti'], s5);

linkClick(s4Hdr.btnBack, s2);
linkClick(btnSaveTx, s3);

linkClick(s5Hdr.btnBack, s3);
linkClick(btnPayFull, s6);

linkClick(s6Hdr.btnBack, s2);
linkClick(s6Nav.tabs['beranda'], s2);
linkClick(s6Nav.tabs['transaksi'], s3);
linkClick(s6Nav.tabs['statistik'], s9);
linkClick(s6Nav.tabs['pengaturan'], s10);

linkClick(cardArisan1, s8);
linkClick(s7Nav.tabs['transaksi'], s3);
linkClick(s7Nav.tabs['lunas'], s6);
linkClick(s7Nav.tabs['statistik'], s9);
linkClick(s7Nav.tabs['pengaturan'], s10);

linkClick(s8Hdr.btnBack, s7);

linkClick(s9Nav.tabs['beranda'], s2);
linkClick(s9Nav.tabs['transaksi'], s3);
linkClick(s9Nav.tabs['lunas'], s6);
linkClick(s9Nav.tabs['pengaturan'], s10);

linkClick(s10Hdr.btnBack, s2);
linkClick(s10Nav.tabs['beranda'], s2);
linkClick(s10Nav.tabs['transaksi'], s3);
linkClick(s10Nav.tabs['lunas'], s6);
linkClick(s10Nav.tabs['statistik'], s9);

figma.viewport.scrollAndZoomIntoView([s1, s2, s3, s4, s5, s6, s7, s8, s9, s10]);
print("🎉 Selesai! 10 Layar Hupisan & Interactive Prototype Berhasil Dibuat di Canvas!");
