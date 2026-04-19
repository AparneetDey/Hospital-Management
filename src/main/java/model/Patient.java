package model;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String disease;
    private Integer doctorId;

    public Patient() {
    }

    public Patient(int id, String name, int age, String disease) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
    }

    public Patient(int id, String name, int age, String disease, Integer doctorId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
        this.doctorId = doctorId;
    }

    public Patient(String name, int age, String disease, Integer doctorId) {
        this.name = name;
        this.age = age;
        this.disease = disease;
        this.doctorId = doctorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
}
