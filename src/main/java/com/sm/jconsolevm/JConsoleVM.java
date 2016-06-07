package com.sm.jconsolevm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

@Controller
@EnableAutoConfiguration
@ComponentScan("com.sm.jconsolevm")
public class JConsoleVM {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(JConsoleVM.class, args);
    }
	
}
