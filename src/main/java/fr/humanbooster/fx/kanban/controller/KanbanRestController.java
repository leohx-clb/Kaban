package fr.humanbooster.fx.kanban.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.humanbooster.fx.kanban.business.Colonne;
import fr.humanbooster.fx.kanban.business.Tache;
import fr.humanbooster.fx.kanban.service.ColonneService;
import fr.humanbooster.fx.kanban.service.DeveloppeurService;
import fr.humanbooster.fx.kanban.service.TacheService;
import fr.humanbooster.fx.kanban.service.TypeTacheService;

@RestController
public class KanbanRestController {
	
	private final TacheService tacheService;
	private final DeveloppeurService developpeurService;
	private final ColonneService colonneService;
	private final TypeTacheService typeTacheService;
	
	public KanbanRestController(TacheService tacheService, DeveloppeurService developpeurService,
			ColonneService colonneService, TypeTacheService typeTacheService) {
		super();
		this.tacheService = tacheService;
		this.developpeurService = developpeurService;
		this.colonneService = colonneService;
		this.typeTacheService = typeTacheService;
	}
	
	/**
	 * une méthode qui ajoute une tâche en précisant son intitulé et le type de tâche.
	 * Exemple : http://localhost:8080/ taches/Corriger%20CSS/Bug
	 * @param nomTache
	 * @param typeDeTache
	 * @return
	 */
	@PostMapping("taches/{nomTache}/{typeDeTache}")
	public Tache ajouterTache(@PathVariable String nomTache, @PathVariable String typeDeTache) {
		return tacheService.ajouterTache(nomTache ,typeDeTache);
	}
	
	/**
	 * B) une méthode permettant d’obtenir toutes les informations sur une tâche
	 * Exemple : http://localhost:8080/taches/2
	 * @param id
	 * @return
	 */
	@GetMapping("taches/{id}")
	public Tache recupererTache(@PathVariable Long id) {
		return tacheService.recupererTache(id);
	}
	
	/**
	 * C) une méthode qui permet de mettre à jour l’intitulé d’une tâche dont
	 * l’id est précisé dans l’URL
	 * Exemple : http://localhost:8080/taches/2/Corriger%20JSP%20index
	 * @param idTache
	 * @param nouveauNom
	 * @return
	 */
	@PutMapping("taches/{idTache}/{nouveauNom}")
	public Tache mettreAJourTache(@PathVariable Long idTache, @PathVariable String nouveauNom) {
		Tache tache = tacheService.recupererTache(idTache);
		if (tache != null) {
			tache.setIntitule(nouveauNom);
			return tacheService.enregistrerTache(tache);
		}
		return null;
	}
	
	/**
	 * D) une méthode permettant de supprimer une tâche en précisant son id
	 * Exemple : http://localhost:8080/taches/2
	 * @param id
	 */
	@DeleteMapping("taches/{id}")
	public void supprimerTache(@PathVariable Long id) {
		Tache tache = tacheService.recupererTache(id);
		tacheService.supprimerTache(tache);
	}
	
	//E) une méthode permettant d’obtenir une page de toutes les tâches placées dans une colonne
	// Exemple : http://localhost:8080/colonnes/2/taches?page=1&size=5
	//@GetMapping
	
	
	/*
	//F) une méthode permettant d’obtenir les tâches ayant le statut « à faire » et confiées à un développeur en particulier
	//Exemple : http://localhost:8080/developpeurs/2/tachesAFaire
	@GetMapping("developpeurs/{idDev}/tachesAFaire")
	public List<Tache> recupererTachesAFaireEtConfierAUnDev(@PathVariable Long idDev){
		return tacheService.recupererTaches(idDev);
	}*/
	
	/**
	 * G) une méthode permettant d’obtenir toutes les tâches dont l’intitulé contient le mot précisé dans l’URL
	 * Exemple : http://localhost:8080/taches?intitule=Timer
	 * @param intituler
	 * @return
	 */
	@GetMapping("taches")
	public List<Tache> recuprerTachesFilter(@RequestParam ("intitule") String intituler) {
		System.out.println(intituler);
		return tacheService.recuprerTachesFilter(intituler);
	}
	

	/**
	 * H) une méthode permettant de déterminer le total des heures prévues pour les tâchées créées entre deux dates données en paramètre
	 * Exemple : http://localhost:8080 /totalHeuresPrevues?dateDebut=2021-09-01&dateFin=2021-09-30
	 * @param dateDebut
	 * @param dateFin
	 * @return
	 */
	@GetMapping("totalHeuresPrevues")
	public int totalHeuresPrevues(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "dateDebut", required = false) Date dateDebut,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "dateFin", required = false) Date dateFin) {
		return tacheService.totaleHeureEstimerEntreDeuxDate(dateDebut,dateFin);
	}
	
	/**
	 * I) une méthode permettant de supprimer toutes les tâches d’une colonne
	 * Exemple : http://localhost:8080/dropColone/2
	 * @param idColone
	 */
	@GetMapping("dropColone/{idColone}")
	public void supprimerColone(@PathVariable Long idColone) {
		//List<Tache> tachesASupprimer = new ArrayList<>();
		List<Tache> taches = tacheService.recupererTaches();
		
		for (Tache tache : taches) {
			if (tache.getColonne().getId() == idColone) {
				tacheService.supprimerTache(tache);
			}
		}
	}
	
	/**
	 * une méthode permettant de gérer le déplacement d’une tâche effectué sur le tableau Kanban de la page Web de l’application
	 * Exemple : http://localhost:8080/deplacer/1/2
	 * @param idTache
	 * @param idColone
	 */
	@GetMapping("deplacer/{idTache}/{idColone}")
	public void deplacerTache(@PathVariable Long idTache, @PathVariable Long idColone) {
		Tache tache = tacheService.recupererTache(idTache);
		if (tache != null) {
				Colonne colonne = colonneService.recupererColonnes(idColone);
				tache.setColonne(colonne);
				tacheService.enregistrerTache(tache);
		}

		
	}
	
	
}
