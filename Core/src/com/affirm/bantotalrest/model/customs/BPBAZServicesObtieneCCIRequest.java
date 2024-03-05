package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BPBAZServicesObtieneCCIRequest extends BtRequestData {

    private Long Producto;

    public Long getProducto() {
        return Producto;
    }

    public void setProducto(Long producto) {
        Producto = producto;
    }
}
