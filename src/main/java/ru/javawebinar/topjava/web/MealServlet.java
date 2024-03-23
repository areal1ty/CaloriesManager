package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.InMemoryMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.uuidGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MealServlet extends HttpServlet {
    private static final String INSERT_OR_EDIT = "WEB-INF/mealsEdit.jsp";
    private static final String VIEW = "WEB-INF/mealsView.jsp";
    private static final String LIST = "WEB-INF/meals.jsp";
    private MealStorage storage;

    @Override
    public void init() throws ServletException {
        storage = new InMemoryMealStorage();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String uuid= request.getParameter("id");
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");

        Meal meal = new Meal(LocalDateTime.parse(dateTime), description, Integer.parseInt(calories));

        if (uuid.isEmpty()) {
            storage.create(meal);
        } else {
            meal.setId(Integer.parseInt(uuid));
            storage.update(meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        storage.create(new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        storage.create(new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        storage.create(new Meal( 3, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        storage.create(new Meal( 4, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        storage.create(new Meal( 5, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        storage.create(new Meal( 6, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        storage.create(new Meal(  7, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
        String forward;
        String action = (request.getParameter("action") == null ? "" : request.getParameter("action"));
        String uuid= request.getParameter("id");
        uuidGenerator generator = new uuidGenerator();
        Meal meal;
        switch (action) {
            case "delete": {
                storage.delete(Integer.parseInt(uuid));
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
                meal = storage.read(Integer.parseInt(uuid));
                break;
            }
            case "edit": {
                forward = INSERT_OR_EDIT;
                meal = storage.read(Integer.parseInt(uuid));
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
