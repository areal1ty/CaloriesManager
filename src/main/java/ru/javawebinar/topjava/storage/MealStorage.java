package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    Meal create(Meal m);
    Meal read(int uuid);
    List<Meal> readAll();
    Meal update(Meal m);
    void delete(int uuid);
}
