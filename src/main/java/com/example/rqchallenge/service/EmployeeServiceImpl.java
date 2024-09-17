package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final RestTemplate restTemplate;

  /**
   * External api url stored in application.yaml
   */
  @Value("${employee.api.url}")
  private String apiUrl;

  /**
   * Return a list of all employees
   *
   * @return List<Employee>
   */
  @Override
  public List<Employee> getAllEmployees() {
    log.info("Fetching all employees from external API");
    String url = apiUrl + "/employees";
    try {
      ResponseEntity<Response> responseEntity = restTemplate.getForEntity(url, Response.class);
      log.debug("Received employees data: {}", responseEntity.getBody());
      Response response = responseEntity.getBody();
      return response.getData();
    } catch (Exception e) {
      log.error("Failed to fetch all employees", e);
      throw new RuntimeException("Error fetching employees", e);
    }
  }

  /**
   * Return all employees based on a name search
   *
   * @param name
   * @return List<Employee>
   */
  @Override
  public List<Employee> getEmployeesByNameSearch(String name) {
    log.info("Searching for employees with name containing: {}", name);
    try {
      List<Employee> employees = getAllEmployees();
      List<Employee> filteredEmployees = employees.stream()
          .filter(e -> e.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
          .toList();
      log.debug("Employees found: {}", filteredEmployees);
      return filteredEmployees;
    } catch (Exception e) {
      log.error("Error searching for employees by name: {}", name, e);
      throw new RuntimeException("Error searching employees", e);
    }
  }

  /**
   * Find an employee by ID
   *
   * @param id
   * @return Employee
   */
  @Override
  @Cacheable("employees")
  public Employee getEmployeeById(String id) {
    log.info("Fetching employee with ID: {}", id);
    String url = apiUrl + "/employee/" + id;
    try {
      ResponseEntity<Response> responseEntity = restTemplate.getForEntity(url, Response.class);
      log.info("Employee data: {}", responseEntity.getBody());
      Response response = responseEntity.getBody();
      return response.getData().get(0);
    } catch (Exception e) {
      log.error("Failed to fetch employee with ID: {}", id, e);
      throw new RuntimeException("Error fetching employee", e);
    }
  }

  /**
   * Returns the highest salary of any employee
   *
   * @return int
   */
  @Override
  public int getHighestSalaryOfEmployees() {
    log.info("Fetching highest salary of employees");
    try {
      return getAllEmployees().stream()
          .mapToInt(e -> Integer.parseInt(e.getEmployeeSalary()))
          .max()
          .orElse(0);
    } catch (Exception e) {
      log.error("Error fetching highest salary", e);
      throw new RuntimeException("Error fetching highest salary", e);
    }
  }

  /**
   * Returns a list of names of the top 10 highest earning employees
   *
   * @return List<String>
   */
  @Override
  public List<String> getTop10HighestEarningEmployeeNames() {
    log.info("Fetching top 10 highest earning employees");
    try {
      List<String> topEarners = getAllEmployees().stream()
          .sorted((e1, e2) -> Integer.compare(Integer.parseInt(e2.getEmployeeSalary()),
              Integer.parseInt(e1.getEmployeeSalary())))
          .limit(10)
          .map(Employee::getEmployeeName)
          .toList();
      log.debug("Top 10 employees: {}", topEarners);
      return topEarners;
    } catch (Exception e) {
      log.error("Error fetching top 10 highest earning employees", e);
      throw new RuntimeException("Error fetching top 10 highest earning employees", e);
    }
  }

  /**
   * Create a new employee record
   *
   * @param name
   * @param salary
   * @param age
   * @return String
   */
  @Override
  public String createEmployee(String name, String salary, String age) {
    log.info("Creating employee with name: {}, salary: {}, age: {}", name, salary, age);
    String url = apiUrl + "/create";
    String payload = """
        {
          name:%s,
          salary:%s,
          age:%s
        }
        """.formatted(name, salary, age);
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, payload, String.class);
      log.debug("Employee creation response: {}", response.getBody());
      return response.getStatusCode().toString();
    } catch (Exception e) {
      log.error("Error creating employee", e);
      throw new RuntimeException("Error creating employee", e);
    }
  }

  /**
   * Delete employee by ID
   *
   * @param id
   * @return String
   */
  @Override
  @CacheEvict(value = "employees", key = "#id")
  public String deleteEmployee(String id) {
    log.info("Deleting employee with ID: {}", id);
    String url = apiUrl + "/delete/" + id;
    try {
      Employee employee = getEmployeeById(id);
      restTemplate.getForEntity(url, Object.class);
      log.info("Deleted employee: {}", employee.getEmployeeName());
      return employee.getEmployeeName();
    } catch (Exception e) {
      log.error("Error deleting employee with ID: {}", id, e);
      throw new RuntimeException("Error deleting employee", e);
    }
  }
}
