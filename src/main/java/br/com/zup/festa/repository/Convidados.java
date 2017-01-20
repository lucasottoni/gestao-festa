package br.com.zup.festa.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.zup.festa.model.Convidado;
import br.com.zup.services.DefinedEntityService;

@Repository
public class Convidados {

	private static final List<Convidado> LISTA_CONVIDADOS = new ArrayList<>();
	
	@Autowired
	private final DefinedEntityService entityService;
	private final Gson gson;
	
	static {
		LISTA_CONVIDADOS.add(new Convidado("Pedro", 2));
		LISTA_CONVIDADOS.add(new Convidado("Maria", 3));
		LISTA_CONVIDADOS.add(new Convidado("Ricardo", 1));
	}
	
	@Autowired
	public Convidados(DefinedEntityService entityService) {
		this.entityService = entityService;
        this.gson = new GsonBuilder().create();
	}
	
	public List<Convidado> todos() {
		//return Convidados.LISTA_CONVIDADOS;
		String json = entityService.getAllEntities("convidados");
		Convidado[] mcArray = gson.fromJson(json, Convidado[].class);

		return Arrays.asList(mcArray); 
	}

	public void adicionar(Convidado convidado) {
		entityService.saveEntity("convidados", gson.toJson(convidado));
	}
	
}
