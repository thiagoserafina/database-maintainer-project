package db;

import utils.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class DBBackup {

    private static final String DATABASE_NAME = "banco2";
    private static final String USER_NAME = "postgres";
    private static final String PASSWORD = "admin";
    private static final String BACKUP_PATH = "C:\\Temp\\Backup\\";
    private static final String BACKUP_ADDITIONAL_PATH = "C:\\Temp\\BackupAdicional\\";

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static final int MAX_BACKUPS = 5;

    public static void executar(boolean copiarBackupAdicional) {
        File backupDir = new File(BACKUP_PATH);
        File backupAdditionalDir = new File(BACKUP_ADDITIONAL_PATH);

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        if (!backupAdditionalDir.exists() && copiarBackupAdicional) {
            backupAdditionalDir.mkdirs();
        }

        try {
            String backupName = criarBackup();

            String encryptedBackupName = criptografarBackup(backupName);

            excluirBackupOriginal(backupName);

            manterLimiteDeBackups(BACKUP_PATH);
            if (copiarBackupAdicional) {
                copiarBackupAdicional(encryptedBackupName);
                manterLimiteDeBackups(BACKUP_ADDITIONAL_PATH);
            }

            Logger.log("Backup criado e criptografado com sucesso em: " + BACKUP_PATH);
        } catch (InterruptedException | IOException e) {
            Logger.log("Erro ao criar o backup: " + e.getMessage());
        }
    }

    private static String criarBackup() throws IOException, InterruptedException {
        String backupFilePath = getBackupFileName();
        String command = String.format("pg_dump -U %s -F c -b -v -f \"%s\" %s",
                USER_NAME, backupFilePath, DATABASE_NAME);

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.environment().put("PGPASSWORD", PASSWORD);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Erro ao executar pg_dump, codigo de saida: " + exitCode);
        }

        Logger.log("Backup do banco de dados criado em: " + backupFilePath);

        return backupFilePath;
    }

    private static String criptografarBackup(String backupName) {
        String encryptedBackupName = backupName + ".enc";
        try {
            Logger.log("Iniciando criptografia do backup.");

            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            SecretKey secretKey = keyGen.generateKey();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (FileInputStream fis = new FileInputStream(backupName);
                 FileOutputStream fos = new FileOutputStream(encryptedBackupName)) {

                byte[] input = new byte[64];
                int bytesRead;

                while ((bytesRead = fis.read(input)) != -1) {
                    byte[] output = cipher.update(input, 0, bytesRead);
                    if (output != null) {
                        fos.write(output);
                    }
                }

                byte[] outputBytes = cipher.doFinal();
                if (outputBytes != null) {
                    fos.write(outputBytes);
                }
            }

            Logger.log("Backup criptografado com sucesso: " + encryptedBackupName);
        } catch (Exception e) {
            Logger.log("Erro ao criptografar o backup: " + e.getMessage());
        }
        return encryptedBackupName;
    }

    private static void excluirBackupOriginal(String backupName) {
        File originalBackup = new File(backupName);
        if (originalBackup.delete()) {
            Logger.log("Backup original excluído: " + backupName);
        } else {
            Logger.log("Erro ao excluir o backup original: " + backupName);
        }
    }

    private static void copiarBackupAdicional(String encryptedBackupName) throws IOException, InterruptedException {
        String additionalBackupFilePath = BACKUP_ADDITIONAL_PATH + new File(encryptedBackupName).getName();
        ProcessBuilder processBuilderAdditional = new ProcessBuilder("cmd.exe", "/c", "copy",
                encryptedBackupName, additionalBackupFilePath);
        Process processAdditional = processBuilderAdditional.start();

        int exitCodeAdditional = processAdditional.waitFor();
        if (exitCodeAdditional != 0) {
            throw new IOException("Erro ao copiar backup adicional, codigo de saida: " + exitCodeAdditional);
        }

        Logger.log("Backup adicional criado em: " + additionalBackupFilePath);
    }

    private static void manterLimiteDeBackups(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".enc"));

        if (files != null && files.length > MAX_BACKUPS) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));

            for (int i = 0; i < files.length - MAX_BACKUPS; i++) {
                if (files[i].delete()) {
                    Logger.log("Backup antigo excluído de " + path + ": " + files[i].getName());
                } else {
                    Logger.log("Erro ao excluir backup antigo de " + path + ": " + files[i].getName());
                }
            }
        }
    }

    private static String getBackupFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return BACKUP_PATH + "backup_" + timestamp + ".bak";
    }
}
