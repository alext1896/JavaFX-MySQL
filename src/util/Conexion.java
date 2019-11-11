package util;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
 
public class Conexion {
	private ODB odb = null;
	public void abrirBaseDatos () {
		odb = ODBFactory.open ("/datos/usuarios/alumnos/jose.guapache/Escritorio/AccesoDatos/transparencias/Agenda JavaFX NeoDatis");  
	}
	
	public void cerrarBaseDatos () {
		odb.close();
	}
	
}

