package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    Meal save(Meal m);

    Meal read(int id);

    List<Meal> readAll();

    boolean delete(int id);
}
