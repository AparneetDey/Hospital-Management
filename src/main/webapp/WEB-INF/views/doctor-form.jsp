<%@ page import="model.Doctor" %>
<%@ page import="util.HtmlEscaper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Doctor doctor = (Doctor) request.getAttribute("doctor");
    boolean editing = doctor != null;
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= editing ? "Edit Doctor" : "Add Doctor" %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
<main class="page narrow">
    <nav class="nav">
        <a href="<%= request.getContextPath() %>/patients">Patients</a>
        <a class="active" href="<%= request.getContextPath() %>/doctors">Doctors</a>
    </nav>

    <p class="eyebrow">Hospital Management</p>
    <h1><%= editing ? "Edit Doctor" : "Add Doctor" %></h1>

    <form method="post" action="<%= request.getContextPath() %>/doctors">
        <% if (editing) { %>
        <input type="hidden" name="id" value="<%= doctor.getId() %>">
        <% } %>

        <label for="name">Name</label>
        <input id="name" name="name" type="text" required
               value="<%= editing ? HtmlEscaper.escape(doctor.getName()) : "" %>">

        <label for="specialization">Specialization</label>
        <input id="specialization" name="specialization" type="text" required
               value="<%= editing ? HtmlEscaper.escape(doctor.getSpecialization()) : "" %>">

        <% if (editing) { %>
        <p class="field-note">Alloted patient ID: <%= HtmlEscaper.escape(doctor.getAlloted()) %></p>
        <% } %>

        <div class="form-actions">
            <button type="submit"><%= editing ? "Update Doctor" : "Save Doctor" %></button>
            <a href="<%= request.getContextPath() %>/doctors">Cancel</a>
        </div>
    </form>
</main>
</body>
</html>
