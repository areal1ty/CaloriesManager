<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Главная страница</a></h3>
    <hr/>
    <h1>Meals</h1>
    <a href="meals?action=create">Добавить</a>
    <br><br>
    <form method="get" action="meals">
        <input type="hidden" name="action" value="getAllFiltered">
        От даты (включая)
        <div>
            Дата <input type="date" name="dateBegin" id="dateBegin" value="${param['dateBegin']}">
        </div>

        До даты (включая)
        <div>
            Дата <input type="date" name="dateEnd" value="${param['dateEnd']}">
        </div>

        От времени (включая)
        <div>
            Время <input type="time" name="timeBegin" value="${param['timeBegin']}">
        </div>
        До времени (исключая)
        <div>
            Время <input type="time" name="timeEnd" value="${param['timeEnd']}">
        </div>

        <button type="submit">Отфильтровать</button>
    </form>
    <br><br>
    <table border="1" cellpadding="10" cellspacing="0">
        <thead>
        <tr>
            <th>Дата/Время</th>
            <th>Описание</th>
            <th>Калории</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${requestScope.meals}" var="meal">
            <c:set var="mealTo" value="${meal}"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        ${TimeUtil.toString(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=edit&id=${meal.id}">Изменить</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Удалить</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>