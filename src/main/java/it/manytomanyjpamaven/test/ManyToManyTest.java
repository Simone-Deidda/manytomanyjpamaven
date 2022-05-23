package it.manytomanyjpamaven.test;

import java.util.Date;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testInserisciNuovoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testInserisciNuovoRuolo(ruoloServiceInstance);
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");

			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testModificaStatoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testModificaStatoRuolo(ruoloServiceInstance);
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");

			testRimuoviRuoloDaUtente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testRimuoviUtente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testRimuoviRuolo(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");

			testCaricaUtentePerId(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testCaricaRuoloPerId(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testInserisciNuovoRuolo(RuoloService ruoloServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoRuolo inizio.............");

		Ruolo utenteNuovo = new Ruolo("qualcosa", "ROLE_SOMETHING");
		ruoloServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoRuolo fallito ");

		System.out.println(".......testInserisciNuovoRuolo fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testModificaStatoRuolo(RuoloService ruoloServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoRuolo inizio.............");

		Ruolo primoRuolo = ruoloServiceInstance.listAll().get(0);
		String vecchiaDescrizione = primoRuolo.getDescrizione();
		String nuovaDescrizione = "nuova descrizione";
		primoRuolo.setDescrizione(nuovaDescrizione);

		ruoloServiceInstance.aggiorna(primoRuolo);
		if (!ruoloServiceInstance.caricaSingoloElemento(primoRuolo.getId()).getDescrizione().equals(nuovaDescrizione))
			throw new RuntimeException("testModificaStatoRuolo fallito: modifica non avvenuta correttamente ");

		primoRuolo.setDescrizione(vecchiaDescrizione);
		ruoloServiceInstance.aggiorna(primoRuolo);
		System.out.println(".......testModificaStatoRuolo fine: PASSED.............");
	}

	private static void testRimuoviRuoloDaUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviRuoloDaUtente inizio.............");

		// carico un ruolo e lo associo ad un nuovo utente
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("aldo.manuzzi", "pwd@2", "aldo", "manuzzi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente non inserito ");
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);

		// ora ricarico il record e provo a disassociare il ruolo
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		boolean confermoRuoloPresente = false;
		for (Ruolo ruoloItem : utenteReloaded.getRuoli()) {
			if (ruoloItem.getCodice().equals(ruoloEsistenteSuDb.getCodice())) {
				confermoRuoloPresente = true;
				break;
			}
		}

		if (!confermoRuoloPresente)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente e ruolo non associati ");

		// ora provo la rimozione vera e propria ma poi forzo il caricamento per fare un
		// confronto 'pulito'
		utenteServiceInstance.rimuoviRuoloDaUtente(utenteReloaded.getId(), ruoloEsistenteSuDb.getId());
		utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (!utenteReloaded.getRuoli().isEmpty())
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo ancora associato ");

		System.out.println(".......testRimuoviRuoloDaUtente fine: PASSED.............");
	}

	private static void testRimuoviUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviUtente inizio.............");

		Utente utenteNuovo = new Utente("filippo.ortu", "pwd$@16k", "Filippo", "Ortu", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testRimuoviUtente fallito: utente non inserito ");

		utenteServiceInstance.rimuovi(utenteNuovo.getId());

		for (Utente utente : utenteServiceInstance.listAll()) {
			if (utente.equals(utenteNuovo)) {
				throw new RuntimeException("testRimuoviUtente fallito: utente ancora presente nel DB ");
			}
		}
		System.out.println(".......testRimuoviUtente fine: PASSED.............");
	}

	private static void testRimuoviRuolo(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviRuolo inizio.............");

		Ruolo ruoloNuovo = new Ruolo("CODICE", "descrizione");
		ruoloServiceInstance.inserisciNuovo(ruoloNuovo);
		if (ruoloNuovo.getId() == null)
			throw new RuntimeException("testRimuoviRuolo fallito: ruolo non inserito ");

		ruoloServiceInstance.rimuovi(ruoloNuovo.getId());

		for (Ruolo ruolo : ruoloServiceInstance.listAll()) {
			if (ruolo.equals(ruoloNuovo)) {
				throw new RuntimeException("testRimuoviRuolo fallito: utente ancora presente nel DB ");
			}
		}
		System.out.println(".......testRimuoviRuolo fine: PASSED.............");
	}

	private static void testCaricaUtentePerId(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testCaricaUtentePerId inizio.............");

		Utente utenteNuovo = new Utente("gino.paoli", "pwd$@2", "Gino", "Paoli", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testCaricaUtentePerId fallito: utente non inserito ");

		Utente utenteReloaded = utenteServiceInstance.caricaSingoloElemento(utenteNuovo.getId());

		if (!utenteReloaded.getId().equals(utenteNuovo.getId()))
			throw new RuntimeException(
					"testCaricaUtentePerId fallito: utente inserito non è uguale a quello ripescato dal DB ");

		System.out.println(".......testCaricaUtentePerId fine: PASSED.............");
	}

	private static void testCaricaRuoloPerId(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testCaricaRuoloPerId inizio.............");

		Ruolo primoRuolo = ruoloServiceInstance.listAll().get(0);

		Ruolo ruoloReloaded = ruoloServiceInstance.caricaSingoloElemento(primoRuolo.getId());

		if (!ruoloReloaded.getId().equals(primoRuolo.getId()))
			throw new RuntimeException(
					"testCaricaRuoloPerId fallito: ruolo inserito non è uguale a quello ripescato dal DB ");

		System.out.println(".......testCaricaRuoloPerId fine: PASSED.............");
	}

}
