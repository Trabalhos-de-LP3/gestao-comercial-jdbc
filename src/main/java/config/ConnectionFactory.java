package config;

import exception.NegocioException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL =
            "jdbc:mysql://localhost:3306/gestao_comercial"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO = "app";
    private static final String SENHA = "app";

    private ConnectionFactory() {
    }

    public static Connection abrir() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new NegocioException("Não foi possível conectar ao banco de dados.", e);
        }
    }
}
