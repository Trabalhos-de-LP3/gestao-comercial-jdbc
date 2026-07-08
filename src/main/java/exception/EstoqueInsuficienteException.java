package exception;

public class EstoqueInsuficienteException extends NegocioException {

    private static final long serialVersionUID = 1L;
	private final int produtoId;
    private final int quantidadeSolicitada;
    private final int quantidadeDisponivel;

    public EstoqueInsuficienteException(int produtoId,
                                        int quantidadeSolicitada,
                                        int quantidadeDisponivel) {
        super("Estoque insuficiente para o produto ID " + produtoId
                + ". Solicitado: " + quantidadeSolicitada
                + " | Disponível: " + quantidadeDisponivel);
        this.produtoId = produtoId;
        this.quantidadeSolicitada = quantidadeSolicitada;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
}
