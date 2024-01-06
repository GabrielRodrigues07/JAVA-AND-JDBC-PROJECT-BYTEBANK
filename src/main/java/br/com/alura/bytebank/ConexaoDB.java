package br.com.alura.bytebank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    public static void main(String[] args)  {

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bytebank?user=root&password=root");

            System.out.println("Conexão Aberta!!");


            connection.close();
            System.out.println("Conexão Fechada!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
