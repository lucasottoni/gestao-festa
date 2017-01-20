package br.com.zup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe que instancia o server para o SpringBoot
 * @author Lucas
 *
 */
@SpringBootApplication
public class ZupRestApp {

    public static void main(String[] args) {
        SpringApplication.run(ZupRestApp.class, args);
    }
}