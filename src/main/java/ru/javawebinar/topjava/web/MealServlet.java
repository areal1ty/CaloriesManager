package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MapStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.IdGenerator;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MealServlet extends HttpServlet {
    private static final String INSERT_OR_EDIT = "WEB-INF/mealsEdit.jsp";
    private static final String VIEW = "WEB-INF/mealsView.jsp";
    private static final String LIST = "WEB-INF/meals.jsp";
    private MealStorage storage;

    @Override
    public void init() throws ServletException {
        storage = new MapStorage();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");

        Meal meal = new Meal(LocalDateTime.parse(dateTime), description, Integer.parseInt(calories));

        if (id.isEmpty()) {
            storage.create(meal);
        } else {
            meal.setId(Integer.parseInt(id));
            storage.update(meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward;
        String action = (request.getParameter("action") == null ? "" : request.getParameter("action"));
        String id = request.getParameter("id");
        IdGenerator generator = new IdGenerator();
        Meal meal;
        switch (action) {
            case "delete": {
                storage.delete(Integer.parseInt(id));
                response.sendRedirect("meals");
                return;
            }
            case "create": {
                forward = INSERT_OR_EDIT;
                meal = new Meal(generator.nextId(), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0);
                break;
            }
            case "view": {
                forward = VIEW;
                meal = storage.read(Integer.parseInt(id));
                break;
            }
            case "edit": {
                forward = INSERT_OR_EDIT;
                meal = storage.read(Integer.parseInt(id));
                break;
            }
            default:
                List<Meal> meals = storage.readAll();
                request.setAttribute("meals", MealsUtil.filteredByStreams(meals,
                        LocalTime.MIN, LocalTime.MAX, MealsUtil.MAX_CALORIES));
                request.getRequestDispatcher(LIST).forward(request, response);
                return;
        }
        request.setAttribute("meal", meal);
        request.getRequestDispatcher(forward).forward(request, response);
    }
}
