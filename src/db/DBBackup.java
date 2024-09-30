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
import java.util.Date;

public class DBBackup {

    private static final String DATABASE_NAME = "banco2";
    private static final String USER_NAME = "postgres";
    private static final String PASSWORD = "admin";
    private static final String BACKUP_PATH = "C:\\Temp\\Backup\\";
    private static final String BACKUP_ADDITIONAL_PATH = "C:\\Temp\\BackupAdicional\\";

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    public static void executar(boolean excluirAntigos, boolean copiarBackupAdicional) {
        File backupDir = new File(BACKUP_PATH);
        File backupAdditionalDir = new File(BACKUP_ADDITIONAL_PATH);

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        if (!backupAdditionalDir.exists() && copiarBackupAdicional) {
            backupAdditionalDir.mkdirs();
        }

        try {
            String backupName = criarBackup(copiarBackupAdicional);

            criptografarBackup(backupName);

            if (excluirAntigos) {
                excluirAntigos();
            }

            Logger.log("Backup criado com sucesso em: " + BACKUP_PATH);
        } catch (InterruptedException | IOException e) {
            Logger.log("Erro ao criar o backup: " + e.getMessage());
        }
    }

    private static String criarBackup(boolean copiarBackupAdicional) throws IOException, InterruptedException {
        String backupFilePath = getBackupFileName();
        String command = String.format("pg_dump -U %s -F c -b -v -f \"%s\" %s",
                USER_NAME, backupFilePath, DATABASE_NAME);

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.environment().put("PGPASSWORD", PASSWORD);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Erro ao executar pg_dump, código de saída: " + exitCode);
        }

        if (copiarBackupAdicional) {
            String additionalBackupFilePath = BACKUP_ADDITIONAL_PATH + new File(backupFilePath).getName();
            ProcessBuilder processBuilderAdditional = new ProcessBuilder("cmd.exe", "/c", "copy", backupFilePath, additionalBackupFilePath);
            Process processAdditional = processBuilderAdditional.start();

            int exitCodeAdditional = processAdditional.waitFor();
            if (exitCodeAdditional != 0) {
                throw new IOException("Erro ao copiar backup adicional, código de saída: " + exitCodeAdditional);
            }

            Logger.log("Backup adicional criado em: " + additionalBackupFilePath);
        }

        Logger.log("Backup do banco de dados criado em: " + backupFilePath);

        return backupFilePath;
    }

    private static void criptografarBackup(String backupName) {
        try {
            Logger.log("Iniciando criptografia do backup.");

            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            SecretKey secretKey = keyGen.generateKey();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (FileInputStream fis = new FileInputStream(backupName);
                 FileOutputStream fos = new FileOutputStream(backupName + ".enc")) {

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

            Logger.log("Backup criptografado com sucesso: " + backupName + ".enc");
        } catch (Exception e) {
            Logger.log("Erro ao criptografar o backup: " + e.getMessage());
        }
    }

    private static void excluirAntigos() {
        File dir = new File("C:\\Temp\\Backup\\");
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                long diff = System.currentTimeMillis() - file.lastModified();
                long daysOld = diff / (1000 * 60 * 60 * 24);
                if (daysOld > 7) {
                    if (file.delete()) {
                        Logger.log("Arquivo excluído: " + file.getName());
                    } else {
                        Logger.log("Erro ao excluir arquivo: " + file.getName());
                    }
                }
            }
        } else {
            Logger.log("Nenhum arquivo encontrado para exclusão.");
        }
    }

    private static String getBackupFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return BACKUP_PATH + "backup_" + timestamp + ".bak";
    }
}
