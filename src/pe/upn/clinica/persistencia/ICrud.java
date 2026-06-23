package pe.upn.clinica.persistencia;

import java.util.List;

public interface ICrud<T> {

    boolean registrar(T objeto);

    boolean editar(T objeto);

    boolean eliminar(int id);

    List<T> listarTodos();

    T buscarPorId(int id);
}