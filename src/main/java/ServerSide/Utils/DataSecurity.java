/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerSide.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Scanner;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Pedro A. Otoya
 */
public class DataSecurity {

    // ----------------------------------------------------------------------------------------
    /**
     * Calcula el codigo criptografico de hash con algoritmo SHA-512 y la clave
     *
     * @param datosJSON Datos JSON que se quieren encryptar con el hash.
     * @return - String de los bytes hasheados, en codificacion hexadecimal.
     */
    public static String hashCryptoCode(String datosJSON) {
        String algoritmo = "SHA-512";
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            data.write(datosJSON.getBytes("UTF-8"));
            byte[] bytes = data.toByteArray();
            byte[] rawHmac = md.digest(bytes);
            return transformar(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   // ----------------------------------------------------------------------------------------
    // Verificacion de integridad ( HASH_DENUEVO(datosJSON) == hash_hexa ) -->
    // Datos sin alteraciones.
    // ----------------------------------------------------------------------------------------
    /**
     * Metodo que verifica que un codigo HMAC corresponda con un mensaje dado.
     *
     * @param datosJSON String con del JSON de los datos.
     * @param hash_String Hash de los datosJSON. (Bytes + {Encoding base64} =
     * String)
     * @return La verificacion de que el mensaje y el codigo hmac coincidan.
     * @throws Exception Si hubo un error al generar un mensaje HMAC.
     */
    public static boolean verificarIntegridad(String datosJSON, String data_hash)
            throws Exception {
        byte[] nuevo = destransformar(hashCryptoCode(datosJSON));
        byte[] hash = destransformar(data_hash);
        if (nuevo.length != hash.length) {
            return false;
        }
        for (int i = 0; i < nuevo.length; i++) {
            if (nuevo[i] != hash[i]) {
                return false;
            }
        }
        return true;
    }

	// ----------------------------------------------------------------------------------------
    // Transformacion de bytes a su representacion hexadecimal en un STRING
    // ----------------------------------------------------------------------------------------
    /**
     * Codifica los bytes
     *
     * @param b Bytes
     * @return EL string construido con la representacion de bytes como Base64.
     * @throws UnsupportedEncodingException
     */
    private static String transformar(byte[] b) throws UnsupportedEncodingException {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(b);
    }

    /**
     * Algoritmo que transforma los enteros en los bytes correspondientes
     *
     * @param ss El string con los enteros a transformar.
     * @return Los bytes en su representacion real.
     * @throws Base64DecodingException
     * @throws UnsupportedEncodingException
     */
    private static byte[] destransformar(String ss) {
        return Base64.decodeBase64(ss);
    }

}
