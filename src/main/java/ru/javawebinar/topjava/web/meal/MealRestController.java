package ru.javawebinar.topjava.web.meal;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController extends AbstractMealController {

    public Meal get(int id) {
        return super.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        return super.create(meal);
    }

    public void delete(int id) {
        super.delete(id, authUserId());
    }

    public void update(Meal meal) {
        super.update(meal, meal.getId());
    }

    public List<MealTo> getMealsBetweenDates(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                             @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        int userId = SecurityUtil.authUserId();

        List<Meal> mealsDateFiltered = service.getMealsBetweenDatesInclusively(startDate, endDate, userId);
        return MealsUtil.getFilteredTos(mealsDateFiltered, authUserCaloriesPerDay(), startTime, endTime);
    }

}