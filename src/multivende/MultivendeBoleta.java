/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multivende;

import java.util.ArrayList;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author cfuen
 */
@WebService
public interface MultivendeBoleta {
	@WebMethod
	public String carga(ArrayList<String> cabecera,ArrayList<String> detalle,ArrayList<String> pago);
}