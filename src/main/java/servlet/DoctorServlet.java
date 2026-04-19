package servlet;

import dao.DoctorDao;
import model.Doctor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class DoctorServlet extends HttpServlet {
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
                deleteDoctor(request, response);
            } else {
                listDoctors(request, response);
            }
        } catch (SQLException exception) {
            throw new ServletException("Unable to process doctor request", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            String id = request.getParameter("id");
            String alloted = "";
            if (id != null && !id.trim().isEmpty()) {
                Doctor existingDoctor = doctorDao.findById(Integer.parseInt(id));
                if (existingDoctor != null) {
                    alloted = existingDoctor.getAlloted();
                }
            }

            Doctor doctor = new Doctor(
                    request.getParameter("name"),
                    request.getParameter("specialization"),
                    alloted
            );

            if (id == null || id.trim().isEmpty()) {
                doctorDao.save(doctor);
            } else {
                doctor.setId(Integer.parseInt(id));
                doctorDao.update(doctor);
            }

            response.sendRedirect(request.getContextPath() + "/doctors");
        } catch (NumberFormatException exception) {
            throw new ServletException("Invalid doctor id", exception);
        } catch (SQLException exception) {
            throw new ServletException("Unable to save doctor", exception);
        }
    }

    private void listDoctors(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute("doctors", doctorDao.findAll());
        request.getRequestDispatcher("/WEB-INF/views/doctor-list.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Doctor doctor = doctorDao.findById(id);
        showForm(request, response, doctor);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Doctor doctor)
            throws ServletException, IOException {
        request.setAttribute("doctor", doctor);
        request.getRequestDispatcher("/WEB-INF/views/doctor-form.jsp").forward(request, response);
    }

    private void deleteDoctor(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        doctorDao.delete(id);
        response.sendRedirect(request.getContextPath() + "/doctors");
    }
}
