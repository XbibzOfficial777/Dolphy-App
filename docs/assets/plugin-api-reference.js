const sdkText = {
  "id": {
    "search": "Cari metode, parameter, atau kapabilitas",
    "all": "Semua",
    "methods": "metode",
    "publicApi": "API publik",
    "noResults": "Tidak ada metode API yang cocok dengan pencarian ini.",
    "parameters": "Parameter",
    "returns": "Return",
    "callback": "Callback",
    "requirements": "Persyaratan",
    "example": "Contoh",
    "noParameters": "Tidak ada parameter.",
    "optional": "opsional",
    "defaultValue": "Default",
    "callbackReturn": "Pemanggilan langsung selesai. Hasil akhir dikirim ke callback.",
    "callbackJson": "Callback String biasanya menerima teks JSON. Konversikan dengan json.loads(str(value)). Callback Boolean menerima True atau False.",
    "booleanCheck": "Status Boolean: true jika kondisi terpenuhi, selain itu false.",
    "booleanAction": "true jika operasi diterima; false jika tidak didukung, ditolak, atau gagal.",
    "jsonReturn": "Teks JSON. Konversikan dengan json.loads(str(value)).",
    "nullableReturn": "Nilai yang dideklarasikan atau None jika tidak tersedia atau operasi gagal.",
    "unitReturn": "Tidak ada nilai return.",
    "nodeReturn": "Node UI native yang kompatibel dengan JSON dan dirender oleh Dolphy.",
    "directReturn": "Mengembalikan nilai Java/Python yang dideklarasikan. Object Java tersedia melalui Chaquopy.",
    "general": "Berjalan dalam proses plugin. Tangani exception Java dan Python di sekitar pemanggilan platform langsung.",
    "ui": "Izin Android tidak diperlukan. Kembalikan node ini dari layar plugin atau builder UI lain.",
    "files": "Path string bersifat relatif terhadap direktori privat plugin ini kecuali File atau Uri secara eksplisit diperlukan.",
    "bluetooth": "Memerlukan perangkat keras Bluetooth kompatibel dan izin Android terkait. Android 12+ memisahkan akses scan, connect, dan advertise.",
    "wifi": "Memerlukan perangkat keras Wi-Fi. Operasi scan dan nearby dapat memerlukan izin Location atau Nearby Wi-Fi, bergantung pada versi Android.",
    "nfc": "Memerlukan perangkat keras NFC dan adaptor aktif. Metode tag memerlukan tag yang diteruskan ke Dolphy saat plugin aktif.",
    "infrared": "Memerlukan pemancar IR bawaan atau service pemancar eksternal yang disediakan plugin.",
    "network": "Memerlukan akses jaringan. Validasi host, payload, dan batas ukuran yang tidak tepercaya dalam kode plugin.",
    "root": "Memerlukan Root yang berfungsi. Callback perintah menggunakan {ok, code, out, err, via}; selalu periksa ok dan code.",
    "shizuku": "Shizuku harus berjalan dan izin telah diberikan kepada Dolphy.",
    "notification": "Android 13+ dapat memerlukan POST_NOTIFICATIONS sebelum notifikasi ditampilkan.",
    "location": "Data lokasi dapat memerlukan izin lokasi runtime dan provider yang aktif.",
    "sensor": "Hentikan listener saat unload untuk mencegah kebocoran dan penggunaan baterai di latar belakang.",
    "dex": "Menjalankan kode native tepercaya dalam proses Dolphy. Verifikasi SHA-256, simpan payload secara privat, dan lepaskan semua resource saat unload.",
    "resource": "Pemanggilan ini dapat memiliki resource berumur panjang. Hentikan secara eksplisit atau panggil release() dari on_plugin_unload.",
    "does": "Menjalankan",
    "checks": "Memeriksa",
    "reads": "Membaca",
    "writes": "Menulis",
    "starts": "Memulai",
    "stops": "Menghentikan",
    "opens": "Membuka",
    "creates": "Membuat",
    "deletes": "Menghapus",
    "sends": "Mengirim",
    "registers": "Mendaftarkan",
    "through": "melalui"
  }
}

