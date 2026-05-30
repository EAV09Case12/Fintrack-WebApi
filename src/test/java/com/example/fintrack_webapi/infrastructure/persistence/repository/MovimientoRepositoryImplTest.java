package com.example.fintrack_webapi.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.infrastructure.dao.EgresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.IngresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.MovimientoJpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.EgresoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.IngresoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;

@ExtendWith(MockitoExtension.class)
class MovimientoRepositoryImplTest {

    @Mock
    private IngresoJpaRepository ingresoRepo;

    @Mock
    private EgresoJpaRepository egresoRepo;

    @Mock
    private MovimientoJpaRepository movRepo;

    @InjectMocks
    private MovimientoRepositoryImpl repo;

    private void autenticarComo(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of())
        );
    }

    private IngresoEntity in(Long id, double m, Date f) {
        return new IngresoEntity(id, "usuario@test.com", m, f);
    }

    private EgresoEntity eg(Long id, double m, Date f, int c) {
        return new EgresoEntity(id, "usuario@test.com", m, f, c, "desc");
    }

    private MovimientoEntity mv(String tipo, Integer id) {
        return new MovimientoEntity(null, tipo, id == null ? null : id.longValue(), "usuario@test.com", LocalDateTime.now());
    }

    // Caso feliz: guardar ingreso.
    // Comportamiento esperado: retorna domain mapeado desde entidad guardada.
    @Test
    void guardaIngreso() {
        Date f = new Date();
        autenticarComo("usuario@test.com");
        when(ingresoRepo.save(any())).thenReturn(in(1L, 200.0, f));

        var out = repo.guardarIngreso(new com.example.fintrack_webapi.domain.model.Ingreso(200.0, f));
        assertEquals(200.0, out.getMonto());
    }

    // Caso feliz: guardar egreso.
    // Comportamiento esperado: retorna domain mapeado desde entidad guardada.
    @Test
    void guardaEgreso() {
        Date f = new Date();
        autenticarComo("usuario@test.com");
        when(egresoRepo.save(any())).thenReturn(eg(1L, 90.0, f, Categoria.SALUD.getCodigo()));

        var out = repo.guardarEgreso(new com.example.fintrack_webapi.domain.model.Egreso(90.0, f, Categoria.SALUD, "x"));
        assertEquals(Categoria.SALUD, out.getCategoria());
    }

    // Caso feliz: historial mezcla ingresos y egresos.
    // Comportamiento esperado: combina y ordena por fecha descendente.
    @Test
    void historialOk() {
        Date old = new Date(System.currentTimeMillis() - 1000);
        Date now = new Date();
        autenticarComo("usuario@test.com");

        when(movRepo.findByUserEmail("usuario@test.com")).thenReturn(List.of(
                mv("ingreso", 1),
                mv("egreso", 2),
                mv(null, 3)));
        when(ingresoRepo.findById(1L)).thenReturn(Optional.of(in(1L, 100.0, old)));
        when(egresoRepo.findById(2L)).thenReturn(Optional.of(eg(2L, 50.0, now, 1)));

        var out = repo.obtenerHistorial();

        assertEquals(2, out.size());
        assertTrue(out.get(0).getFecha().after(out.get(1).getFecha()) || out.get(0).getFecha().equals(out.get(1).getFecha()));
    }

    // Caso feliz: últimos movimientos limita tamaño.
    // Comportamiento esperado: retorna solo cantidad solicitada.
    @Test
    void ultimosLimita() {
        Date a = new Date(System.currentTimeMillis() - 2000);
        Date b = new Date(System.currentTimeMillis() - 1000);
        Date c = new Date();
        autenticarComo("usuario@test.com");

        when(movRepo.findByUserEmail("usuario@test.com")).thenReturn(List.of(mv("ingreso", 1), mv("ingreso", 2), mv("ingreso", 3)));
        when(ingresoRepo.findById(1L)).thenReturn(Optional.of(in(1L, 1, a)));
        when(ingresoRepo.findById(2L)).thenReturn(Optional.of(in(2L, 2, b)));
        when(ingresoRepo.findById(3L)).thenReturn(Optional.of(in(3L, 3, c)));

        var out = repo.obtenerUltimosMovimientos(2);
        assertEquals(2, out.size());
    }

    // Caso feliz: filtro por categoría en egresos.
    // Comportamiento esperado: incluye solo egresos con código solicitado.
    @Test
    void porCategoriaOk() {
        Date f = new Date();
        autenticarComo("usuario@test.com");

        when(movRepo.findByUserEmail("usuario@test.com")).thenReturn(List.of(
                mv("egreso", 1),
                mv("egreso", 2),
                mv("ingreso", 3),
                mv("egreso", null)));

        when(egresoRepo.findById(1L)).thenReturn(Optional.of(eg(1L, 10, f, 4)));
        when(egresoRepo.findById(2L)).thenReturn(Optional.of(eg(2L, 20, f, 1)));

        var out = repo.obtenerPorCategoria(4);

        assertEquals(1, out.size());
        assertEquals(10.0, out.get(0).getMonto());
    }
}
