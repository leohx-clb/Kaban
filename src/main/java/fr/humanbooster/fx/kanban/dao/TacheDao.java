package fr.humanbooster.fx.kanban.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.humanbooster.fx.kanban.business.Tache;

public interface TacheDao extends JpaRepository<Tache, Long> {

	List<Tache> findByIntituleContaining(String intituler);

	List<Tache> findALLByDateCreationBetween(Date dateDebut, Date dateFin);

	
}