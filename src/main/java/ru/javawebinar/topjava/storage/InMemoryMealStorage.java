package ru.javawebinar.topjava.storage;
import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class InMemoryMealStorage implements MealStorage {
    private static final Logger log = getLogger(InMemoryMealStorage.class);
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::create);
    }

    @Override
    public Meal create(Meal m) {
        if (m.isNotExist()) {
            m.setUuid(counter.incrementAndGet());
            storage.put(m.getUuid(), m);
            return m;
        }
        return storage.computeIfPresent(m.getUuid(), (uuid, olderMeal) -> m);
    }

    @Override
    public Meal read(int uuid) {
        log.info("read");
        return storage.get(uuid);
    }

    @Override
    public List<Meal> readAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(int uuid) {
        log.info("remove {}", read(uuid));
        return storage.remove(uuid) != null;
    }

}
