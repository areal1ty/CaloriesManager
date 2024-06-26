package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(MealRestController.class);

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        int userId = authUserId();
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(userId), authUserCaloriesPerDay());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        int userId = authUserId();
        log.info("user {} created {}", userId, meal);
        checkNew(meal);
        return service.create(meal, userId);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        int userId = authUserId();
        log.info("user {} updated {} with id={}", userId, meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, userId);
    }

    public List<MealTo> getBetweenDateTime(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                           @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        int userId = SecurityUtil.authUserId();
        if (startTime == null) {
            startTime = LocalTime.MIN;
        }
        if (endTime == null) {
            endTime = LocalTime.MAX;
        }
        List<Meal> mealsDateFiltered = service.getBetweenDateInclusively(startDate, endDate, userId);
        log.info("get meals of {} between {} {} and {} {}", userId, startDate, startTime, endDate, endTime);
        return MealsUtil.getFilteredTos(mealsDateFiltered, authUserCaloriesPerDay(), startTime, endTime);
    }

}