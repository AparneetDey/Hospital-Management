package dao;

import model.Patient;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {
    private static final String INSERT_PATIENT =
            "INSERT INTO patients (name, age, disease, doctor_id) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_PATIENTS =
            "SELECT id, name, age, disease, doctor_id FROM patients ORDER BY id ASC";
    private static final String SELECT_PATIENT_BY_ID =
            "SELECT id, name, age, disease, doctor_id FROM patients WHERE id = ?";
    private static final String UPDATE_PATIENT =
            "UPDATE patients SET name = ?, age = ?, disease = ?, doctor_id = ? WHERE id = ?";
    private static final String DELETE_PATIENT =
            "DELETE FROM patients WHERE id = ?";
    private static final String UPDATE_DOCTOR_ALLOTMENT =
            "UPDATE doctors SET alloted = ? WHERE id = ?";
    private static final String CLEAR_DOCTOR_ALLOTMENT =
            "UPDATE doctors SET alloted = '' WHERE id = ? AND alloted = ?";

    public void save(Patient patient) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            assertDoctorAvailableForAssignment(connection, patient.getDoctorId(), null);

            try (PreparedStatement statement = connection.prepareStatement(INSERT_PATIENT, Statement.RETURN_GENERATED_KEYS)) {
                setPatientValues(statement, patient);
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId(generatedKeys.getInt(1));
                    }
                }
            }

            updateDoctorAllotment(connection, patient.getDoctorId(), patient.getId());
            connection.commit();
        } catch (SQLException exception) {
            throw exception;
        }
    }

    public List<Patient> findAll() throws SQLException {
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PATIENTS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                patients.add(mapPatient(resultSet));
            }
        }

        return patients;
    }

    public Patient findById(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PATIENT_BY_ID)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPatient(resultSet);
                }
            }
        }

        return null;
    }

    public void update(Patient patient) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            Integer previousDoctorId = findDoctorIdByPatientId(connection, patient.getId());
            assertDoctorAvailableForAssignment(connection, patient.getDoctorId(), patient.getId());

            try (PreparedStatement statement = connection.prepareStatement(UPDATE_PATIENT)) {
                setPatientValues(statement, patient);
                statement.setInt(5, patient.getId());
                statement.executeUpdate();
            }

            if (previousDoctorId != null && !previousDoctorId.equals(patient.getDoctorId())) {
                clearDoctorAllotment(connection, previousDoctorId, patient.getId());
            }

            updateDoctorAllotment(connection, patient.getDoctorId(), patient.getId());
            connection.commit();
        } catch (SQLException exception) {
            throw exception;
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            Integer doctorId = findDoctorIdByPatientId(connection, id);

            try (PreparedStatement statement = connection.prepareStatement(DELETE_PATIENT)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }

            if (doctorId != null) {
                clearDoctorAllotment(connection, doctorId, id);
            }

            connection.commit();
        }
    }

    private void setPatientValues(PreparedStatement statement, Patient patient) throws SQLException {
        statement.setString(1, patient.getName());
        statement.setInt(2, patient.getAge());
        statement.setString(3, patient.getDisease());

        if (patient.getDoctorId() == null) {
            statement.setNull(4, java.sql.Types.INTEGER);
        } else {
            statement.setInt(4, patient.getDoctorId());
        }
    }

    private Integer findDoctorIdByPatientId(Connection connection, int patientId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT doctor_id FROM patients WHERE id = ?")) {
            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int doctorId = resultSet.getInt("doctor_id");
                    return resultSet.wasNull() ? null : doctorId;
                }
            }
        }

        return null;
    }

    private void assertDoctorAvailableForAssignment(Connection connection, Integer doctorId, Integer currentPatientId)
            throws SQLException {
        if (doctorId == null) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT alloted FROM doctors WHERE id = ? FOR UPDATE")) {
            statement.setInt(1, doctorId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Selected doctor does not exist.");
                }

                String alloted = resultSet.getString("alloted");
                boolean assigned = alloted != null && !alloted.trim().isEmpty();
                boolean assignedToCurrent = currentPatientId != null
                        && String.valueOf(currentPatientId).equals(alloted);

                if (assigned && !assignedToCurrent) {
                    throw new SQLException("Selected doctor is already assigned to another patient.");
                }
            }
        }
    }

    private void updateDoctorAllotment(Connection connection, Integer doctorId, int patientId) throws SQLException {
        if (doctorId == null) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_DOCTOR_ALLOTMENT)) {
            statement.setString(1, String.valueOf(patientId));
            statement.setInt(2, doctorId);
            statement.executeUpdate();
        }
    }

    private void clearDoctorAllotment(Connection connection, int doctorId, int patientId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CLEAR_DOCTOR_ALLOTMENT)) {
            statement.setInt(1, doctorId);
            statement.setString(2, String.valueOf(patientId));
            statement.executeUpdate();
        }
    }

    private Patient mapPatient(ResultSet resultSet) throws SQLException {
        int doctorId = resultSet.getInt("doctor_id");
        return new Patient(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                resultSet.getString("disease"),
                resultSet.wasNull() ? null : doctorId
        );
    }
}
