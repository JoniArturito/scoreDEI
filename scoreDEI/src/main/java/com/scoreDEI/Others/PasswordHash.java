package com.scoreDEI.Others;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PasswordHash {

    public static byte[] getSha(String plainTextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(plainTextPassword.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not hash: " + plainTextPassword);
            return null;
        }
    }

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
