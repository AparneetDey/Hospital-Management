package model;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String alloted;

    public Doctor() {
    }

    public Doctor(int id, String name, String specialization, String alloted) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.alloted = alloted;
    }

    public Doctor(String name, String specialization, String alloted) {
        this.name = name;
        this.specialization = specialization;
        this.alloted = alloted;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAlloted() {
        return alloted;
    }

    public void setAlloted(String alloted) {
        this.alloted = alloted;
    }
}
