package com.example.myapplication.Utils.translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 encoding related classes
 * 
 * @author wangjingtao
 * 
 */
public class MD5 {
    // First initialize a character array to store each hexadecimal character
    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    /**
     * Get the MD5 value of a string
     * 
     * @param input input string
     * @return MD5 value of the input string
     * 
     */
    public static String md5(String input) {
        if (input == null)
            return null;

        try {
            // Get an MD5 converter (if you want to change the SHA1 parameter to "SHA1")
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // Convert the input string to a byte array
            byte[] inputByteArray = input.getBytes("utf-8");
            // inputByteArray is the byte array converted from the input string
            messageDigest.update(inputByteArray);
            // Convert and return the result, also a byte array, containing 16 elements
            byte[] resultByteArray = messageDigest.digest();
            // Convert character array to string and return
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Get the MD5 value of the file
     * 
     * @param file
     * @return
     */
    public static String md5(File file) {
        try {
            if (!file.isFile()) {
                System.err.println("File" + file.getAbsolutePath() + "does not exist or is not a file");
                return null;
            }

            FileInputStream in = new FileInputStream(file);

            String result = md5(in);

            in.close();

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String md5(InputStream in) {

        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, read);
            }

            in.close();

            String result = byteArrayToHex(messagedigest.digest());

            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String byteArrayToHex(byte[] byteArray) {
        // new A character array, this is used to form the result string (explain: a byte is an eight-bit binary, that is,
        // two hexadecimal characters (2 to the 8th power is equal to 16 to the 2nd power))
        char[] resultCharArray = new char[byteArray.length * 2];
        // Traverse the byte array, convert it into characters and put them in the character array through bit operation (bit operation efficiency is high)
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        // Combining character arrays into string returns
        return new String(resultCharArray);

    }

}
