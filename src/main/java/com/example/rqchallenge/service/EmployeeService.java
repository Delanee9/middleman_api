package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;
import java.util.List;

public interface EmployeeService {

  // Get all of the employees
  List<Employee> getAllEmployees();

  // Get employees by name search
  List<Employee> getEmployeesByNameSearch(String name);

  // Get an employee by ID
  Employee getEmployeeById(String id);

  // Get the highest salary of any employee
  int getHighestSalaryOfEmployees();

  // Returns the names of the top 10 highest earning employees
  List<String> getTop10HighestEarningEmployeeNames();

  // Create a new employee by providing parameters
  String createEmployee(String name, String salary, String age);

  // Delete an employee by ID
  String deleteEmployee(String id);
}
