package edu.uv.dawts.trabajofinal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AccesoDatos {
	private PreparedStatement getTareasUsuario;
	private PreparedStatement getTareasProyecto;
	private PreparedStatement setFechaFinalizacion;
	private PreparedStatement creaProyecto;
	private PreparedStatement creaTarea;
	private Statement st;
	private SimpleDateFormat formatter;
	private PreparedStatement getUserRole;

	public AccesoDatos(Connection c) throws Exception {
		getTareasUsuario = c
				.prepareStatement("select * from tareas where username=?");
		setFechaFinalizacion = c
				.prepareStatement("update tareas set fechafinalizacion=? where ta_id=?");
		String pattern = "yyyy-MM-dd";
		formatter = new SimpleDateFormat(pattern);
		st = c.createStatement();
		creaProyecto = c
				.prepareStatement("insert into proyectos (nombre) values (?)");
		creaTarea = c
				.prepareStatement("insert into tareas (nombre,pr_id,fechalimite,username) values (?,?,?,?)");

		getTareasProyecto = c.prepareStatement("select * from tareas where pr_id=?");
		getUserRole = c.prepareStatement("select role from roles where username=?");


	}

	public ArrayList<Tarea> getAllTareas(int proyecto) throws Exception {
		getTareasProyecto.setInt(1,proyecto);
		ResultSet rs = getTareasProyecto.executeQuery();
		rs.beforeFirst();
		ArrayList<Tarea> tareas = new ArrayList<Tarea>();
		while (rs.next()) {
			Tarea t = new Tarea();
			t.setId(rs.getInt("ta_id"));
			t.setNombre(rs.getString("nombre"));
			t.setProyecto(rs.getInt("pr_id"));
			t.setFechaTope(rs.getDate("fechalimite"));
			t.setFechaFinalizacion(rs.getDate("fechafinalizacion"));
			t.setProgramador(rs.getString("username"));
			tareas.add(t);
		}
		rs.close();
		return tareas;

	}

	public ArrayList<Proyecto> getAllProyectos() throws Exception {
		ResultSet rs = st.executeQuery("select * from proyectos");
		rs.beforeFirst();
		ArrayList<Proyecto> proyectos = new ArrayList<Proyecto>();
		while (rs.next()) {
			Proyecto pr = new Proyecto();
			pr.setId(rs.getInt(1));
			pr.setNombre(rs.getString(2));
			proyectos.add(pr);
		}
		rs.close();
		return proyectos;
	}
	
	public String getRol(String username) throws Exception {
		getUserRole.setString(1, username);
		ResultSet rs = getUserRole.executeQuery();
		rs.next();
		return rs.getString("role");
	}

	public ArrayList<Tarea> getTareasUsuario(String user) throws Exception {
		getTareasUsuario.setString(1, user);
		ResultSet rs = getTareasUsuario.executeQuery();
		ArrayList<Tarea> tareas = new ArrayList<Tarea>();
		while (rs.next()) {
			Tarea t = new Tarea();
			t.setId(rs.getInt("ta_id"));
			t.setNombre(rs.getString("nombre"));
			t.setProyecto(rs.getInt("pr_id"));
			t.setFechaTope(rs.getDate("fechalimite"));
			t.setProgramador(rs.getString("username"));
			t.setFechaFinalizacion(rs.getDate("fechafinalizacion"));
			tareas.add(t);
		}
		rs.close();
		return tareas;
	}

	public void setFechaFinalizacion(int year, int mes, int dia, int tarea)
			throws Exception {
		String finalizacion = getDate(year, mes, dia);
		setFechaFinalizacion.setString(1, finalizacion);
		setFechaFinalizacion.setInt(2, tarea);

		setFechaFinalizacion.executeUpdate();
	}

	public void creaProyecto(String nombre) throws Exception {
		creaProyecto.setString(1, nombre);

		creaProyecto.executeUpdate();
	}

	public void creaTarea(String nombre, int proyecto, String usuario, int year,
			int mes, int dia) throws Exception {
		String limite = getDate(year, mes, dia);
		creaTarea.setString(1, nombre);
		creaTarea.setInt(2, proyecto);
		creaTarea.setString(3, limite);
		creaTarea.setString(4, usuario);

		creaTarea.executeUpdate();
	}
	

	private String getDate(int year, int mes, int dia) {
		Calendar c = Calendar.getInstance();
		c.set(year, mes - 1, dia);
		String fecha = formatter.format(c.getTime());
		return fecha;
	}

	public void closeAll() throws Exception {
		if (st != null)
			st.close();
		if (creaTarea != null)
			creaTarea.close();
		if (creaProyecto != null)
			creaProyecto.close();
		if (getTareasUsuario != null)
			getTareasUsuario.close();
		if (setFechaFinalizacion != null)
			setFechaFinalizacion.close();
	}

}
