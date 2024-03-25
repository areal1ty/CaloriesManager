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
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal m) {
        if (m.isNew()) {
            m.setid(counter.incrementAndGet());
            storage.put(m.getid(), m);
            log.info("{} saved", m);
            return m;
        }
        return storage.computeIfPresent(m.getid(), (id, olderMeal) -> m);
    }

    @Override
    public Meal read(int id) {
        log.info("read {}", id);
        return storage.get(id);
    }

    @Override
    public List<Meal> readAll() {
        log.info("real all elements in storage");
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(int id) {
        log.info("remove {}", id);
        return storage.remove(id) != null;
    }

}
