package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        Objects.requireNonNull(mealsTo).forEach(System.out::println);
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumOfCaloriesByDay = new HashMap<>();
        Map<LocalDate, List<UserMeal>> mealsByDate = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            sumOfCaloriesByDay.merge(mealDate, meal.getCalories(), Integer::sum);
            mealsByDate.computeIfAbsent(mealDate, k -> new ArrayList<>()).add(meal);
        }
        List<UserMealWithExcess> filteredMeals = new ArrayList<>();
        for (LocalDate date : sumOfCaloriesByDay.keySet()) {
            int calories = sumOfCaloriesByDay.get(date);
            boolean isExceed = calories > caloriesPerDay;
            for (UserMeal meal : mealsByDate.getOrDefault(date, Collections.emptyList())) {
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    filteredMeals.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExceed));
                }
            }
        }
        return filteredMeals;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.mapping(Function.identity(), Collectors.toList())))
                .entrySet().stream()
                .flatMap(entry -> {
                    int calories = entry.getValue().stream().mapToInt(UserMeal::getCalories).sum();
                    boolean isExceed = calories > caloriesPerDay;
                    return entry.getValue().stream()
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExceed));
                })
                .collect(Collectors.toList());
    }
}
