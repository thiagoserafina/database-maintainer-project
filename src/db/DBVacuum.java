package db;

import utils.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DBVacuum {

    public static void autoVacuum() {
        try (Connection conn = DBConnection.connect()) {
            long dias = diasDesdeUltimoVacuum(conn);

            if (dias >= 60) {
                executarVacuum(conn, true, true);
            } else if (dias >= 30) {
                executarVacuum(conn, true, false);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void vacuumManual(boolean vacuum, boolean fullAnalyze) {
        try (Connection conn = DBConnection.connect()) {
            if (vacuum) {
                executarVacuum(conn, true, fullAnalyze);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void reindex() {
        try (Connection conn = DBConnection.connect()) {
            String comandoReindex = "REINDEX DATABASE " + conn.getCatalog() + ";";

            Logger.log("Executando comando " + comandoReindex);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(comandoReindex);
                Logger.log("Comando " + comandoReindex + " executado com sucesso.");
            } catch (SQLException e) {
                Logger.log("Erro ao executar comando " + comandoReindex + ": " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static long diasDesdeUltimoVacuum(Connection conn) throws SQLException {
        Logger.log("Verificando dias desde o ultimo VACUUM.");
        String query = "SELECT last_vacuum FROM pg_stat_all_tables WHERE schemaname = 'public' ORDER BY last_vacuum DESC LIMIT 1;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("last_vacuum");
                if (sqlDate != null) {
                    LocalDate lastVacuum = sqlDate.toLocalDate();
                    long dias = ChronoUnit.DAYS.between(lastVacuum, LocalDate.now());
                    Logger.log("Ãšltimo VACUUM realizado hÃ¡ " + dias + " dias.");
                    return dias;
                } else {
                    Logger.log("Nenhum valor encontrado para last_vacuum.");
                }
            }
        }

        Logger.log("Nenhum VACUUM encontrado.");
        return 0;
    }

    private static void executarVacuum(Connection conn, boolean vacuum, boolean fullAnalyze) throws SQLException {
        String comandoVacuum = vacuum ? (fullAnalyze ? "VACUUM FULL ANALYZE;" : "VACUUM;") : "";

        Logger.log("Executando comando " + comandoVacuum);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(comandoVacuum);
            Logger.log("Comando " + comandoVacuum + " executado com sucesso.");
        } catch (SQLException e) {
            Logger.log("Erro ao executar comando " + comandoVacuum + ": " + e.getMessage());
            throw e;
        }
    }
}
