package com.studentlms.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class CredentialManager {

    private static final String KEYSTORE_ALIAS = "StudentLMSCredentials";
    private static final String PREFS_NAME = "EncryptedCredentials";
    private static final String KEY_USERNAME = "erp_username";
    private static final String KEY_PASSWORD = "erp_password_encrypted";
    private static final String KEY_IV = "erp_iv";
    private static final String KEY_STUDENT_NAME = "erp_student_name";

    private final Context context;

    public CredentialManager(Context context) {
        this.context = context;
    }

    public void saveCredentials(String username, String password) throws Exception {
        // Generate or get encryption key
        SecretKey secretKey = getOrCreateKey();

        // Encrypt password
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();
        byte[] encryptedPassword = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

        // Store encrypted data
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, Base64.encodeToString(encryptedPassword, Base64.DEFAULT))
                .putString(KEY_IV, Base64.encodeToString(iv, Base64.DEFAULT))
                .apply();
    }

    public void saveStudentName(String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String encryptedName = encrypt(name);
        if (encryptedName != null) {
            prefs.edit().putString(KEY_STUDENT_NAME, encryptedName).apply();
        }
    }

    public String getUsername() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getStudentName() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String encryptedName = prefs.getString(KEY_STUDENT_NAME, null);
        return decrypt(encryptedName);
    }

    public String getPassword() throws Exception {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String encryptedPassword = prefs.getString(KEY_PASSWORD, null);
        String ivString = prefs.getString(KEY_IV, null);

        if (encryptedPassword == null || ivString == null) {
            return null;
        }

        // Decrypt password
        SecretKey secretKey = getOrCreateKey();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = Base64.decode(ivString, Base64.DEFAULT);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

        byte[] decryptedPassword = cipher.doFinal(Base64.decode(encryptedPassword, Base64.DEFAULT));
        return new String(decryptedPassword, StandardCharsets.UTF_8);
    }

    public boolean hasCredentials() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_USERNAME) && prefs.contains(KEY_PASSWORD);
    }

    public void clearCredentials() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private SecretKey getOrCreateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            // Generate new key
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build();

            keyGenerator.init(keySpec);
            return keyGenerator.generateKey();
        } else {
            // Get existing key
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS, null))
                    .getSecretKey();
        }
    }

    private String encrypt(String plainText) {
        try {
            SecretKey secretKey = getOrCreateKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            String ivString = Base64.encodeToString(iv, Base64.NO_WRAP);
            String encryptedString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);

            return ivString + ":" + encryptedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decrypt(String encryptedComposite) {
        try {
            if (encryptedComposite == null || !encryptedComposite.contains(":"))
                return null;

            String[] parts = encryptedComposite.split(":");
            if (parts.length != 2)
                return null;

            String ivString = parts[0];
            String encryptedString = parts[1];

            SecretKey secretKey = getOrCreateKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = Base64.decode(ivString, Base64.NO_WRAP);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            byte[] decryptedBytes = cipher.doFinal(Base64.decode(encryptedString, Base64.NO_WRAP));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
