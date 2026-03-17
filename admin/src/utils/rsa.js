// ===== 导入公钥（PEM → CryptoKey）=====
export async function importPublicKey(pem) {
  // 移除 PEM 头尾
  const pemHeader = '-----BEGIN PUBLIC KEY-----'
  const pemFooter = '-----END PUBLIC KEY-----'
  const pemContents = pem.replace(pemHeader, '').replace(pemFooter, '').replace(/\s/g, '')

  const binaryDerString = atob(pemContents)
  const binaryDer = new Uint8Array(binaryDerString.length)
  for (let i = 0; i < binaryDerString.length; i++) {
    binaryDer[i] = binaryDerString.charCodeAt(i)
  }

  return crypto.subtle.importKey(
    'spki',
    binaryDer,
    {
      name: 'RSA-OAEP',
      hash: 'SHA-256'
    },
    true,
    ['encrypt']
  )
}

// ===== 导入私钥（PEM → CryptoKey）=====
export async function importPrivateKey(pem) {
  const pemHeader = '-----BEGIN PRIVATE KEY-----'
  const pemFooter = '-----END PRIVATE KEY-----'
  const pemContents = pem.replace(pemHeader, '').replace(pemFooter, '').replace(/\s/g, '')

  const binaryDerString = atob(pemContents)
  const binaryDer = new Uint8Array(binaryDerString.length)
  for (let i = 0; i < binaryDerString.length; i++) {
    binaryDer[i] = binaryDerString.charCodeAt(i)
  }

  return crypto.subtle.importKey(
    'pkcs8',
    binaryDer,
    {
      name: 'RSA-OAEP',
      hash: 'SHA-256'
    },
    true,
    ['decrypt']
  )
}

// ===== 用公钥加密（Uint8Array → ArrayBuffer）=====
export async function encryptWithPublicKey(data, publicKey) {
  return crypto.subtle.encrypt(
    {
      name: 'RSA-OAEP'
    },
    publicKey,
    data
  )
}

// ===== 用私钥解密（ArrayBuffer → Uint8Array）=====
export async function decryptWithPrivateKey(encryptedData, privateKey) {
  let buffer
  if (typeof encryptedData === 'string') {
    // 假设是 Base64
    const binaryString = atob(encryptedData)
    buffer = new Uint8Array(binaryString.length)
    for (let i = 0; i < binaryString.length; i++) {
      buffer[i] = binaryString.charCodeAt(i)
    }
    buffer = buffer.buffer
  } else if (encryptedData instanceof ArrayBuffer) {
    buffer = encryptedData
  } else {
    throw new Error('decryptWithPrivateKey: input must be Base64 string or ArrayBuffer')
  }

  const decrypted = await crypto.subtle.decrypt(
    {
      name: 'RSA-OAEP'
    },
    privateKey,
    buffer
  )

  return new Uint8Array(decrypted)
}
