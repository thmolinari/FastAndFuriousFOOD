/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.thiago.FastFood.domain.DTO;

import br.dev.thiago.FastFood.domain.model.Pedido;

/**
 *
 * @author sesidevb
 */
public class StatusRequest {
    private Pedido.Status status;

    public Pedido.Status getStatus() {
        return status;
    }

    public void setStatus(Pedido.Status status) {
        this.status = status;
    }
    
}