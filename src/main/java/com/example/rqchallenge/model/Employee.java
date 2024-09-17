package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

  @NotBlank(message = "ID cannot be blank")
  private String id;

  @JsonProperty("employee_name")
  @NotBlank(message = "Name cannot be blank")
  @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
  private String employeeName;

  @JsonProperty("employee_salary")
  @NotBlank(message = "Salary cannot be blank")
  @Pattern(regexp = "\\d+", message = "Salary must be a valid number")
  private String employeeSalary;

  @JsonProperty("employee_age")
  @NotNull(message = "Age cannot be null")
  @Positive(message = "Age must be a positive number")
  private String employeeAge;

  @JsonProperty("profile_image")
  private String profileImage; // file path or url perhaps
}