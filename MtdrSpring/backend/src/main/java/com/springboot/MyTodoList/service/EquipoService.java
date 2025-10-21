package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Equipo;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import com.springboot.MyTodoList.model.RolEquipo;
import com.springboot.MyTodoList.repository.EquipoRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepo;
    private final UsuarioEquipoRepository ueRepo;

    public EquipoService(EquipoRepository equipoRepo, UsuarioEquipoRepository ueRepo) {
        this.equipoRepo = equipoRepo;
        this.ueRepo = ueRepo;
    }

    @Transactional
    public Equipo crearEquipoConAdmin(Equipo equipo, Long creadorUsuarioId) {
        Equipo saved = equipoRepo.save(equipo);
        UsuarioEquipoId id = new UsuarioEquipoId(creadorUsuarioId, saved.getId());
        UsuarioEquipo ue = new UsuarioEquipo(id, RolEquipo.ADMIN);
        ueRepo.save(ue);
        return saved;
    }

    @Transactional
    public void eliminarEquipo(Long equipoId) {
        // 1) Borrar relaciones
        ueRepo.deleteByEquipoId(equipoId);
        // 2) Borrar equipo
        equipoRepo.deleteById(equipoId);
    }
}
