package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.InMemoryMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String INSERT_OR_EDIT = "/mealsEdit.jsp";
    private static final String LIST = "/meals.jsp";
    private MealStorage storage;

    @Override
    public void init() {
        storage = new InMemoryMealStorage();
        log.info("storage initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String uuid = request.getParameter("uuid");

        Meal meal = new Meal(uuid.isEmpty() ? null : Integer.valueOf(uuid),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "saved {} in storage" : "updated {} in storage", meal);
        storage.save(meal);
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String uuid = request.getParameter("uuid");

        switch (action == null ? "default" : action) {
            case "delete":
                log.info("Meal with uuid = {} deleted", uuid);
                storage.delete(getUuid(request));
                response.sendRedirect("meals");
                break;
            case "save":
            case "edit":
                final Meal meal = "save".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1) :
                        storage.read(getUuid(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher(INSERT_OR_EDIT).forward(request, response);
                break;
            case "default":
            default:
                request.setAttribute("meals", MealsUtil.filteredByStreams(storage.readAll(), LocalTime.MIN, LocalTime.MAX, MealsUtil.MAX_CALORIES));
                request.getRequestDispatcher(LIST).forward(request, response);
                log.info("view all meals in storage");
                break;
        }
    }

    private int getUuid(HttpServletRequest request) {
        return Integer.parseInt(Objects.requireNonNull(request.getParameter("uuid")));
    }

}
