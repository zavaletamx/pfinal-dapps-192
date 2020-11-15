package mx.edu.uteq.dapps.proyectofinal192;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
Singleton
1.- La clase ya no se puede heredar
2.- La clase no se debe instanciar
*/
public final  class Helper {

    /*Constructor privado*/
    private Helper() { }

    /**
     * Método para genera una cadena de MD5
     * @param s
     * @return
     */
    public static String MD5_Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }

}
