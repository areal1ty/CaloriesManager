<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <jsp:useBean uuid="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <title>Еда ${meal.description}</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<div class="form-wrapper">
    <h2>${meal.description}</h2>
    <div>dateTime:
        ${TimeUtil.toString(meal.dateTime)}
    </div>

    <div>Calories:
        ${meal.calories}
    </div>
</div>
</body>
</html>