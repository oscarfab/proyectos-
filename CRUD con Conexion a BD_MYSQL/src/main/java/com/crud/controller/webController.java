package com.crud.controller;


import com.crud.modelo.Empleado;
import com.crud.servicio.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class webController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/")
    public String listaEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.obtenerTodos());
        model.addAttribute("empleado", new Empleado());
        return "empleados"; // nombre de la plantilla HTML
    }

    @PostMapping("/agregar")
    public String agregarEmpleado(@ModelAttribute Empleado empleado) {
        empleadoService.create(empleado);
        return "redirect:/";
    }
}