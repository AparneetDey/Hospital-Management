<%@ page import="java.util.List" %>
<%@ page import="model.Patient" %>
<%@ page import="util.HtmlEscaper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hospital Management</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
<main class="page">
    <nav class="nav">
        <a class="active" href="<%= request.getContextPath() %>/patients">Patients</a>
        <a href="<%= request.getContextPath() %>/doctors">Doctors</a>
    </nav>

    <div class="topbar">
        <div>
            <p class="eyebrow">Hospital Management</p>
            <h1>Patients</h1>
        </div>
        <a class="button" href="<%= request.getContextPath() %>/patients?action=new">Add Patient</a>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Age</th>
            <th>Disease</th>
            <th>Doctor ID</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Patient> patients = (List<Patient>) request.getAttribute("patients");
            if (patients == null || patients.isEmpty()) {
        %>
        <tr>
            <td colspan="6" class="empty">No patients found.</td>
        </tr>
        <%
            } else {
                for (Patient patient : patients) {
        %>
        <tr>
            <td><%= patient.getId() %></td>
            <td><%= HtmlEscaper.escape(patient.getName()) %></td>
            <td><%= patient.getAge() %></td>
            <td><%= HtmlEscaper.escape(patient.getDisease()) %></td>
            <td><%= patient.getDoctorId() == null ? "Not assigned" : patient.getDoctorId() %></td>
            <td class="actions">
                <a href="<%= request.getContextPath() %>/patients?action=edit&id=<%= patient.getId() %>">Edit</a>
                <a class="danger" href="<%= request.getContextPath() %>/patients?action=delete&id=<%= patient.getId() %>"
                   onclick="return confirm('Delete this patient?');">Delete</a>
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
