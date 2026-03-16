/**
 * 将 PEM 格式的私钥转换为 CryptoKey
 * @param {string} pem - PEM 格式私钥（-----BEGIN PRIVATE KEY-----）
 * @returns {Promise<CryptoKey>}
 */
export async function importPrivateKey(pem) {
  const pemContent = pem
    .replace(/-----BEGIN PRIVATE KEY-----/, '')
    .replace(/-----END PRIVATE KEY-----/, '')
    .replace(/\s/g, '')

  const binaryDer = Uint8Array.from(atob(pemContent), c => c.charCodeAt(0))

  return await crypto.subtle.importKey(
    'pkcs8', // Private Key format
    binaryDer,
    {
      name: 'RSA-OAEP',
      hash: 'SHA-256'
    },
    true,
    ['decrypt'] // 注意这里是 decrypt
  )
}

/**
 * 使用 RSA 私钥解密数据
 * @param {string} encryptedB64 - Base64 编码的加密数据
 * @param {CryptoKey} privateKey - 已导入的私钥
 * @returns {Promise<Uint8Array>} 原始数据字节数组
 */
export async function decryptWithPrivateKey(encryptedB64, privateKey) {
  // Base64 → Uint8Array
  const binary = atob(encryptedB64)
  const encryptedBuffer = Uint8Array.from(binary, c => c.charCodeAt(0))

  const decrypted = await crypto.subtle.decrypt(
    {
      name: 'RSA-OAEP'
    },
    privateKey,
    encryptedBuffer
  )

  return new Uint8Array(decrypted)
}
