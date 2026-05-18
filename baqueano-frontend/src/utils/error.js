/**
 * Extrae el mensaje legible de un error de axios contra nuestra API.
 * El backend devuelve { code, message, fieldErrors? } en respuestas de error.
 */
export function errorToMessage(err, fallback = 'Ocurrio un error inesperado') {
  const data = err?.response?.data;
  if (!data) return err?.message ?? fallback;
  if (data.fieldErrors?.length) {
    return data.fieldErrors.map((fe) => `${fe.field}: ${fe.message}`).join(', ');
  }
  return data.message ?? fallback;
}
