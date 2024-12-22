package com.lakshay.student_service.service;

import com.lakshay.student_service.dto.School;
import com.lakshay.student_service.dto.StudentResponse;
import com.lakshay.student_service.model.Student;
import com.lakshay.student_service.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private StudentRepository studentRepository;
    private RestTemplate restTemplate;

    public StudentService(StudentRepository studentRepository, RestTemplate restTemplate) {
        this.studentRepository = studentRepository;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> createStudent(Student student){
        try{
            return new ResponseEntity<Student>(studentRepository.save(student) , HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> fetchStudentById(String id){
        Optional<Student> student = studentRepository.findById(id);
        if(student.isPresent()) {
            School school = restTemplate.getForObject("http://SCHOOL-SERVICE/school/" + student.get().getSchoolId(),School.class);
            StudentResponse studentResponse = new StudentResponse(
                    student.get().getId(),
                    student.get().getName(),
                    student.get().getAge(),
                    student.get().getGender(),
                    school
            );
            return new ResponseEntity<>(studentResponse , HttpStatus.OK);
        }else {
            return new ResponseEntity<>("No Student record found" , HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> fetchStudents() {
        List<Student> students = studentRepository.findAll();
        if(!students.isEmpty()){
            return new ResponseEntity<List<Student>>(students,HttpStatus.OK);
        }else {
            return new ResponseEntity<>("No Students record found" , HttpStatus.NOT_FOUND);
        }
    }


}
