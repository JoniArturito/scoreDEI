/**
 * It takes a string, hashes it with SHA-256, and returns the hash as a hexadecimal string
 */
package com.scoreDEI.Others;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PasswordHash {

    /**
     * Get the SHA-256 hash of the given string.
     *
     * @param plainTextPassword The password you want to hash.
     * @return A byte array of the hashed password.
     */
    public static byte[] getSha(String plainTextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(plainTextPassword.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not hash: " + plainTextPassword);
            return null;
        }
    }

    /**
     * Convert the byte array to a BigInteger, then convert the BigInteger to a hex string.
     *
     * @param hash The byte array to convert to hex
     * @return A string of the hexadecimal representation of the hash.
     */
    public static String toHexString(byte[] hash) {
        BigInteger number =  new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        /* Pad with leading zeros */
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public static void main(String [] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Password to hash: ");
            String password = scanner.nextLine();

            System.out.println(toHexString(getSha(password)));


        } catch (InputMismatchException e) {
            System.out.println("[INPUT] " + e.getMessage());
        }
    }
}
