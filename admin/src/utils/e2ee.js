import {
  importPublicKey,
  encryptWithPublicKey,
  importPrivateKey,
  decryptWithPrivateKey
} from '@/utils/rsa'

const KEY_PAIR_STORAGE_KEY = 'user_rsa_keypair'
const USER_META_KEY = 'user_meta'

// --- 密钥对管理 ---
export async function ensureKeyPairExists() {
  const stored = localStorage.getItem(KEY_PAIR_STORAGE_KEY)
  if (stored) return JSON.parse(stored)

  const keyPair = await crypto.subtle.generateKey(
    { name: 'RSA-OAEP', modulusLength: 2048, publicExponent: new Uint8Array([1, 0, 1]), hash: 'SHA-256' },
    true,
    ['encrypt', 'decrypt']
  )

  const spki = await crypto.subtle.exportKey('spki', keyPair.publicKey)
  const pkcs8 = await crypto.subtle.exportKey('pkcs8', keyPair.privateKey)

  const pemPublicKey = arrayBufferToPem(spki, 'PUBLIC KEY')
  const pemPrivateKey = arrayBufferToPem(pkcs8, 'PRIVATE KEY')

  const keyPairData = { publicKey: pemPublicKey, privateKey: pemPrivateKey }
  localStorage.setItem(KEY_PAIR_STORAGE_KEY, JSON.stringify(keyPairData))
  return keyPairData
}

// --- 公钥上传 ---
export async function uploadPublicKeyIfNeeded(apiUploadFn) {
  const userMeta = JSON.parse(localStorage.getItem(USER_META_KEY) || '{}')
  if (userMeta.publicKeyUploaded) return

  const keyPair = await ensureKeyPairExists()
  await apiUploadFn(keyPair.publicKey)
  localStorage.setItem(USER_META_KEY, JSON.stringify({ ...userMeta, publicKeyUploaded: true }))
}

// --- 工具函数 ---
function arrayBufferToBase64(buffer) {
  let binary = ''
  const bytes = new Uint8Array(buffer)
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary)
}

function arrayBufferToPem(buffer, type) {
  const base64 = arrayBufferToBase64(buffer)
  const header = `-----BEGIN ${type}-----`
  const footer = `-----END ${type}-----`
  const pemBody = base64.match(/.{1,64}/g).join('\n')
  return `${header}\n${pemBody}\n${footer}`
}

// 导出用于外部调用
export { importPublicKey, encryptWithPublicKey, importPrivateKey, decryptWithPrivateKey }
