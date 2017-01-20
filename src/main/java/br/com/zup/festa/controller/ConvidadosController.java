package br.com.zup.festa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import br.com.zup.festa.model.Convidado;
import br.com.zup.festa.repository.Convidados;

@Controller
public class ConvidadosController {

	@Autowired
	private Convidados convidados;
	
	@RequestMapping("/convidados")
	public ModelAndView listar() { 
		ModelAndView mv = new ModelAndView("ListaConvidados");
		mv.addObject("convidados", convidados.todos());
		
		mv.addObject(new Convidado());
		
		return mv;
	}

	@RequestMapping(value = "/convidados", method = RequestMethod.POST)
	public String salvar(Convidado convidado) {
		this.convidados.adicionar(convidado);
		return "redirect:/convidados";
	}
	
}
