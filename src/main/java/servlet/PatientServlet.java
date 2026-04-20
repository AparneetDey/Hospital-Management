package servlet;

import dao.PatientDao;
import dao.DoctorDao;
import model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class PatientServlet extends HttpServlet {
    private final PatientDao patientDao = new PatientDao();
    private final DoctorDao doctorDao = new DoctorDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("new".equals(action)) {
                showForm(request, response, null);
            } else if ("edit".equals(action)) {
                showEditForm(request, response);
            } else if ("delete".equals(action)) {
                deletePatient(request, response);
            } else {
                listPatients(request, response);
            }
        } catch (SQLException exception) {
            throw new ServletException("Unable to process patient request", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            String id = request.getParameter("id");
            Integer patientId = null;
            if (id != null && !id.trim().isEmpty()) {
                patientId = Integer.parseInt(id);
            }

            Patient patient = new Patient(
                    request.getParameter("name"),
                    Integer.parseInt(request.getParameter("age")),
                    request.getParameter("disease"),
                    parseDoctorId(request.getParameter("doctorId"))
            );
            if (patientId != null) {
                patient.setId(patientId);
            }

            if (patientId == null) {
                patientDao.save(patient);
            } else {
                patientDao.update(patient);
            }

            response.sendRedirect(request.getContextPath() + "/patients");
        } catch (NumberFormatException exception) {
            request.setAttribute("errorMessage", "Age must be a valid number.");
            showForm(request, response, buildPatientFromRequest(request));
        } catch (SQLException exception) {
            request.setAttribute("errorMessage", exception.getMessage());
            showForm(request, response, buildPatientFromRequest(request));
        }
    }

    private void listPatients(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute("patients", patientDao.findAll());
        request.getRequestDispatcher("/WEB-INF/views/patient-list.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Patient patient = patientDao.findById(id);
        showForm(request, response, patient);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Patient patient)
            throws ServletException, IOException {
        try {
            request.setAttribute("patient", patient);
            request.setAttribute("doctors", doctorDao.findAll());
            request.getRequestDispatcher("/WEB-INF/views/patient-form.jsp").forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to load doctors", exception);
        }
    }

    private void deletePatient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        patientDao.delete(id);
        response.sendRedirect(request.getContextPath() + "/patients");
    }

    private Integer parseDoctorId(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            return null;
        }

        return Integer.parseInt(doctorId);
    }

    private int parseAgeSafely(String ageValue) {
        try {
            return Integer.parseInt(ageValue);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private Integer parseDoctorIdSafely(String doctorIdValue) {
        try {
            return parseDoctorId(doctorIdValue);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Patient buildPatientFromRequest(HttpServletRequest request) {
        String id = request.getParameter("id");
        Integer patientId = null;
        if (id != null && !id.trim().isEmpty()) {
            try {
                patientId = Integer.parseInt(id);
            } catch (NumberFormatException ignored) {
                patientId = null;
            }
        }

        Patient patient = new Patient(
                request.getParameter("name"),
                parseAgeSafely(request.getParameter("age")),
                request.getParameter("disease"),
                parseDoctorIdSafely(request.getParameter("doctorId"))
        );
        if (patientId != null) {
            patient.setId(patientId);
        }
        return patient;
    }
}
