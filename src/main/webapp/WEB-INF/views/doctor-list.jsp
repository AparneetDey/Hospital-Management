<%@ page import="java.util.List" %>
<%@ page import="model.Doctor" %>
<%@ page import="util.HtmlEscaper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Doctors</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
<main class="page">
    <nav class="nav">
        <a href="<%= request.getContextPath() %>/patients">Patients</a>
        <a class="active" href="<%= request.getContextPath() %>/doctors">Doctors</a>
    </nav>

    <div class="topbar">
        <div>
            <p class="eyebrow">Hospital Management</p>
            <h1>Doctors</h1>
        </div>
        <a class="button" href="<%= request.getContextPath() %>/doctors?action=new">Add Doctor</a>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Specialization</th>
            <th>Alloted</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
            if (doctors == null || doctors.isEmpty()) {
        %>
        <tr>
            <td colspan="5" class="empty">No doctors found.</td>
        </tr>
        <%
            } else {
                for (Doctor doctor : doctors) {
        %>
        <tr>
            <td><%= doctor.getId() %></td>
            <td><%= HtmlEscaper.escape(doctor.getName()) %></td>
            <td><%= HtmlEscaper.escape(doctor.getSpecialization()) %></td>
            <td><%= doctor.getAlloted() == null || doctor.getAlloted().trim().isEmpty() ? "Not assigned" : HtmlEscaper.escape(doctor.getAlloted()) %></td>
            <td class="actions">
                <a href="<%= request.getContextPath() %>/doctors?action=edit&id=<%= doctor.getId() %>">Edit</a>
                <a class="danger" href="<%= request.getContextPath() %>/doctors?action=delete&id=<%= doctor.getId() %>"
                   onclick="return confirm('Delete this doctor?');">Delete</a>
            </td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>
</main>
</body>
</html>
