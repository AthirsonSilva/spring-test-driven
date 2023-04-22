package com.springtestdriven.controller;

import com.springtestdriven.entity.EmployeeEntity;
import com.springtestdriven.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee API")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeEntity>> getEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeEntity> getEmployee(@PathVariable Long id) {
        EmployeeEntity employeeEntity = employeeService.findById(id).orElse(null);

        if (employeeEntity == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).body(employeeEntity);
    }

    @PostMapping
    public ResponseEntity<EmployeeEntity> createEmployee(@RequestBody EmployeeEntity request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<EmployeeEntity> updateEmployee(@PathVariable Long id, @RequestBody EmployeeEntity request) {
        EmployeeEntity employeeEntity = employeeService.findById(id).orElse(null);

        if (employeeEntity == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.update(request, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        EmployeeEntity employeeEntity = employeeService.findById(id).orElse(null);

        if (employeeEntity == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        employeeService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
