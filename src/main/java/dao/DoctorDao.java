package dao;

import model.Doctor;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDao {
    private static final String INSERT_DOCTOR =
            "INSERT INTO doctors (name, specialization, alloted) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_DOCTORS =
            "SELECT id, name, specialization, alloted FROM doctors ORDER BY id ASC";
    private static final String SELECT_DOCTOR_BY_ID =
            "SELECT id, name, specialization, alloted FROM doctors WHERE id = ?";
    private static final String UPDATE_DOCTOR =
            "UPDATE doctors SET name = ?, specialization = ?, alloted = ? WHERE id = ?";
    private static final String DELETE_DOCTOR =
            "DELETE FROM doctors WHERE id = ?";

    public void save(Doctor doctor) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_DOCTOR)) {
            statement.setString(1, doctor.getName());
            statement.setString(2, doctor.getSpecialization());
            statement.setString(3, doctor.getAlloted());
            statement.executeUpdate();
        }
    }

    public List<Doctor> findAll() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_DOCTORS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                doctors.add(mapDoctor(resultSet));
            }
        }

        return doctors;
    }

    public Doctor findById(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_DOCTOR_BY_ID)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDoctor(resultSet);
                }
            }
        }

        return null;
    }

    public void update(Doctor doctor) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_DOCTOR)) {
            statement.setString(1, doctor.getName());
            statement.setString(2, doctor.getSpecialization());
            statement.setString(3, doctor.getAlloted());
            statement.setInt(4, doctor.getId());
            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_DOCTOR)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Doctor mapDoctor(ResultSet resultSet) throws SQLException {
        return new Doctor(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("specialization"),
                resultSet.getString("alloted")
        );
    }
}
