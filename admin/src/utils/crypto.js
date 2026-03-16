/**
 * 将 Unicode 字符串转为 Base64（支持中文、Emoji 等）
 */
export function utf8ToB64(str) {
  const utf8 = encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, (match, p1) => {
    return String.fromCharCode('0x' + p1)
  })
  return btoa(utf8)
}

/**
 * 将 Base64 解码为原始字符串（支持中文）
 */
export function b64ToUtf8(b64) {
  const utf8 = atob(b64)
  try {
    return decodeURIComponent(
      utf8.replace(/(.)/g, (m, p) => {
        let code = p.charCodeAt(0).toString(16).toUpperCase()
        if (code.length < 2) code = '0' + code
        return '%' + code
      })
    )
  } catch (e) {
    return utf8
  }
}