const sdkParams = {
  "callback": "Fungsi yang menerima hasil asinkron.",
  "onEvent": "Fungsi yang dipanggil untuk setiap event state atau data.",
  "onDeviceJs": "Fungsi yang menerima batch JSON perangkat yang ditemukan.",
  "onResult": "Fungsi yang menerima hasil asinkron.",
  "cb": "Penerima event; berikan None untuk melepaskannya.",
  "path": "Path di dalam penyimpanan privat plugin.",
  "address": "Alamat MAC Bluetooth.",
  "serviceUuid": "UUID service GATT.",
  "charUuid": "UUID characteristic GATT.",
  "descUuid": "UUID descriptor GATT.",
  "scanId": "Identifier stabil yang digunakan untuk menghentikan pemindaian ini.",
  "command": "Teks perintah shell.",
  "enabled": "Status aktif yang diinginkan.",
  "hex": "Byte heksadesimal dengan jumlah digit genap.",
  "payloadHex": "Payload advertising dalam bentuk byte heksadesimal.",
  "recordsJson": "Array JSON yang mendeskripsikan record NDEF.",
  "headersJson": "Object JSON berisi header HTTP, atau None.",
  "maxBytes": "Jumlah byte maksimum yang diterima.",
  "timeoutMs": "Timeout dalam milidetik.",
  "freqHz": "Frekuensi carrier dalam hertz.",
  "pattern": "Durasi mark dan space yang bergantian dalam mikrodetik.",
  "key": "Kunci stabil atau nama properti platform.",
  "value": "Nilai baru.",
  "url": "URL absolut.",
  "on_change": "Callback yang menerima nilai baru komponen.",
  "on_click": "Callback click tanpa argumen.",
  "on_select": "Callback yang menerima indeks atau nilai yang dipilih."
}

function sdkEscape(value) {
  return String(value).replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]))
}

function sdkSplit(source) {
  if (!source.trim()) return []
  const result = []
  let current = ''
  let depth = 0
  for (const char of source) {
    if ('(<[{'.includes(char)) depth += 1
    if (')>]}'.includes(char)) depth -= 1
    if (char === ',' && depth === 0) {
      result.push(current.trim())
      current = ''
    } else current += char
  }
  if (current.trim()) result.push(current.trim())
  return result
}

function sdkInferReturn(name, group) {
  if (group === 'ui') return 'UiNode'
  if (/^load_dex/.test(name)) return 'ClassLoader'
  if (name === 'dex_class' || /^import_java_class$/.test(name)) return 'Class?'
  if (/^new_dex_instance$|^import_java_object$/.test(name)) return 'Object?'
  if (/^export_(dex|app|java)/.test(name)) return 'Boolean'
  if (name === 'java_exports') return 'Object'
  if (/^(add_module|navigate|navigate_app|refresh|set_setting|toast|hook_screen|provide_service|hook_action|register)/.test(name)) return 'Unit'
  return 'Any'
}

function sdkParse(signature, group) {
  const arrow = signature.lastIndexOf(' -> ')
  const declaration = arrow >= 0 ? signature.slice(0, arrow) : signature
  const open = declaration.indexOf('(')
  const close = declaration.lastIndexOf(')')
  const name = open >= 0 ? declaration.slice(0, open) : declaration
  const parts = open >= 0 && close >= open ? sdkSplit(declaration.slice(open + 1, close)) : []
  return {
    name,
    returns: arrow >= 0 ? signature.slice(arrow + 4) : sdkInferReturn(name, group),
    params: parts.map(item => {
      const equals = item.indexOf('=')
      const left = (equals >= 0 ? item.slice(0, equals) : item).trim()
      const colon = left.indexOf(':')
      return {name: (colon >= 0 ? left.slice(0, colon) : left).trim(), type: (colon >= 0 ? left.slice(colon + 1) : 'Any').trim(), defaultValue: equals >= 0 ? item.slice(equals + 1).trim() : ''}
    })
  }
}

function sdkWords(name) {
  return name.replace(/Json$/, ' JSON').replace(/([a-z0-9])([A-Z])/g, '$1 $2').replace(/_/g, ' ').toLowerCase()
}

function sdkDescription(parsed, owner, text, lang) {
  const name = parsed.name
  let verb = text.does
  if (/^(is|has)|Supports|Available|Enabled/.test(name)) verb = text.checks
  else if (/(Get|Read|List|Info|Status|Capabilities|Stat|Results|Last|^get)/.test(name)) verb = text.reads
  else if (/(Write|Set|Put|Append|Import|Push)/.test(name)) verb = text.writes
  else if (/(Start|Connect|Discover|Play|Enable|Install|Request)/.test(name)) verb = text.starts
  else if (/(Stop|Disconnect|Cancel|Disable|Release|Unload)/.test(name)) verb = text.stops
  else if (/(Open|Share|Dial|Navigate)/.test(name)) verb = text.opens
  else if (/(Create|Mkdir|Add)/.test(name)) verb = text.creates
  else if (/(Delete|Remove|Uninstall|Kill)/.test(name)) verb = text.deletes
  else if (/(Send|Transmit|Notify|Broadcast)/.test(name)) verb = text.sends
  else if (/(Register|Hook|Provide)/.test(name)) verb = text.registers
  return verb + ' operasi «' + sdkWords(name) + '» ' + text.through + ' ' + owner + '.'
}

