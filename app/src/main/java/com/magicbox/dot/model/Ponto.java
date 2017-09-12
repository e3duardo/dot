package com.magicbox.dot.model;

import java.util.Date;

/**
 * Criado por eduardo em 11/09/17.
 */

public class Ponto {


    private Date abertura;

    private Date fechamento;

    public Ponto(Date abertura, Date fechamento) {
        this.abertura = abertura;
        this.fechamento = fechamento;
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public Date getFechamento() {
        return fechamento;
    }

    public void setFechamento(Date fechamento) {
        this.fechamento = fechamento;
    }
}
