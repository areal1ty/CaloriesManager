<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Meal</title>
</head>
<body>
<section>
    <h3><a href="index.html">Главная страница</a></h3>
    <hr>
    <h2>${param.action == 'create' ? 'Create' : 'Edit'}</h2>
    <jsp:useBean uuid="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <form method="post" action="meals">
        <input type="hidden" name="id" value="${meal.id}">
        <dl>
            <dt>Дата/Время:</dt>
            <dd><input type="datetime-local" value="${meal.dateTime}" name="dateTime" required></dd>
        </dl>
        <dl>
            <dt>Описание:</dt>
            <dd><input type="text" value="${meal.description}" size=40 name="description" required></dd>
        </dl>
        <dl>
            <dt>Калории:</dt>
            <dd><input type="number" value="${meal.calories}" name="calories" required></dd>
        </dl>
        <button type="submit">Сохранить</button>
        <button onclick="window.history.back()" type="button">Отменить</button>
    </form>
</section>
</body>
</html>