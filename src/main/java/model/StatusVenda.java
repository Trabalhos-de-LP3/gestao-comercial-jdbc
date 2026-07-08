package model;

public enum StatusVenda {
    ABERTA,
    CONFIRMADA,
    CANCELADA,
    FATURADA;

    public boolean permiteEdicao() {
        return this == ABERTA;
    }

    public boolean permiteCancelamento() {
        return this == ABERTA || this == CONFIRMADA;
    }
}