function sdkRequirement(parsed, group, text) {
  const name = parsed.name
  const result = []
  if (group === 'ui') result.push(text.ui)
  else if (/^(files|pluginFiles|importUri|exportSandbox|assets|createCamera|importCamera)/.test(name)) result.push(text.files)
  else if (/^(bt|ble|gatt)/.test(name)) result.push(text.bluetooth)
  else if (/^wifi/.test(name)) result.push(text.wifi)
  else if (/^(nfc|onNfc)/.test(name)) result.push(text.nfc)
  else if (/^ir/.test(name)) result.push(text.infrared)
  else if (/^(http|tcp|ping|portScan|network|netInterfaces|nsd)/.test(name)) result.push(text.network)
  else if (/^root/.test(name)) result.push(text.root)
  else if (/^shizuku/.test(name)) result.push(text.shizuku)
  else if (/^notify/.test(name)) result.push(text.notification)
  else if (/^location/.test(name)) result.push(text.location)
  else if (/^sensor/.test(name)) result.push(text.sensor)
  else if (/(Dex|dex|java_class|java_object|java_exports)/.test(name)) result.push(text.dex)
  else result.push(text.general)
  if (/(Start|Connect|Discover|Play|Advertise|Server)/.test(name) && !/(Stop|Disconnect)/.test(name)) result.push(text.resource)
  return result
}

function sdkReturn(parsed, text) {
  if (parsed.params.some(param => /callback|onEvent|onResult|onDeviceJs|cb/.test(param.name))) return text.callbackReturn
  if (parsed.returns === 'Boolean') return /^(is|has)|Supports|Available|Enabled/.test(parsed.name) ? text.booleanCheck : text.booleanAction
  if (parsed.returns === 'String' && /Json$/.test(parsed.name)) return text.jsonReturn
  if (parsed.returns.endsWith('?')) return text.nullableReturn
  if (parsed.returns === 'Unit') return text.unitReturn
  if (parsed.returns === 'UiNode') return text.nodeReturn
  return text.directReturn
}

function sdkExampleValue(param) {
  if (/callback|onEvent|onResult|onDeviceJs|cb/.test(param.name)) return 'self.on_result'
  if (param.defaultValue) return ''
  if (param.name === 'freqHz' || param.name === 'frequency') return '38000'
  if (param.name === 'pattern') return '[9000, 4500, 560, 560]'
  if (/address/i.test(param.name)) return '"AA:BB:CC:DD:EE:FF"'
  if (/uuid/i.test(param.name)) return '"0000180f-0000-1000-8000-00805f9b34fb"'
  if (/path|file/i.test(param.name)) return '"data/output.bin"'
  if (/url/i.test(param.name)) return '"https://example.org"'
  if (/hex|b64|data/i.test(param.name)) return '"01020304"'
  if (/json/i.test(param.name)) return '"{}"'
  if (/text|title|name|key|action|command|host|iface|pkg|service|mode/i.test(param.name)) return '"value"'
  if (/Array|List/.test(param.type)) return '[]'
  if (/Boolean/.test(param.type)) return 'True'
  if (/Int|Long|Float|Double/.test(param.type)) return '0'
  if (param.type.endsWith('?')) return 'None'
  return 'value'
}

function sdkExample(parsed, owner) {
  const values = parsed.params.map(sdkExampleValue).filter(Boolean)
  const call = owner + '.' + parsed.name + '(' + values.join(', ') + ')'
  if (parsed.params.some(param => /callback|onEvent|onResult|onDeviceJs|cb/.test(param.name))) return 'def on_result(self, value):\n    print(value)\n\n' + call
  return (parsed.returns === 'Unit' ? '' : 'result = ') + call
}

function sdkCatalog() {
  const groups = []
  const seen = new Set()
  document.querySelectorAll('.api-reference details').forEach(details => {
    const strong = details.querySelector('summary strong')
    if (!strong) return
    let group = ''
    let owner = ''
    if (strong.matches('[data-i18n="deviceCompleteTitle"]')) { group = 'device'; owner = 'self.api.getDevice()' }
    else if (strong.textContent.trim() === 'BasePlugin') { group = 'base'; owner = 'self' }
    else if (strong.textContent.trim() === 'UiFactory') { group = 'ui'; owner = 'self.ui' }
    else if (strong.textContent.trim() === 'api') { group = 'bridge'; owner = 'self.api' }
    else if (strong.textContent.includes('getAndroid')) { group = 'android'; owner = 'self.api.getAndroid()' }
    else if (strong.textContent.includes('getDevice')) { group = 'device'; owner = 'self.api.getDevice()' }
    if (!group) return
    details.querySelectorAll('.api-chips code').forEach(code => {
      const parsed = sdkParse(code.textContent.trim(), group)
      const key = group + ':' + parsed.name
      if (seen.has(key)) return
      seen.add(key)
      groups.push({group, owner, signature: code.textContent.trim(), parsed})
    })
  })
  return groups
}

