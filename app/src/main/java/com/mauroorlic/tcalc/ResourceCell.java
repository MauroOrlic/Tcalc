package com.mauroorlic.tcalc;

public class ResourceCell {
    Double total;
    Double remaining;

    public ResourceCell(Double total, Double remaining) {
        this.total = total;
        this.remaining = remaining;
    }
    public ResourceCell(Double total) {
        this.total = total;
        this.remaining = total;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getRemaining() {
        return remaining;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }
}
