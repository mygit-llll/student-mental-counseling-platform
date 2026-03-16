/**
 * 将文本转为 Uint8Array
 */
function strToUint8(str) {
  return new TextEncoder().encode(str)
}

/**
 * 将 Uint8Array 转为 Base64 字符串
 */
function uint8ToBase64(buffer) {
  let binary = ''
  const bytes = new Uint8Array(buffer)
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary)
}

/**
 * 将 Base64 字符串转为 Uint8Array
 */
function base64ToUint8(base64) {
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i)
  }
  return bytes
}

/**
 * AES-GCM 加密
 * @param {string} plaintext - 明文
 * @param {string} keyStr - 32字节密钥（字符串形式）
 * @returns {Promise<string>} 格式: base64(iv):base64(encrypted):base64(authTag)
 */
export async function encryptAESGCM(plaintext, keyStr) {
  const key = await crypto.subtle.importKey(
    'raw',
    strToUint8(keyStr),
    { name: 'AES-GCM' },
    false,
    ['encrypt']
  )

  const iv = crypto.getRandomValues(new Uint8Array(12)) // 96-bit IV
  const encoded = strToUint8(plaintext)

  const encrypted = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv },
    key,
    encoded
  )

  // 分离 authTag（最后16字节）
  const encryptedBuffer = new Uint8Array(encrypted)
  const authTag = encryptedBuffer.slice(-16)
  const ciphertext = encryptedBuffer.slice(0, -16)

  return [
    uint8ToBase64(iv),
    uint8ToBase64(ciphertext),
    uint8ToBase64(authTag)
  ].join(':')
}

/**
 * AES-GCM 解密
 * @param {string} encryptedData - 格式: base64(iv):base64(ciphertext):base64(authTag)
 * @param {string} keyStr - 32字节密钥
 * @returns {Promise<string>} 明文
 */
export async function decryptAESGCM(encryptedData, keyStr) {
  const parts = encryptedData.split(':')
  if (parts.length !== 3) throw new Error('Invalid encrypted format')

  const [ivB64, ciphertextB64, authTagB64] = parts
  const iv = base64ToUint8(ivB64)
  const ciphertext = base64ToUint8(ciphertextB64)
  const authTag = base64ToUint8(authTagB64)

  // 拼接 ciphertext + authTag
  const encrypted = new Uint8Array(ciphertext.length + authTag.length)
  encrypted.set(ciphertext)
  encrypted.set(authTag, ciphertext.length)

  const key = await crypto.subtle.importKey(
    'raw',
    strToUint8(keyStr),
    { name: 'AES-GCM' },
    false,
    ['decrypt']
  )

  const decrypted = await crypto.subtle.decrypt(
    { name: 'AES-GCM', iv },
    key,
    encrypted
  )

  return new TextDecoder().decode(decrypted)
}
