/**
 * 将 PEM 格式的公钥转换为 CryptoKey
 * @param {string} pem - PEM 格式公钥（带 -----BEGIN PUBLIC KEY----- 头尾）
 * @returns {Promise<CryptoKey>}
 */
export async function importPublicKey(pem) {
  // 移除头尾和换行
  const pemContent = pem
    .replace(/-----BEGIN PUBLIC KEY-----/, '')
    .replace(/-----END PUBLIC KEY-----/, '')
    .replace(/\s/g, '')

  // Base64 解码为二进制 DER
  const binaryDer = Uint8Array.from(atob(pemContent), c => c.charCodeAt(0))

  // 导入为 CryptoKey
  return await crypto.subtle.importKey(
    'spki', // SubjectPublicKeyInfo 格式
    binaryDer,
    {
      name: 'RSA-OAEP',
      hash: 'SHA-256'
    },
    true,
    ['encrypt']
  )
}

/**
 * 使用 RSA 公钥加密数据
 * @param {Uint8Array} data - 要加密的原始数据（如 AES 密钥）
 * @param {CryptoKey} publicKey - 已导入的公钥
 * @returns {Promise<string>} 加密结果的 Base64 字符串
 */
export async function encryptWithPublicKey(data, publicKey) {
  const encrypted = await crypto.subtle.encrypt(
    {
      name: 'RSA-OAEP'
    },
    publicKey,
    data
  )
  // 转为 Base64 字符串
  let binary = ''
  const bytes = new Uint8Array(encrypted)
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary)
}
