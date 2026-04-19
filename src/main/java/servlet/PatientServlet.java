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
            Patient patient = new Patient(
                    request.getParameter("name"),
                    Integer.parseInt(request.getParameter("age")),
                    request.getParameter("disease"),
                    parseDoctorId(request.getParameter("doctorId"))
            );

            if (id == null || id.trim().isEmpty()) {
                patientDao.save(patient);
            } else {
                patient.setId(Integer.parseInt(id));
                patientDao.update(patient);
            }

            response.sendRedirect(request.getContextPath() + "/patients");
        } catch (NumberFormatException exception) {
            request.setAttribute("errorMessage", "Age must be a valid number.");
            showForm(request, response, null);
        } catch (SQLException exception) {
            throw new ServletException("Unable to save patient", exception);
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
}
