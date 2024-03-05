package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.exceptions.NotFoundException;
import br.com.alura.bytebank.domain.exceptions.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ContaService {

    private ConnectionFactory connection;

    public ContaService() {
        this.connection = new ConnectionFactory();
    }

    private Set<Conta> contas = new HashSet<>();

    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public Conta consultarConta(Integer numeroDaConta) {
        Connection conn = connection.recuperarConexao();
        Conta conta = new ContaDAO(conn).buscarUsandoNumeroDaConta(numeroDaConta);
        if (Objects.nonNull(conta)) {
            return conta;
        } else {
            throw new NotFoundException("Nenhuma conta encontrada com número informado: " + numeroDaConta);
        }
    }

    public void abrir(DadosAberturaConta dadosDaConta) {

        Connection conn = connection.recuperarConexao();

        new ContaDAO(conn).salvar(dadosDaConta);

    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        Conta conta = consultarConta(numeroDaConta);
        Connection conn = connection.recuperarConexao();
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        new ContaDAO(conn).sacar(numeroDaConta, valor);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        consultarConta(numeroDaConta);
        Connection conn = connection.recuperarConexao();
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }
        new ContaDAO(conn).depositar(numeroDaConta, valor);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        contas.remove(conta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        return contas
                .stream()
                .filter(c -> Objects.equals(c.getNumero(), numero))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
    }

    public void realizarTransferencia(Integer numeroContaOrigem, Integer numeroContaDestino, BigDecimal valor) {

        realizarSaque(numeroContaOrigem, valor);
        realizarDeposito(numeroContaDestino, valor);
    }
}
