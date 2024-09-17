package com.example.rqchallenge.employees;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.EmployeeService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@Slf4j
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController {

  private final EmployeeService employeeService;

  @Override
  @GetMapping
  public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
    log.info("Fetching all employees");
    List<Employee> employees = employeeService.getAllEmployees();
    return ResponseEntity.ok(employees);
  }

  @Override
  @GetMapping("/search/{searchString}")
  public ResponseEntity<List<Employee>> getEmployeesByNameSearch(
      @PathVariable String searchString) {
    log.info("Searching employees with name containing: {}", searchString);
    List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
    return ResponseEntity.ok(employees);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
    log.info("Fetching employee with ID: {}", id);
    Employee employee = employeeService.getEmployeeById(id);
    return ResponseEntity.ok(employee);
  }

  @Override
  @GetMapping("/highestSalary")
  public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
    log.info("Fetching the highest salary of employees");
    Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
    return ResponseEntity.ok(highestSalary);
  }

  @Override
  @GetMapping("/topTenHighestEarningEmployeeNames")
  public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
    log.info("Fetching the top 10 highest earning employee names");
    List<String> topEmployeeNames = employeeService.getTop10HighestEarningEmployeeNames();
    return ResponseEntity.ok(topEmployeeNames);
  }

  @Override
  @PostMapping
  public ResponseEntity<String> createEmployee(@RequestBody Map<String, Object> employeeInput) {
    log.info("Creating employee with input: {}", employeeInput);
    return ResponseEntity.ok(employeeService.createEmployee(employeeInput.get("name").toString(),
        employeeInput.get("salary").toString(), employeeInput.get("age").toString()));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
    log.info("Deleting employee with input: {}", id);
    return ResponseEntity.ok(employeeService.deleteEmployee(id));
  }
}
