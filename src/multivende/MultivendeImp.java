/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multivende;

import java.util.ArrayList;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author cfuen
 */
@WebService(endpointInterface = "multivende.MultivendeBoleta")
public class MultivendeImp {

    public String carga(@WebParam(name = "cabecera") ArrayList<String> cabecera,
            @WebParam(name = "detalleboleta") ArrayList<String> detalle,
            @WebParam(name = "pagoboleta") ArrayList<String> pago) {
        return "OK";
    }}
