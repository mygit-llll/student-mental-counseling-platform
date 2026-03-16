/**
 * AES-GCM 加解密工具（使用 Web Crypto API）
 * 密钥格式：32 字节原始数据转成的二进制安全字符串（通过 String.fromCharCode）
 * 加密输出格式：Base64(iv + ciphertext + authTag)
 */

/**
 * 将二进制安全字符串（每个字符 charCode = 原始字节）转为 Uint8Array
 * @param {string} str - 二进制字符串（长度应为 32）
 * @returns {Uint8Array}
 */
function strToUint8(str) {
  const buf = new Uint8Array(str.length)
  for (let i = 0; i < str.length; i++) {
    buf[i] = str.charCodeAt(i)
  }
  return buf
}

/**
 * 将 Uint8Array 转为 Base64 字符串
 * @param {ArrayBuffer | Uint8Array} buffer
 * @returns {string}
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
 * @param {string} base64
 * @returns {Uint8Array}
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
 * 使用 AES-GCM 加密明文
 * @param {string} plaintext - 要加密的文本
 * @param {string} keyStr - 32 字节密钥（二进制安全字符串）
 * @returns {Promise<string>} Base64 编码的 (iv + ciphertext + authTag)
 */
export async function encryptAESGCM(plaintext, keyStr) {
  if (typeof keyStr !== 'string' || keyStr.length !== 32) {
    throw new Error('密钥必须是长度为 32 的字符串')
  }

  const key = await crypto.subtle.importKey(
    'raw',
    strToUint8(keyStr),
    { name: 'AES-GCM' },
    false,
    ['encrypt']
  )

  const iv = crypto.getRandomValues(new Uint8Array(12))
  const encoded = new TextEncoder().encode(plaintext)

  const encrypted = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv },
    key,
    encoded
  )

  // 拼接：iv (12) + ciphertextWithTag (n + 16)
  const result = new Uint8Array(iv.length + encrypted.byteLength)
  result.set(iv, 0)
  result.set(new Uint8Array(encrypted), iv.length)

  return uint8ToBase64(result)
}

/**
 * 使用 AES-GCM 解密
 * @param {string} encryptedData - Base64 编码的 (iv + ciphertext + authTag)
 * @param {string} keyStr - 32 字节密钥（二进制安全字符串）
 * @returns {Promise<string>} 解密后的明文（UTF-8）
 */
export async function decryptAESGCM(encryptedData, keyStr) {
  if (typeof keyStr !== 'string' || keyStr.length !== 32) {
    throw new Error('密钥必须是长度为 32 的字符串')
  }

  const full = base64ToUint8(encryptedData)

  if (full.length < 12 + 16) {
    throw new Error('加密数据太短，无法包含 IV 和 AuthTag')
  }

  const iv = full.slice(0, 12)
  const ciphertextWithTag = full.slice(12)

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
    ciphertextWithTag // 必须包含 authTag（最后 16 字节）
  )

  return new TextDecoder().decode(decrypted)
}