function sdkRender() {
  const root = document.querySelector('[data-sdk-reference]')
  if (!root) return
  const lang = 'id'
  const text = sdkText[lang]
  const query = (root.querySelector('[data-sdk-search]').value || '').trim().toLowerCase()
  const selected = root.dataset.layer || 'all'
  const entries = sdkCatalog().filter(item => (selected === 'all' || item.group === selected) && (!query || (item.owner + ' ' + item.signature + ' ' + sdkWords(item.parsed.name)).toLowerCase().includes(query)))
  root.querySelector('[data-sdk-count]').textContent = selected === 'all' && !query ? text.publicApi : entries.length + ' ' + text.methods
  const list = root.querySelector('[data-sdk-list]')
  if (!entries.length) {
    list.innerHTML = '<div class="sdk-empty"><span class="material-symbols-rounded">search_off</span><p>' + text.noResults + '</p></div>'
    return
  }
  list.innerHTML = entries.map(item => {
    const parsed = item.parsed
    const params = parsed.params.length ? parsed.params.map(param => {
      const known = sdkParams[param.name]
      const fallback = 'Nilai ' + param.name + ' yang diteruskan sebagai ' + param.type + '.'
      const flags = [param.type.endsWith('?') ? text.optional : '', param.defaultValue ? text.defaultValue + ': ' + param.defaultValue : ''].filter(Boolean).join(' · ')
      return '<div><code>' + sdkEscape(param.name) + '</code><span><b>' + sdkEscape(param.type) + '</b> · ' + sdkEscape(known || fallback) + (flags ? '<small>' + sdkEscape(flags) + '</small>' : '') + '</span></div>'
    }).join('') : '<p class="sdk-none">' + text.noParameters + '</p>'
    const requirements = sdkRequirement(parsed, item.group, text).map(value => '<li>' + sdkEscape(value) + '</li>').join('')
    const callback = parsed.params.some(param => /callback|onEvent|onResult|onDeviceJs|cb/.test(param.name)) ? '<h4>' + text.callback + '</h4><p>' + sdkEscape(text.callbackJson) + '</p>' : ''
    const anchor = 'sdk-' + item.group + '-' + parsed.name.toLowerCase().replace(/[^a-z0-9]+/g, '-')
    return '<details class="sdk-method" id="' + anchor + '"><summary><span class="sdk-owner">' + sdkEscape(item.owner) + '</span><strong>' + sdkEscape(parsed.name) + '</strong><code>' + sdkEscape(parsed.returns) + '</code><span class="material-symbols-rounded">expand_more</span></summary><div class="sdk-method-body"><p class="sdk-description">' + sdkEscape(sdkDescription(parsed, item.owner, text, lang)) + '</p><pre><code>' + sdkEscape(item.owner + '.' + item.signature.replace(' -> ', ' → ')) + '</code></pre><div class="sdk-detail-grid"><section><h4>' + text.parameters + '</h4><div class="sdk-params">' + params + '</div></section><section><h4>' + text.returns + '</h4><p>' + sdkEscape(sdkReturn(parsed, text)) + '</p>' + callback + '</section></div><h4>' + text.requirements + '</h4><ul class="sdk-requirements">' + requirements + '</ul><h4>' + text.example + '</h4><pre><code>' + sdkEscape(sdkExample(parsed, item.owner)) + '</code></pre></div></details>'
  }).join('')
}

function sdkInit() {
  const root = document.querySelector('[data-sdk-reference]')
  if (!root) return
  const search = root.querySelector('[data-sdk-search]')
  const filters = root.querySelector('[data-sdk-filters]')
  const synchronize = () => {
    const text = sdkText.id
    search.placeholder = text.search
    root.querySelector('[data-sdk-all]').textContent = text.all
    sdkRender()
  }
  filters.addEventListener('click', event => {
    const button = event.target.closest('[data-sdk-layer]')
    if (!button) return
    root.dataset.layer = button.dataset.sdkLayer
    filters.querySelectorAll('button').forEach(item => item.classList.toggle('selected', item === button))
    sdkRender()
  })
  search.addEventListener('input', sdkRender)
  new MutationObserver(synchronize).observe(document.documentElement, {attributes: true, attributeFilter: ['lang']})
  synchronize()
}

document.addEventListener('DOMContentLoaded', sdkInit)
