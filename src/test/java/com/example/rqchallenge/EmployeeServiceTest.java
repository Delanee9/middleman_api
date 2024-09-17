package com.example.rqchallenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.Response;
import com.example.rqchallenge.service.EmployeeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

  private final String apiUrl = "null";
  @Mock
  private RestTemplate restTemplate;
  @InjectMocks
  private EmployeeServiceImpl employeeService;
  private List<Employee> employees;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() throws IOException {

    objectMapper = new ObjectMapper();

    File file = new File("src/test/resources/EmployeesValid.json");
    if (!file.exists()) {
      throw new IOException("File not found: " + file.getAbsolutePath());
    }

    Response response = objectMapper.readValue(file, Response.class);
    employees = response.getData();
  }

  @Test
  void getAllEmployees_success() {
    Response mockResponse = new Response();
    mockResponse.setData(employees);  // Ensure this is populated
    mockResponse.setStatus("ok");
    mockResponse.setMessage("passed");
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.getForEntity(apiUrl + "/employees", Response.class)).thenReturn(
        responseEntity);

    List<Employee> result = employeeService.getAllEmployees();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Tiger Nixon", result.get(0).getEmployeeName());
    assertEquals("Garrett Winters", result.get(1).getEmployeeName());
  }

  @Test
  void getAllEmployees_failure() {
    when(restTemplate.getForEntity(apiUrl + "/employees", Response.class))
        .thenThrow(new RuntimeException("External API error"));

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.getAllEmployees());
    assertEquals("Error fetching employees", exception.getMessage());
  }

  @Test
  void getEmployeesByNameSearch_success() {
    Response mockResponse = new Response();
    mockResponse.setData(employees);

    ResponseEntity<Response> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.getForEntity(apiUrl + "/employees", Response.class)).thenReturn(
        responseEntity);

    List<Employee> result = employeeService.getEmployeesByNameSearch("Tiger");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Tiger Nixon", result.get(0).getEmployeeName());
  }

  @Test
  void getEmployeesByNameSearch_failure() {
    when(restTemplate.getForEntity(apiUrl + "/employees", Response.class))
        .thenThrow(new RuntimeException("Error fetching employees"));

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.getEmployeesByNameSearch("John"));
    assertEquals("Error searching employees", exception.getMessage());
  }

  @Test
  void getEmployeeById_success() {
    Response mockResponse = new Response();
    mockResponse.setData(employees);

    ResponseEntity<Response> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.getForEntity(apiUrl + "/employee/1", Response.class)).thenReturn(
        responseEntity);

    Employee result = employeeService.getEmployeeById("1");

    assertNotNull(result);
    assertEquals("Tiger Nixon", result.getEmployeeName());
  }

  @Test
  void getEmployeeById_failure() {
    when(restTemplate.getForEntity(apiUrl + "/employee/1", Response.class)).thenThrow(
        new RuntimeException("Employee not found"));
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.getEmployeeById("1"));
    assertEquals("Error fetching employee", exception.getMessage());
  }

  @Test
  void getHighestSalaryOfEmployees_success() {
    Response mockResponse = new Response();
    mockResponse.setData(employees);

    ResponseEntity<Response> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.getForEntity(apiUrl + "/employees", Response.class)).thenReturn(
        responseEntity);
    int highestSalary = employeeService.getHighestSalaryOfEmployees();
    assertEquals(320800, highestSalary);
  }

  @Test
  void getHighestSalaryOfEmployees_failure() {
    when(restTemplate.getForEntity(apiUrl + "/employees", Response.class))
        .thenThrow(new RuntimeException("Error fetching employees"));

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.getHighestSalaryOfEmployees());
    assertEquals("Error fetching highest salary", exception.getMessage());
  }

  @Test
  void createEmployee_success() {
    // Mock API response
    ResponseEntity<String> responseEntity = new ResponseEntity<>("Employee created",
        HttpStatus.CREATED);
    String payload = """
        {
          name:John,
          salary:5000,
          age:30
        }
        """;
    when(restTemplate.postForEntity(apiUrl + "/create", payload, String.class)).thenReturn(
        responseEntity);

    String result = employeeService.createEmployee("John", "5000", "30");

    assertEquals(HttpStatus.CREATED.toString(), result);
  }

  @Test
  void createEmployee_failure() {
    String payload = """
        {
          name:John,
          salary:5000,
          age:30
        }
        """;
    when(restTemplate.postForEntity(apiUrl + "/create", payload, String.class))
        .thenThrow(new RuntimeException("Error creating employee"));

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.createEmployee("John", "5000", "30"));
    assertEquals("Error creating employee", exception.getMessage());
  }


}