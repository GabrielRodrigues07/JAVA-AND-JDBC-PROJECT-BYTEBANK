package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;
import br.com.alura.bytebank.domain.exceptions.NotUpdateException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ContaDAO {

    private Connection connection;

    public ContaDAO(Connection connection) {
        this.connection = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {

        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente);

        String sql = "INSERT INTO conta(numero, saldo, cliente_nome, cliente_cpf, cliente_email) " +
                "VALUES(?, ?, ?, ?, ?)";


        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());

            preparedStatement.execute();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Set<Conta> listar() {
        Set<Conta> conta = new HashSet<>();
        ResultSet resultSet;
        PreparedStatement ps;

        String sql = "SELECT * FROM conta";

        try {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta.add(new Conta(numero, saldo, cliente));
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return conta;
    }

    public Conta buscarUsandoNumeroDaConta(Integer numeroConta) {

        String sql = "SELECT * FROM conta WHERE numero = ?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Conta conta = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, numeroConta);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numero, saldo, cliente);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void depositar(Integer numeroConta, BigDecimal valorDeposito) {

        String sql = "UPDATE conta SET saldo = saldo + ? WHERE numero = ?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBigDecimal(1, valorDeposito);
            preparedStatement.setInt(2, numeroConta);
            int i = preparedStatement.executeUpdate();

            if (Objects.equals(i, 0)) {
                throw new NotUpdateException("Houve algum problema ao depositar valor");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void sacar(Integer numeroConta, BigDecimal valorSaque) {

        String sql = "UPDATE conta SET saldo = saldo - ? WHERE numero = ?";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBigDecimal(1, valorSaque);
            preparedStatement.setInt(2, numeroConta);
            int i = preparedStatement.executeUpdate();

            if (Objects.equals(i, 0)) {
                throw new NotUpdateException("Houve algum problema ao sacar valor");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deletar(Integer numeroConta) {

        String sql = "DELETE FROM conta WHERE numero = ? ";

        PreparedStatement preparedStatement;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, numeroConta);

            int i = preparedStatement.executeUpdate();

            if (Objects.equals(i, 0)) {
                throw new NotUpdateException("Houve algum problema ao Encerrar conta");
            }

            preparedStatement.close();
            connection.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
