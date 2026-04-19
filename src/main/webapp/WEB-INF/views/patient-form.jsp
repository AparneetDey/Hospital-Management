<%@ page import="model.Patient" %>
<%@ page import="model.Doctor" %>
<%@ page import="java.util.List" %>
<%@ page import="util.HtmlEscaper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Patient patient = (Patient) request.getAttribute("patient");
    List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
    boolean editing = patient != null;
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= editing ? "Edit Patient" : "Add Patient" %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
<main class="page narrow">
    <nav class="nav">
        <a class="active" href="<%= request.getContextPath() %>/patients">Patients</a>
        <a href="<%= request.getContextPath() %>/doctors">Doctors</a>
    </nav>

    <p class="eyebrow">Hospital Management</p>
    <h1><%= editing ? "Edit Patient" : "Add Patient" %></h1>

    <% if (request.getAttribute("errorMessage") != null) { %>
    <p class="error"><%= request.getAttribute("errorMessage") %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/patients">
        <% if (editing) { %>
        <input type="hidden" name="id" value="<%= patient.getId() %>">
        <% } %>

        <label for="name">Name</label>
        <input id="name" name="name" type="text" required
               value="<%= editing ? HtmlEscaper.escape(patient.getName()) : "" %>">

        <label for="age">Age</label>
        <input id="age" name="age" type="number" min="0" max="130" required
               value="<%= editing ? patient.getAge() : "" %>">

        <label for="disease">Disease</label>
        <input id="disease" name="disease" type="text" required
               value="<%= editing ? HtmlEscaper.escape(patient.getDisease()) : "" %>">

        <label for="doctorId">Doctor</label>
        <select id="doctorId" name="doctorId">
            <option value="">No doctor assigned</option>
            <%
                if (doctors != null) {
                    for (Doctor doctor : doctors) {
                        boolean selected = editing
                                && patient.getDoctorId() != null
                                && patient.getDoctorId() == doctor.getId();
            %>
            <option value="<%= doctor.getId() %>" <%= selected ? "selected" : "" %>>
                <%= doctor.getId() %> - <%= HtmlEscaper.escape(doctor.getName()) %>
                (<%= HtmlEscaper.escape(doctor.getSpecialization()) %>)
            </option>
            <%
                    }
                }
            %>
        </select>

        <div class="form-actions">
            <button type="submit"><%= editing ? "Update Patient" : "Save Patient" %></button>
            <a href="<%= request.getContextPath() %>/patients">Cancel</a>
        </div>
    </form>
</main>
</body>
</html>
