const KEY_PAIR_STORAGE_KEY = 'user_rsa_keypair'

export async function ensureKeyPairExists() {
  const stored = localStorage.getItem(KEY_PAIR_STORAGE_KEY)
  if (stored) return JSON.parse(stored)

  // 生成新密钥对
  const keyPair = await crypto.subtle.generateKey(
    { name: 'RSA-OAEP', modulusLength: 2048, publicExponent: new Uint8Array([1, 0, 1]), hash: 'SHA-256' },
    true,
    ['encrypt', 'decrypt']
  )

  // 导出公钥（上传）
  const spki = await crypto.subtle.exportKey('spki', keyPair.publicKey)
  const pemPublicKey = spkiToPem(spki)

  // 导出私钥（本地存储）
  const pkcs8 = await crypto.subtle.exportKey('pkcs8', keyPair.privateKey)
  const pemPrivateKey = pkcs8ToPem(pkcs8)

  const keyPairData = { publicKey: pemPublicKey, privateKey: pemPrivateKey }
  localStorage.setItem(KEY_PAIR_STORAGE_KEY, JSON.stringify(keyPairData))

  return keyPairData
}

// 辅助函数：SPKI → PEM
function spkiToPem(spkiBuffer) {
  const base64 = arrayBufferToBase64(spkiBuffer)
  return `-----BEGIN PUBLIC KEY-----\n${base64.match(/.{1,64}/g).join('\n')}\n-----END PUBLIC KEY-----`
}

// 辅助函数：PKCS#8 → PEM
function pkcs8ToPem(pkcs8Buffer) {
  const base64 = arrayBufferToBase64(pkcs8Buffer)
  return `-----BEGIN PRIVATE KEY-----\n${base64.match(/.{1,64}/g).join('\n')}\n-----END PRIVATE KEY-----`
}

function arrayBufferToBase64(buffer) {
  let binary = ''
  const bytes = new Uint8Array(buffer)
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary)
}
