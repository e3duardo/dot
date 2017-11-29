package com.magicbox.dot.model;

import java.util.Calendar;

/**
 * Criado por eduardo em 06/10/17.
 */

public enum DiaSemana {

    SEGUNDA("Segunda-Feira", "seg", Calendar.MONDAY),
    TERCA("Terça-Feira", "ter", Calendar.TUESDAY),
    QUARTA("Quarta-Feira", "qua", Calendar.WEDNESDAY),
    QUINTA("Quinta-Feira", "qui", Calendar.THURSDAY),
    SEXTA("Sexta-Feira", "sex", Calendar.FRIDAY),
    SABADO("Sábado", "sab", Calendar.SATURDAY),
    DOMINGO("Domingo", "dom", Calendar.SUNDAY);

    private String nome;

    private String sigla;

    private int calendar;

    DiaSemana(String nome, String sigla, int calendar){
        this.nome = nome;
        this.sigla = sigla;
        this.calendar = calendar;
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }
    public int getCalendar(){
        return calendar;
    }

    public static DiaSemana valueOf(int dayFromCalendar){
        for (DiaSemana dia : DiaSemana.values()){
            if(dia.getCalendar() == dayFromCalendar){
                return dia;
            }
        }
        return null;
    }

    @Override public String toString() {
        return this.getNome();
    }
}
